package org.deku.leoz.ui.config

import com.github.salomonbrys.kodein.*
import javafx.scene.control.TextArea
import org.deku.leoz.Storage
import sx.fx.TextAreaLogAppender

/**
 * Log configuration
 * Created by masc on 26/09/2016.
 */
class LogConfiguration : org.deku.leoz.config.LogConfiguration() {
    val textAreaLogAppender by lazy { TextAreaLogAppender(TextArea()) }

    companion object {
        val module = Kodein.Module {
            bind<LogConfiguration>() with eagerSingleton {
                val storageConfiguration: Storage = instance()
                val config = LogConfiguration()
                config.logFile = storageConfiguration.logFile
                config
            }
        }
    }

    init {
        this.addAppender(this.textAreaLogAppender)
    }
}