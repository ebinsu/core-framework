package core.framework.mysql.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(MysqlProcessorRegistrar.class)
public class MysqlDataSourceConfiguration {
}