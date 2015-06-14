package org.deku.leo2.messaging.log.v1;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.slf4j.helpers.MessageFormatter;

import java.io.Serializable;
import java.util.logging.LogRecord;

/**
 * Created by masc on 16.04.15.
 */
public class LogMessage implements Serializable {
    private static final long serialVersionUID = -8027400236775552276L;

    public static final String LOG_QUEUE_NAME = "leo2.log.v1";

    private Level mLevel;
    private String mLoggerName;
    private String mThreadName;
    private String mMessage;
    private long mTimestamp;

    public LogMessage() {
    }
    public LogMessage(LoggingEvent le) {
        mLevel = le.getLevel();
        mLoggerName = le.getLoggerName();
        mThreadName = le.getThreadName();
        mMessage = (le.getArgumentArray() != null) ?
                MessageFormatter.arrayFormat(le.getMessage(), le.getArgumentArray()).getMessage() : le.getMessage();
        mTimestamp = le.getTimeStamp();
    }

    public Level getLevel() {
        return mLevel;
    }

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
