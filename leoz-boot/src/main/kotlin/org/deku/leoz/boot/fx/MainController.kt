package org.deku.leoz.boot.fx

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import org.deku.leoz.boot.Application
import org.deku.leoz.boot.config.BundleInstallerConfiguration
import org.deku.leoz.boot.config.LogConfiguration
import sx.fx.TextAreaLogAppender
import java.awt.GraphicsEnvironment
import java.net.URL
import java.util.*
import kotlin.concurrent.thread

/**
 * Created by n3 on 29-Jul-15.
 */
class MainController : Initializable {
    private val log: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(this.javaClass)

    @FXML
    lateinit var uxTitle: Label
    @FXML
    lateinit var uxTextArea: TextArea
    @FXML
    lateinit var uxProgressBar: ProgressBar
    @FXML
    lateinit var uxProgressIndicator: ProgressIndicator
    @FXML
    lateinit var uxClose: Button

    var logAppender: TextAreaLogAppender? = null

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        LogConfiguration.addAppender(TextAreaLogAppender(uxTextArea, 1000))

        val bundleName = Application.instance.bundle
        uxTitle.text = "Booting ${bundleName}"
        uxProgressBar.progressProperty().addListener { v, o, n ->
            uxProgressIndicator.isVisible = (n.toDouble() > 0.0 && n.toDouble() < 1)
        }
        uxClose.onMouseClicked = object:EventHandler<MouseEvent> {
            override fun handle(event: MouseEvent?) {
                Application.instance.primaryStage.close()
            }
        }
        uxClose.visibleProperty().value = false

        thread {
            try {
                Application.instance.selfInstall()

                val installer = BundleInstallerConfiguration.installer()

                if (!installer.hasBundle(bundleName) || Application.Parameters.forceDownload) {
                    installer.download(
                            bundleName = bundleName,
                            versionPattern = Application.Parameters.versionPattern,
                            forceDownload = Application.Parameters.forceDownload,
                            onProgress = { f, p ->
                                if (p > 0.0) uxProgressBar.progress = p
                            }
                    )
                }
                installer.install(bundleName)

                Platform.runLater {
                    uxProgressBar.styleClass.add("leoz-green-bar")
                    uxTitle.text = "Booted ${bundleName} succesfully."
                }
            } catch(e: Exception) {
                log.error(e.message, e)
                Platform.runLater {
                    uxProgressBar.styleClass.add("leoz-red-bar")
                    uxTitle.text = "Booting ${bundleName} failed."
                }
            } finally {
                uxProgressBar.progress = 1.0
                uxClose.visibleProperty().value = true
            }

            if (Application.Parameters.hideUi || GraphicsEnvironment.isHeadless())
                System.exit(0)
        }
    }
}