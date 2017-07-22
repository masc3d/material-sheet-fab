package org.deku.leoz.log

import ch.qos.logback.classic.pattern.ThrowableProxyConverter
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.CoreConstants
import org.deku.leoz.bundle.BundleType
import org.slf4j.helpers.MessageFormatter
import sx.io.serialization.Serializable

/**
 * Log message
 * Created by masc on 16.04.15.
 */
@Serializable(0x20881e385e22b8)
class LogMessage(
        var nodeType: String = BundleType.LeozNode.value,
        var nodeUid: String = "",
        var logEntries: Array<LogEntry> = arrayOf()) {

    @Serializable
    class LogEntry {
        constructor() {}

        constructor(loggingEvent: LoggingEvent) {
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

        var level: String = ""
        var loggerName: String = ""
        var threadName: String = ""
        var message: String = ""
        var timestamp: Long = 0
    }
}
