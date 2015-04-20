package org.deku.leo2.messaging.log.v2;

import java.io.Serializable;
import java.util.logging.LogRecord;

/**
 * Created by masc on 16.04.15.
 */
public class LogMessage implements Serializable {
    public static String LOG_QUEUE_NAME = "leo2.log";

    private String mMessage2;

    public LogMessage(LogRecord lr) {
        mMessage2 = lr.getMessage();
    }

    public String getMessage2() {
        return mMessage2;
    }
}
