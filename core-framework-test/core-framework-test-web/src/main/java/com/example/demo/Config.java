package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
public class Config {
    @Bean
    public DefaultIdentityManager schedule() {
        return new DefaultIdentityManager();
    }
}
