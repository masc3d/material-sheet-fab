package org.deku.leoz.mobile.config

import android.content.Context
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.android.LogcatAppender
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.*
import org.deku.leoz.mobile.mq.MqttChannels
import org.deku.leoz.identity.Identity
import org.deku.leoz.log.LogMqAppender
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.Storage
import org.slf4j.LoggerFactory
import sx.mq.mqtt.channel

/**
 * Log configuration
 * Created by masc on 12/12/2016.
 */
class LogConfiguration {

    private val rootLogger by lazy {
        LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
    }

    companion object {
        private val loggerContext by lazy {
            LoggerFactory.getILoggerFactory() as LoggerContext
        }

        val module = Kodein.Module {
            bind<LogMqAppender>() with singleton {
                val mqttChannels = instance<MqttChannels>()

                val appender = LogMqAppender(
                        channelSupplier = { mqttChannels.central.transient.channel() },
                        identitySupplier = { instance<Identity>() }
                )

                appender.context = loggerContext
                appender.start()
                appender.dispatcher.start()

                appender
            }

            bind<RollingFileAppender<ILoggingEvent>>() with singleton {
                val fileAppender = RollingFileAppender<ILoggingEvent>()

                val path = instance<Storage>().logDir
                val name = instance<Context>().getString(R.string.app_project_name)
                val logFile = path.resolve("${name}.log").absoluteFile

                val encoder = PatternLayoutEncoder()
                encoder.context = loggerContext
                encoder.pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
                encoder.start()

                val baseFilename = logFile.nameWithoutExtension
                val extension = logFile.extension

                val policy = TimeBasedRollingPolicy<ILoggingEvent>()
                policy.context = loggerContext
                policy.setParent(fileAppender)
                policy.fileNamePattern = logFile.parentFile.resolve("${baseFilename}-%d{yyyy-MM-dd}.${extension}").absolutePath
                policy.maxHistory = 5
                policy.start()

                fileAppender.context = loggerContext
                fileAppender.file = logFile.toString()
                fileAppender.encoder = encoder
                fileAppender.rollingPolicy = policy
                fileAppender.triggeringPolicy = policy
                fileAppender.start()

                fileAppender
            }

            bind<LogcatAppender>() with singleton {
                val logcatAppender = LogcatAppender()

                val encoder = PatternLayoutEncoder()
                encoder.context = loggerContext
                encoder.pattern = "[%thread] %msg%n"
                encoder.start()

                logcatAppender.context = loggerContext
                logcatAppender.encoder = encoder
                logcatAppender.start()

                logcatAppender
            }

            bind<LogConfiguration>() with eagerSingleton {
                loggerContext.reset()

                val config = LogConfiguration()

                config.rootLogger.level = Level.ALL

                config.rootLogger.addAppender(instance<RollingFileAppender<ILoggingEvent>>())
                config.rootLogger.addAppender(instance<LogcatAppender>())
                config.rootLogger.addAppender(instance<LogMqAppender>())

                config
            }
        }
    }
}