package core.framework.mysql.configuration;

import com.mysql.cj.conf.PropertyKey;
import com.zaxxer.hikari.HikariDataSource;
import core.framework.mysql.MySQLQueryInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author ebin
 */
public class MysqlDataSourceBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof HikariDataSource dataSource) {
            if (dataSource.getJdbcUrl().startsWith("jdbc:mysql")) {
                dataSource.addDataSourceProperty(PropertyKey.queryInterceptors.getKeyName(), MySQLQueryInterceptor.class.getName());
            }
        }
        return bean;
    }
}
