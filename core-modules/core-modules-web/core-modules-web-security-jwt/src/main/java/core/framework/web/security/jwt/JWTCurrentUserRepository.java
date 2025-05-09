package core.framework.web.security.jwt;

import core.framework.web.security.AbstractCurrentUserRepository;
import core.framework.web.security.CurrentUser;
import core.framework.web.security.CurrentUserVerifyCustomize;
import core.framework.web.security.jwt.configuration.properties.JWTProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * @author ebin
 */
public class JWTCurrentUserRepository extends AbstractCurrentUserRepository {
    private final String issuer;
    private final long expirationSecond;
    private final SecretKey key;

    public JWTCurrentUserRepository(Environment environment,
                                    JWTProperties jwtProperties,
                                    CurrentUserVerifyCustomize currentUserVerifyCustomize) {
        super(currentUserVerifyCustomize);
        this.issuer = Optional.ofNullable(jwtProperties.getIssuer()).orElse(environment.getProperty("spring.application.name"));
        assert !StringUtils.isEmpty(this.issuer);
        this.expirationSecond = jwtProperties.getExpirationSecond();
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected String doSave(CurrentUser currentUser, Map<String, Object> currentUserMap) {
        return generateToken(currentUser.getId(), currentUserMap);
    }

    @Override
    protected Map<String, Object> doLoad(String identity) {
        Claims claims;
        try {
            claims = parseToken(identity);
        } catch (JwtException e) {
            return null;
        }
        if (claims.getIssuer().equals(issuer) && claims.getExpiration().after(new Date())) {
            return claims;
        }
        return null;
    }

    @Override
    public void destroy(String identity) {

    }

    private String generateToken(String subject,
                                 Map<String, Object> customerClaims) {
        Instant now = Instant.now();
        return Jwts.builder()
            .claims(customerClaims)
            .issuer(issuer)
            .subject(subject)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(expirationSecond)))
            .signWith(key)
            .compact();
    }

    private Claims parseToken(String token) throws JwtException {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
