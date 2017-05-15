package org.deku.leoz.mobile.config

import android.content.Context
import android.support.v4.content.ContextCompat
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.android.LogcatAppender
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.*
import org.deku.leoz.log.LogMqAppender
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.Storage
import org.slf4j.LoggerFactory
import java.io.File

/**
 * Log configuration
 * Created by masc on 12/12/2016.
 */
class LogConfiguration(
        val path: File,
        val name: String) {

    companion object {
        val module = Kodein.Module {
            bind<LogConfiguration>() with eagerSingleton {
                val storage: Storage = instance()
                val context: Context = instance()

                LogConfiguration(
                        path = storage.logDir,
                        name = context.getString(R.string.app_project_name))
            }
        }
    }

    private val loggerContext by lazy {
        LoggerFactory.getILoggerFactory() as LoggerContext
    }

    private val rootLogger by lazy {
        LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
    }

    /**
     * Configures logging
     */
    init {
        // Root logger
        val root = this.rootLogger

        // Logger context
        val lc = this.loggerContext
        lc.reset()

        // Setup file logging
        val fileAppender = RollingFileAppender<ILoggingEvent>()
        run {
            val logFile = this.path.resolve("${this.name}.log").absoluteFile

            val encoder = PatternLayoutEncoder()
            encoder.context = lc
            encoder.pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
            encoder.start()

            val baseFilename = logFile.nameWithoutExtension
            val extension = logFile.extension

            val policy = TimeBasedRollingPolicy<ILoggingEvent>()
            policy.context = lc
            policy.setParent(fileAppender)
            policy.fileNamePattern = logFile.parentFile.resolve("${baseFilename}-%d{yyyy-MM-dd}.${extension}").absolutePath
            policy.maxHistory = 5
            policy.start()

            fileAppender.context = lc
            fileAppender.file = logFile.toString()
            fileAppender.encoder = encoder
            fileAppender.rollingPolicy = policy
            fileAppender.triggeringPolicy = policy
            fileAppender.start()
        }

        // Setup logcat logging
        val logcatAppender = LogcatAppender()
        run {
            val encoder = PatternLayoutEncoder()
            encoder.context = lc
            encoder.pattern = "[%thread] %msg%n"
            encoder.start()

            logcatAppender.context = lc
            logcatAppender.encoder = encoder
            logcatAppender.start()
        }

        root.level = Level.ALL

        // Add appenders
        root.addAppender(fileAppender)
        root.addAppender(logcatAppender)
    }

    fun addAppender(appender: AppenderBase<ILoggingEvent>) {
        appender.context = this.loggerContext
        appender.start()
        this.rootLogger.addAppender(appender)
    }
}