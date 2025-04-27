package core.framework.web.security.jwt;

import core.framework.web.security.AuthStrategy;
import core.framework.web.security.PrincipalDetail;
import core.framework.web.security.jwt.configuration.properties.JWTProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author ebin
 */
public class JWTAuthStrategy implements AuthStrategy {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String PERMISSION_CODES = "PERMISSION_CODES";
    private static final String NAME = "NAME";
    private static final String DISPLAY_NAME = "DISPLAY_NAME";
    private static final String CLIENT_IP = "CLIENT_IP";

    private final String issuer;
    private final long expirationSecond;
    private final SecretKey key;

    public JWTAuthStrategy(Environment environment, JWTProperties jwtProperties) {
        this.issuer = Optional.ofNullable(jwtProperties.getIssuer()).orElse(environment.getProperty("spring.application.name"));
        assert !StringUtils.isEmpty(this.issuer);
        this.expirationSecond = jwtProperties.getExpirationSecond();
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    protected String generateToken(String subject,
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

    @Override
    public String authenticate(HttpServletRequest request, HttpServletResponse response, PrincipalDetail principalDetail) {
        Map<String, Object> customerClaims = new HashMap<>(principalDetail.getAttributes().size() + 3);
        customerClaims.putAll(principalDetail.getAttributes());
        customerClaims.put(PERMISSION_CODES, principalDetail.getPermissionCodes());
        customerClaims.put(NAME, principalDetail.getName());
        customerClaims.put(DISPLAY_NAME, principalDetail.getDisplayName());
        customerClaims.put(CLIENT_IP, principalDetail.getClientIP());
        return generateToken(principalDetail.getId(), customerClaims);
    }

    @Override
    public PrincipalDetail load(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER);
        if (token == null || !token.startsWith(BEARER_PREFIX)) {
            return null;
        }
        token = token.substring(BEARER_PREFIX_LENGTH);
        Claims claims;
        try {
            claims = parseToken(token);
        } catch (JwtException e) {
            return null;
        }
        if (claims.getIssuer().equals(issuer)
            && StringUtils.equals(claims.get(CLIENT_IP, String.class), request.getRemoteAddr())
            && claims.getExpiration().after(new Date())) {
            return new PrincipalDetail(
                claims.getSubject(),
                claims.get(NAME, String.class),
                claims.get(DISPLAY_NAME, String.class),
                claims.get(PERMISSION_CODES, List.class),
                claims.get(CLIENT_IP, String.class)
            );
        }
        return null;
    }

    @Override
    public void destroy(HttpServletRequest request, HttpServletResponse response) {

    }
}
