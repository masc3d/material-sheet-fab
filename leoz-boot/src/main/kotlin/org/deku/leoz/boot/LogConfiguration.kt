package org.deku.leoz.boot

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import javafx.scene.control.TextArea
import org.slf4j.LoggerFactory

/**
 * Created by n3 on 01-Aug-15.
 */
object LogConfiguration {
    private val rootLogger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as Logger
    private val context = LoggerFactory.getILoggerFactory() as LoggerContext

    fun addAppender(appender: AppenderBase<ILoggingEvent>) {
        rootLogger.level = Level.INFO
        appender.context = this.context
        appender.start()
        this.rootLogger.addAppender(appender)
    }
}