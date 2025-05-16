package app;

import core.framework.kernel.async.Executor;
import core.framework.kernel.async.ExecutorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executors;


@SpringBootApplication
public class SpringMvcDemoApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringMvcDemoApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringMvcDemoApplication.class, args);
    }

    @Bean
    public Executor executor() {
        return new ExecutorImpl(Executors.newVirtualThreadPerTaskExecutor());
    }
}
