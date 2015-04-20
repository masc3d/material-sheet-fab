package org.deku.leo2.messaging.log.v1;

import java.io.Serializable;
import java.util.logging.LogRecord;

/**
 * Created by masc on 16.04.15.
 */
public class LogMessage implements Serializable {
    public static String LOG_QUEUE_NAME = "leo2.log";

    private String mMessage;

    public LogMessage(LogRecord lr) {
        mMessage = lr.getMessage();
    }

    public String getMessage() {
        return mMessage;
    }
}
