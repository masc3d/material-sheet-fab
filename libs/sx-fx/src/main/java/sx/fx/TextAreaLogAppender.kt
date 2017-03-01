package sx.fx

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.PatternLayout
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.scene.control.TextArea
import org.slf4j.LoggerFactory

/**

 * @author Russell Shingleton @oclc.org>
 */
class TextAreaLogAppender(
        /** Text are to attach to */
        val textArea: TextArea,
        /** Maximum number of lines */
        var maxLines: Int = 500) : AppenderBase<ILoggingEvent>() {

    private var patternLayout: PatternLayout = PatternLayout()
    private var lines: Int = 0
    private var buffer: StringBuilder = StringBuilder()

    init {
        //this.patternLayout.setPattern("%-5level [%thread]: %message%n")
        this.patternLayout.pattern = "%date{HH:mm:ss.SSS} %-5level [%thread]: %message%n"
        this.patternLayout.context = LoggerFactory.getILoggerFactory() as LoggerContext
        this.patternLayout.start()

        this.textArea.textProperty().addListener { _, _, _ ->
            // TODO: the first time this fires, it doesn't work properly, text area won't scroll to end
            this.textArea.scrollTop = Double.MAX_VALUE
        }
    }

    /**
     * Append
     */
    override fun append(loggingEvent: ILoggingEvent) {
        val message = this.patternLayout.doLayout(loggingEvent)

        synchronized(this.buffer) {
            val lineBreak = '\n'

            buffer.append(message)
            this.lines += message.count {
                it == lineBreak
            }

            while (this.lines > this.maxLines) {
                // Truncate first line
                val crlfIndex = buffer.indexOf(lineBreak)
                if (crlfIndex > 0)
                    buffer.delete(0, crlfIndex + 1)
                this.lines -= 1
            }

            Platform.runLater {
                this.textArea.text = ""
                // Make sure text changed listener fires.
                this.textArea.appendText(this.buffer.toString())
            }
        }
    }
}