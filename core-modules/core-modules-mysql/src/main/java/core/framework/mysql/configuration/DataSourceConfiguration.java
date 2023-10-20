package core.framework.mysql.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfiguration {
    @Bean
    public MysqlDataSourceBeanPostProcessor mysqlDataSourceBeanPostProcessor() {
        return new MysqlDataSourceBeanPostProcessor();
    }
//    @Bean
//    @ConditionalOnMissingBean
//    @ConfigurationProperties(prefix = "spring.datasource.hikari")
//    public HikariDataSource dataSource(DataSourceProperties properties) {
//        HikariDataSource dataSource = properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
//        if (StringUtils.hasText(properties.getName())) {
//            dataSource.setPoolName(properties.getName());
//        }
//        dataSource.setDriverClassName(com.mysql.cj.jdbc.Driver.class.getName());
//        dataSource.setTransactionIsolation("TRANSACTION_READ_COMMITTED");
//        dataSource.setAutoCommit(false);
//        dataSource.addDataSourceProperty(PropertyKey.queryInterceptors.getKeyName(), MySQLQueryInterceptor.class.getName());
//        return dataSource;
//    }
}
