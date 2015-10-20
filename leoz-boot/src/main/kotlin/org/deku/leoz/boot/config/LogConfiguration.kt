package org.deku.leoz.boot.config

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import ch.qos.logback.core.rolling.RollingFileAppender
import javafx.scene.control.TextArea
import org.deku.leoz.boot.config.StorageConfiguration
import org.deku.leoz.config.LogConfiguration
import org.slf4j.LoggerFactory

/**
 * Created by n3 on 01-Aug-15.
 */
object LogConfiguration : LogConfiguration() {

    init {
        this.logFile = StorageConfiguration.logFile
    }

    fun addAppender(appender: AppenderBase<ILoggingEvent>) {
        this.rootLogger.level = Level.INFO
        appender.context = this.loggerContext
        appender.start()
        this.rootLogger.addAppender(appender)
    }
}