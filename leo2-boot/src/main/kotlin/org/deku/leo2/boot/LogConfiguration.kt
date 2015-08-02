package org.deku.leo2.boot

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import javafx.scene.control.TextArea
import org.deku.leo2.boot.fx.TextAreaLogAppender
import org.slf4j.LoggerFactory

/**
 * Created by n3 on 01-Aug-15.
 */
object LogConfiguration {
    private val rootLogger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as Logger
    private val context = LoggerFactory.getILoggerFactory() as LoggerContext

    fun addAppender(appender: AppenderBase<ILoggingEvent>) {
        appender.setContext(this.context)
        appender.start()
        this.rootLogger.addAppender(appender)
    }
}