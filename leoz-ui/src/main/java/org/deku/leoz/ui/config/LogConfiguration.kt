package org.deku.leoz.ui.config

import javafx.scene.control.TextArea
import org.deku.leoz.config.StorageConfiguration
import sx.fx.TextAreaLogAppender

/**
 * Created by masc on 26/09/2016.
 */
object LogConfiguration : org.deku.leoz.config.LogConfiguration() {
    val textAreaLogAppender by lazy { TextAreaLogAppender(TextArea()) }

    override fun initialize() {
        super.initialize()

        this.addAppender(this.textAreaLogAppender)
    }
}