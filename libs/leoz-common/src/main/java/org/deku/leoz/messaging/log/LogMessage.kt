package org.deku.leoz.messaging.log

import ch.qos.logback.classic.spi.LoggingEvent
import org.slf4j.helpers.MessageFormatter

import java.io.Serializable

/**
 * Log message
 * Created by masc on 16.04.15.
 */
public class LogMessage : Serializable {
    companion object {
        private val serialVersionUID = -8027400236775552276L
    }

    public var level: String = ""
    public var loggerName: String = ""
    public var threadName: String = ""
    public var message: String = ""
    public var timestamp: Long = 0

    public constructor() { }

    public constructor(le: LoggingEvent) {
        level = le.level.toString()
        loggerName = le.loggerName
        threadName = le.threadName
        message = if ((le.argumentArray != null))
            MessageFormatter.arrayFormat(le.message, le.argumentArray).message
        else
            le.message
        timestamp = le.timeStamp
    }
}
