package org.deku.leo2.boot.fx

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
public class TextAreaLogAppender(val textArea: TextArea) : AppenderBase<ILoggingEvent>() {
    private var patternLayout: PatternLayout = PatternLayout()

    init {
        //this.patternLayout.setPattern("%-5level [%thread]: %message%n")
        this.patternLayout.setPattern("%date{HH:mm:ss.SSS} %-5level [%thread]: %message%n")
        this.patternLayout.setContext(LoggerFactory.getILoggerFactory() as LoggerContext)
        this.patternLayout.start()
    }

    /**
     * Append
     */
    override fun append(loggingEvent: ILoggingEvent) {
        val message = this.patternLayout.doLayout(loggingEvent)

        // Append formatted message to text area using the Thread.
        try {
            Platform.runLater({
                try {
                    if (textArea != null) {
                        if (textArea.getText().length() == 0) {
                            textArea.setText(message)
                        } else {
                            textArea.insertText(textArea.getText().length(), message)

                            if (textArea.getLength() > 10000)
                                textArea.setText(textArea.getText().substring(textArea.getLength() - 10000))

                            textArea.setScrollTop(Double.MAX_VALUE);
                        }
                    }
                } catch (e: Exception) {
                    // Ignore exceptions
                }
            })
        } catch (e: Exception) {
            // Ignore exceptions
        }
    }
}