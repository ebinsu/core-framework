package core.framework.mysql.configuration;

import com.mysql.cj.conf.PropertyDefinitions;
import com.mysql.cj.conf.PropertyKey;
import com.zaxxer.hikari.HikariDataSource;
import core.framework.mysql.MySQLQueryInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author ebin
 */
public class MysqlDataSourceBeanPostProcessor implements BeanPostProcessor {
    public static final String BEAN_NAME = "mysqlDataSourceBeanPostProcessor";

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof HikariDataSource dataSource) {
            if (dataSource.getJdbcUrl().startsWith("jdbc:mysql")) {
                // disable unnecessary mysql connection cleanup thread to reduce overhead, refer to PropertyDefinitions.SYSP_disableAbandonedConnectionCleanup
                System.setProperty(PropertyDefinitions.SYSP_disableAbandonedConnectionCleanup, "true");

                dataSource.setTransactionIsolation("TRANSACTION_READ_COMMITTED");
                dataSource.setAutoCommit(false);
                dataSource.addDataSourceProperty(PropertyKey.queryInterceptors.getKeyName(), MySQLQueryInterceptor.class.getName());
            }
        }
        return bean;
    }
}