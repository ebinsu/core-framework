package core.framework.web.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.framework.json.JSONMapper;
import core.framework.web.exception.ErrorCodeRuntimeExceptionHandler;
import core.framework.web.exception.BindExceptionHandler;
import core.framework.web.exception.ConstraintViolationExceptionHandler;
import core.framework.web.exception.ExceptionHandlerCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Configuration
@EnableConfigurationProperties(CORSProperties.class)
@Import(DefaultWebMvcConfigurer.class)
public class WebMvcConfiguration {
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return JSONMapper.OBJECT_MAPPER;
    }

    @Bean
    public ExceptionHandlerCustomizer exceptionHandlerCustomizer() {
        return () -> List.of(
                new ErrorCodeRuntimeExceptionHandler(),
                new ConstraintViolationExceptionHandler(),
                new BindExceptionHandler()
        );
    }
}
