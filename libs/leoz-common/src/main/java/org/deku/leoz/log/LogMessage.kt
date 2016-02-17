package org.deku.leoz.log

import ch.qos.logback.classic.spi.LoggingEvent
import org.slf4j.helpers.MessageFormatter

import java.io.Serializable

/**
 * Log message
 * Created by masc on 16.04.15.
 */
public class LogMessage : Serializable {
    companion object {
        private const val serialVersionUID = -8027400236775552276L
    }

    public var nodeId: Int? = null
    public var nodeKey: String = ""
    public var level: String = ""
    public var loggerName: String = ""
    public var threadName: String = ""
    public var message: String = ""
    public var timestamp: Long = 0

    public constructor() { }

    public constructor(nodeId: Int?, nodeKey: String, loggingEvent: LoggingEvent) {
        this.nodeId = nodeId
        this.nodeKey = nodeKey
        this.level = loggingEvent.level.toString()
        this.loggerName = loggingEvent.loggerName
        this.threadName = loggingEvent.threadName
        this.message = if ((loggingEvent.argumentArray != null))
            MessageFormatter.arrayFormat(loggingEvent.message, loggingEvent.argumentArray).message
        else
            loggingEvent.message
        this.timestamp = loggingEvent.timeStamp
    }
}
