package org.deku.leoz.boot.config

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.eagerSingleton
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton

/**
 * Log configuration for leoz-boot
 * Created by masc on 01-Aug-15.
 */
class LogConfiguration : org.deku.leoz.config.LogConfiguration() {
    companion object {
        val module = Kodein.Module {
            bind<LogConfiguration>() with eagerSingleton {
                val config = LogConfiguration()

                // Setup file logging
                val storageConfiguration: StorageConfiguration = instance()
                config.logFile = storageConfiguration.logFile

                config
            }
        }
    }
}