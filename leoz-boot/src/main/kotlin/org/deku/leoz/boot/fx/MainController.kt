package org.deku.leoz.boot.fx

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import javafx.beans.property.DoubleProperty
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TextArea
import javafx.scene.text.TextFlow
import org.deku.leoz.boot.Application
import org.deku.leoz.boot.LocalStorage
import org.deku.leoz.boot.LogConfiguration
import org.deku.leoz.bundle.*
import org.slf4j.LoggerFactory
import sx.fx.TextAreaLogAppender
import java.net.URL
import java.util.*
import kotlin.concurrent.thread

/**
 * Created by n3 on 29-Jul-15.
 */
class MainController : Initializable {
    private val log: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(this.javaClass)

    @FXML
    lateinit val uxTitle: Label
    @FXML
    lateinit val uxTextArea: TextArea
    @FXML
    lateinit val uxProgressBar: ProgressBar
    @FXML
    lateinit val uxProgressIndicator: ProgressIndicator

    var logAppender: TextAreaLogAppender? = null

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        LogConfiguration.addAppender(TextAreaLogAppender(uxTextArea))

        val bundleName = Application.instance.bundle
        uxTitle.text = "Booting ${bundleName}"
        uxProgressBar.progressProperty().addListener { v, o, n ->
            uxProgressIndicator.isVisible = (n.toDouble() > 0.0 && n.toDouble() < 1)
        }

        thread {
            try {
                Application.instance.selfInstall()

                // TODO: move installation logic from controller to bundle installer
                val installer = BundleInstaller(
                        LocalStorage.bundlesDirectory,
                        bundleName,
                        BundleRepositoryFactory.stagingRepository(bundleName))

                if (installer.hasBundle()) {
                    installer.bundle.stop()
                    installer.bundle.uninstall()
                }

                installer.download(Bundle.Version.parse("0.1"), false, { f, p ->
                    if (p > 0.0) uxProgressBar.progress = p
                })

                installer.bundle.install()
                installer.bundle.start()

                uxProgressBar.styleClass.add("leoz-green-bar")
                log.info("Installed sucessfully")
            } catch(e: Exception) {
                uxProgressBar.styleClass.add("leoz-red-bar")
                log.error(e.getMessage(), e)
            } finally {
                uxProgressBar.progress = 1.0
            }
        }
    }
}