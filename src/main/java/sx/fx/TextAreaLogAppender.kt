package sx.fx

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.PatternLayout
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import javafx.application.Platform
import javafx.scene.control.TextArea
import org.slf4j.LoggerFactory

/**

 * @author Russell Shingleton @oclc.org>
 */
class TextAreaLogAppender(val textArea: TextArea) : AppenderBase<ILoggingEvent>() {
    private var patternLayout: PatternLayout = PatternLayout()
    private var lines: Int = 0
    private var buffer: StringBuilder = StringBuilder()

    /** Maximum number of lines */
    var maxLines: Int = 500

    init {
        //this.patternLayout.setPattern("%-5level [%thread]: %message%n")
        this.patternLayout.pattern = "%date{HH:mm:ss.SSS} %-5level [%thread]: %message%n"
        this.patternLayout.context = LoggerFactory.getILoggerFactory() as LoggerContext
        this.patternLayout.start()
    }

    /**
     * Append
     */
    override fun append(loggingEvent: ILoggingEvent) {
        val message = this.patternLayout.doLayout(loggingEvent)

        synchronized(this.buffer) {
            buffer.append(message)
            this.lines++

            if (this.lines > this.maxLines) {
                // Truncate first line
                var crlfIndex = buffer.indexOf("\n")
                if (crlfIndex > 0)
                    buffer.delete(0, crlfIndex + 1)
                this.lines--
            }
        }

        // Append formatted message to text area using the Thread.
        try {
            Platform.runLater({
                try {
                    this.textArea.text = this.buffer.toString()
                    this.textArea.scrollTop = Double.MAX_VALUE;
                } catch (e: Exception) {
                    // Ignore exceptions
                }
            })
        } catch (e: Exception) {
            // Ignore exceptions
        }
    }
}