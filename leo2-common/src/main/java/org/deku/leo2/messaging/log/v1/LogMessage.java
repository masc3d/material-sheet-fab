package org.deku.leo2.messaging.log.v1;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.slf4j.helpers.MessageFormatter;

import java.io.Serializable;

/**
 * Created by masc on 16.04.15.
 */
public class LogMessage implements Serializable {
    private static final long serialVersionUID = -8027400236775552276L;

    private String mLevel;
    private String mLoggerName;
    private String mThreadName;
    private String mMessage;
    private long mTimestamp;

    public LogMessage() {
    }
    public LogMessage(LoggingEvent le) {
        mLevel = le.getLevel().toString();
        mLoggerName = le.getLoggerName();
        mThreadName = le.getThreadName();
        mMessage = (le.getArgumentArray() != null) ?
                MessageFormatter.arrayFormat(le.getMessage(), le.getArgumentArray()).getMessage() : le.getMessage();
        mTimestamp = le.getTimeStamp();
    }

    public String getLevel() {
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
