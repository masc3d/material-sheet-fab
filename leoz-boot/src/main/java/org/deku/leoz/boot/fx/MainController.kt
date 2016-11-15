package org.deku.leoz.boot.fx

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.google.common.base.Strings
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import org.deku.leoz.boot.Application
import org.deku.leoz.boot.Settings
import org.deku.leoz.boot.config.LogConfiguration
import org.deku.leoz.boot.config.StorageConfiguration
import org.deku.leoz.bundle.BundleInstaller
import org.deku.leoz.bundle.BundleRepository
import org.deku.leoz.config.BundleConfiguration
import sx.fx.TextAreaLogAppender
import sx.platform.JvmUtil
import sx.rsync.Rsync
import java.awt.GraphicsEnvironment
import java.net.URL
import java.util.*
import kotlin.concurrent.thread

/**
 * Created by masc on 29-Jul-15.
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

    private val settings: Settings by Kodein.global.lazy.instance()

    private val logConfiguration: LogConfiguration by Kodein.global.lazy.instance()

    private val storageConfiguration: StorageConfiguration by Kodein.global.lazy.instance()

    var logAppender: TextAreaLogAppender? = null

    /**
     * Calculate progress of intermediate steps
     * @param startProgress Start of progress for intermediate step
     * @param endProgress End of progress for intermediate step
     * @param progress Current progress (from 0.0 to 1.0)
     */
    private fun calculateProgress(startProgress: Double, endProgress: Double, progress: Double): Double {
        val range = endProgress - startProgress
        return startProgress + (range * progress)
    }

    /**
     * Initialize
     */
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        uxTitle.text = ""
        uxProgressBar.progressProperty().addListener { v, o, n ->
            uxProgressIndicator.isVisible = (n.toDouble() == ProgressBar.INDETERMINATE_PROGRESS || (n.toDouble() >= 0.0 && n.toDouble() < 1))
        }
        uxClose.onMouseClicked = object:EventHandler<MouseEvent> {
            override fun handle(event: MouseEvent?) {
                Application.instance.primaryStage.close()
            }
        }
        uxClose.visibleProperty().value = false
        uxProgressBar.progress = 0.0

        // TODO. separate main installation logic from UI controller
        thread {
            var verb: String = "Initializing"
            val verbPast: String

            try {
                this.logConfiguration.addAppender(TextAreaLogAppender(uxTextArea, 1000))

                log.info(JvmUtil.shortInfoText)

                try {
                    log.info("leoz-boot [${Application.instance.jarManifest.implementationVersion}] ${JvmUtil.shortInfoText}")
                } catch(e: Exception) {
                    // Printing jar manifest will fail when running from IDE eg. that's ok.
                }

                if (Strings.isNullOrEmpty(this.settings.bundle)) {
                    // Nothing to do
                    throw IllegalArgumentException("Missing or empty bundle parameter. Nothing to do, exiting");
                }

                val bundleName = this.settings.bundle

                when (this.settings.uninstall) {
                    true -> {
                        verb = "Uninstalling"
                        verbPast = "Uninstalled"
                    }
                    else -> {
                        verb = "Booting"
                        verbPast = "Booted"
                    }
                }
                verb += " ${bundleName}"

                log.info(verb)

                Platform.runLater {
                    uxTitle.text = verb
                    uxProgressBar.progressProperty().addListener { v, o, n ->
                        uxProgressIndicator.isVisible = (n.toDouble() == ProgressBar.INDETERMINATE_PROGRESS || (n.toDouble() >= 0.0 && n.toDouble() < 1))
                    }
                    uxClose.onMouseClicked = object : EventHandler<MouseEvent> {
                        override fun handle(event: MouseEvent?) {
                            Application.instance.primaryStage.close()
                        }
                    }
                    uxClose.visibleProperty().value = false
                    uxProgressBar.progress = ProgressBar.INDETERMINATE_PROGRESS
                }

                var startProgress = 0.0
                var endProgress = 0.3
                Application.instance.selfInstall(onProgress = { p ->
                    if (p > 0.0)
                        Platform.runLater {
                            this.uxProgressBar.progress = this.calculateProgress(startProgress, endProgress, p)
                        }
                })

                val installer = BundleInstaller(this.storageConfiguration.bundleInstallationDirectory)

                if (this.settings.uninstall) {
                    installer.uninstall(bundleName)
                } else {
                    if (!installer.hasBundle(bundleName) || this.settings.forceDownload) {
                        val repository = if (Application.instance.repositoryUri != null)
                            BundleRepository(Application.instance.repositoryUri!!) else
                            BundleConfiguration.stagingRepository

                        // Query for version matching pattern
                        val version = repository.queryLatestMatchingVersion(
                                bundleName,
                                this.settings.versionPattern)

                        // Download bundle
                        startProgress = 0.3
                        endProgress = 1.0
                        installer.download(
                                bundleRepository = repository,
                                bundleName = bundleName,
                                version = version,
                                forceDownload = this.settings.forceDownload,
                                onProgress = { f, p ->
                                    if (p > 0.0)
                                        Platform.runLater {
                                            uxProgressBar.progress = this.calculateProgress(startProgress, endProgress, p)
                                        }
                                }
                        )
                    }
                    Platform.runLater {
                        uxProgressBar.progress = ProgressBar.INDETERMINATE_PROGRESS
                    }
                    installer.install(bundleName)
                }

                Platform.runLater {
                    uxProgressBar.styleClass.add("leoz-green-bar")
                    uxTitle.text = "${verbPast} succesfully."
                }
            } catch(e: Throwable) {
                Application.instance.exitCode = -1
                log.error(e.message, e)
                Platform.runLater {
                    uxProgressBar.styleClass.add("leoz-red-bar")
                    uxTitle.text = "${verb} failed."
                }
            } finally {
                Platform.runLater {
                    uxProgressBar.progress = 1.0
                    uxClose.visibleProperty().value = true
                }
            }

            if (this.settings.hideUi || GraphicsEnvironment.isHeadless()) {
                System.exit(Application.instance.exitCode)
            }
        }
    }
}