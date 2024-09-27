package com.example.demo;

import core.framework.namedquery.jpa.HibernateNamedQueryDatasourceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
public class Config {
    @Bean
    public HibernateNamedQueryDatasourceProvider provider() {
        return () -> null;
    }
}
