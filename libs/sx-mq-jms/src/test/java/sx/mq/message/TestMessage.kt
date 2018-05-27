package sx.mq.message

import ch.qos.logback.classic.pattern.ThrowableProxyConverter
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.CoreConstants
import org.slf4j.helpers.MessageFormatter
import sx.io.serialization.Serializable

/**
 * Test message
 * Created by masc on 16.04.15.
 */
@Serializable(0x123456)
class TestMessage(
        var nodeType: String = "",
        var nodeKey: String = "",
        var logEntries: Array<LogEntry> = arrayOf()) {

    @Serializable
    class LogEntry {
        constructor() { }

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
