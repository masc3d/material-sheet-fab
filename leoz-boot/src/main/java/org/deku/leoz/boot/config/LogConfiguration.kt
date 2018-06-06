package org.deku.leoz.boot.config

import com.github.salomonbrys.kodein.*
import org.deku.leoz.Storage

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
                val storage: Storage = instance()
                config.logFile = storage.logFile

                config
            }
        }
    }
}