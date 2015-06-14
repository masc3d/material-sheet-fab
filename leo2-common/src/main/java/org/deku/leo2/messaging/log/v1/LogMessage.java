package org.deku.leo2.messaging.log.v1;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;

import java.io.Serializable;
import java.util.logging.LogRecord;

/**
 * Created by masc on 16.04.15.
 */
public class LogMessage implements Serializable {
    public static final String LOG_QUEUE_NAME = "leo2.log.v1";

//    private Object[] mArgumentArray;
//    private Level mLevel;
    private String mLoggerName;
    private String mThreadName;
    private String mMessage;
    private long mTimestamp;

    public LogMessage() {
    }
    public LogMessage(LoggingEvent le) {
//        mArgumentArray = le.getArgumentArray();
//        mLevel = le.getLevel();
        mLoggerName = le.getLoggerName();
        mThreadName = le.getThreadName();
        mMessage = le.getMessage();
        mTimestamp = le.getTimeStamp();
    }
//
//    public Object[] getArgumentArray() {
//        return mArgumentArray;
//    }
//
//    public Level getLevel() {
//        return mLevel;
//    }

    public String getLoggerName() {
        return mLoggerName;
    }

    public String getThreadName() {
        return mThreadName;
    }

    public String getMessage() {
        return mMessage;
    }

    public long getTimestamp() {
        return mTimestamp;
    }
}
