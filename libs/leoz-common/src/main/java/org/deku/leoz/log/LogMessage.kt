package org.deku.leoz.log

import ch.qos.logback.classic.pattern.ThrowableProxyConverter
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.CoreConstants
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
    public var logEntries: Array<LogEntry> = arrayOf()

    class LogEntry {
        public constructor() {
        }

        public constructor(loggingEvent: LoggingEvent) {
            val it = loggingEvent
            this.level = it.level.toString()
            this.loggerName = it.loggerName
            this.threadName = it.threadName
            this.message = if ((it.argumentArray != null))
                MessageFormatter.arrayFormat(it.message, it.argumentArray).message
            else
                it.message
            this.timestamp = it.timeStamp

            if (loggingEvent.throwableProxy != null) {
                val tc = ThrowableProxyConverter()
                tc.start()
                this.message += CoreConstants.LINE_SEPARATOR + tc.convert(loggingEvent)
            }
        }

        public var level: String = ""
        public var loggerName: String = ""
        public var threadName: String = ""
        public var message: String = ""
        public var timestamp: Long = 0
    }

    public constructor() { }

    public constructor(nodeId: Int?, nodeKey: String, logEntries: Array<LogEntry>) {
        this.nodeId = nodeId
        this.nodeKey = nodeKey
        this.logEntries = logEntries
    }
}
