package org.deku.leo2.node;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import org.deku.leo2.messaging.activemq.ActiveMQContext;
import org.deku.leo2.messaging.log.LogAppender;
import org.slf4j.LoggerFactory;
import sx.Disposable;
import sx.LazyInstance;

/**
 * Created by masc on 03.07.15.
 */
public class LogConfiguration implements Disposable {
    //region Singleton
    private static final LazyInstance<LogConfiguration> mInstance = new LazyInstance<>(LogConfiguration::new);
    public static LogConfiguration instance() {
        return mInstance.get();
    }
    //endregion

    private Logger mRootLogger;
    private LoggerContext mLoggerContext;

    private LogAppender mJmsLogAppender;
    private RollingFileAppender mFileAppender;

    /**
     * c'tor
     */
    private LogConfiguration() {
        mRootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        mLoggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        // File appender
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(mLoggerContext);
        encoder.setPattern("%d %r %thread %level - %msg%n");
        encoder.start();

        mFileAppender = new RollingFileAppender();
        mFileAppender.setContext(mLoggerContext);
        mFileAppender.setFile(LocalStorage.instance().getLogFile().toString());
        mFileAppender.setEncoder(encoder);

        TimeBasedRollingPolicy rollingPolicy = new TimeBasedRollingPolicy();
        rollingPolicy.setContext(mLoggerContext);
        rollingPolicy.setParent(mFileAppender);
        rollingPolicy.setMaxHistory(10);
        rollingPolicy.setFileNamePattern(mFileAppender.rawFileProperty() + "-%d{yyyy-MM-dd}");
        rollingPolicy.start();
        mFileAppender.setRollingPolicy(rollingPolicy);
        mFileAppender.setTriggeringPolicy(rollingPolicy);

        // Jms appender
        if (App.instance().getProfile() == App.PROFILE_CLIENT_NODE) {
            // Setup message log appender
            mJmsLogAppender = new LogAppender(ActiveMQContext.instance());
            mJmsLogAppender.setContext(mLoggerContext);
        }
    }

    public void initialize() {
        // Initialize file appender
        if (mFileAppender != null) {
            mFileAppender.start();
            mRootLogger.addAppender(mFileAppender);
        }

        // Initialize jms appender
        if (mJmsLogAppender != null) {
            mJmsLogAppender.start();
            mRootLogger.addAppender(mJmsLogAppender);
        }
    }

    @Override
    public void dispose() {
        if (mJmsLogAppender != null) {
            mJmsLogAppender.stop();
            mRootLogger.detachAppender(mJmsLogAppender);
        }

        if (mFileAppender != null) {
            mFileAppender.stop();
            mRootLogger.detachAppender(mFileAppender);
        }
    }
}
