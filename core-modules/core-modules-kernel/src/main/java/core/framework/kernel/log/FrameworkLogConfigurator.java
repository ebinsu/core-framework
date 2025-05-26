package core.framework.kernel.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * @author ebin
 */
public final class FrameworkLogConfigurator {
    public static final String PATTERN = "%d{yyy-MM-dd HH:mm:ss,GMT+8} %X{context.action} %X{trace_id} %X{span_id} %p %t %logger %m%n";

    private FrameworkLogConfigurator() {
    }

    public static void configure() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();

        ConsoleAppender<ILoggingEvent> stdOutAppender = stdOutAppender(context);

        ConsoleAppender<ILoggingEvent> stdErrAppender = stdErrAppender(context);

        Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(stdOutAppender);
        rootLogger.addAppender(stdErrAppender);
    }

    private static ConsoleAppender<ILoggingEvent> stdErrAppender(LoggerContext context) {
        ConsoleAppender<ILoggingEvent> stdErrAppender = new ConsoleAppender<>();
        stdErrAppender.setContext(context);
        stdErrAppender.setName("STDERR");
        stdErrAppender.setTarget("System.err");

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern(PATTERN);
        encoder.setCharset(StandardCharsets.UTF_8);
        encoder.start();
        stdErrAppender.setEncoder(encoder);

        MultiLevelFilter filter = new MultiLevelFilter();
        filter.setContext(context);
        filter.setLevels("WARN,ERROR");
        filter.setOnMatch(FilterReply.ACCEPT);
        filter.setOnMismatch(FilterReply.DENY);
        filter.start();
        stdErrAppender.addFilter(filter);
        stdErrAppender.start();
        return stdErrAppender;
    }

    private static ConsoleAppender<ILoggingEvent> stdOutAppender(LoggerContext context) {
        ConsoleAppender<ILoggingEvent> stdoutAppender = new ConsoleAppender<>();
        stdoutAppender.setContext(context);
        stdoutAppender.setName("STDOUT");
        stdoutAppender.setTarget("System.out");

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern(PATTERN);
        encoder.setCharset(StandardCharsets.UTF_8);
        encoder.start();
        stdoutAppender.setEncoder(encoder);

        LevelFilter filter = new LevelFilter();
        filter.setContext(context);
        filter.setLevel(Level.INFO);
        filter.setOnMatch(FilterReply.ACCEPT);
        filter.setOnMismatch(FilterReply.DENY);
        filter.start();
        stdoutAppender.addFilter(filter);
        stdoutAppender.start();
        return stdoutAppender;
    }
}
