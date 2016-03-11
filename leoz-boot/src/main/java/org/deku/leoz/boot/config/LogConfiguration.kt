package org.deku.leoz.boot.config

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import org.deku.leoz.config.LogConfiguration

/**
 * Created by masc on 01-Aug-15.
 */
object LogConfiguration : LogConfiguration() {

    /**
     * Add appender to log configuration
     */
    fun addAppender(appender: AppenderBase<ILoggingEvent>) {
        this.rootLogger.level = Level.INFO
        appender.context = this.loggerContext
        appender.start()
        this.rootLogger.addAppender(appender)
    }
}