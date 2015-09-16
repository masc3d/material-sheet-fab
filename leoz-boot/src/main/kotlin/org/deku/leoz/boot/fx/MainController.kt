package org.deku.leoz.boot.fx

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ProgressBar
import javafx.scene.control.TextArea
import javafx.scene.text.TextFlow
import org.deku.leoz.boot.LogConfiguration
import org.slf4j.LoggerFactory
import sx.fx.TextAreaLogAppender
import java.net.URL
import java.util.*
import kotlin.concurrent.thread

/**
 * Created by n3 on 29-Jul-15.
 */
class MainController : Initializable {
    @FXML
    var uxTextArea: TextArea? = null
    @FXML
    var uxProgressBar: ProgressBar? = null

    var logAppender: TextAreaLogAppender? = null

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        LogConfiguration.addAppender(TextAreaLogAppender(uxTextArea as TextArea))

        var log: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(this.javaClass)

        thread(priority = 5) {
            Thread.sleep(1000);
            var max = 2000
            for (i in 1..max) {
                log.info("Hello ${i}")
                uxProgressBar!!.progress = i.toDouble() / max
                Thread.sleep(1)
            }
            System.gc()
            System.runFinalization()
        }
    }
}