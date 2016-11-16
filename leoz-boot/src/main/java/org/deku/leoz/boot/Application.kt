package org.deku.leoz.boot

import com.beust.jcommander.JCommander
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.google.common.base.Strings
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.deku.leoz.boot.config.*
import org.deku.leoz.boot.fx.MainController
import org.deku.leoz.boot.fx.ResizeHelper
import org.slf4j.LoggerFactory
import rx.Observable
import rx.lang.kotlin.subscribeWith
import sx.Stopwatch
import java.awt.GraphicsEnvironment
import java.awt.SplashScreen
import kotlin.properties.Delegates

/**
 * Main application (javafx) class
 * Created by masc on 29-Jul-15.
 */
class Application : javafx.application.Application() {

    companion object {
        /**
         * Main application entry point
         */
        @JvmStatic fun main(args: Array<String>) {
            javafx.application.Application.launch(Application::class.java, *args)
        }
    }

    private val log = LoggerFactory.getLogger(this.javaClass)

    /** Application settings */
    private val settings: Settings by Kodein.global.lazy.instance()

    /** Primary stage */
    private var primaryStage: Stage by Delegates.notNull()
        private set

    /**
     * Calculate progress of intermediate steps
     * @param startProgress Start of progress for intermediate step
     * @param endProgress End of progress for intermediate step
     * @param progress Current progress (from 0.0 to 1.0)
     */
    private fun skewProgress(startProgress: Double, endProgress: Double, progress: Double): Double {
        val range = endProgress - startProgress
        return startProgress + (range * progress)
    }

    /**
     * JavaFX start
     */
    override fun start(primaryStage: Stage) {
        try {
            // Leoz bundle process commandline interface
            val setup = Setup()
            val command = setup.parse(this.parameters.raw.toTypedArray())
            if (command != null) {
                command.run()
                System.exit(0)
                return
            }

            // Injection setup
            val sw = Stopwatch.createStarted()
            Kodein.global.addImport(ApplicationConfiguration.module)
            Kodein.global.addImport(StorageConfiguration.module)
            Kodein.global.addImport(LogConfiguration.module)
            Kodein.global.addImport(RsyncConfiguration.module)
            Kodein.global.addImport(DiscoveryConfiguration.module)
            Kodein.global.addImport(RestConfiguration.module)
            Kodein.global.addImport(BundleConfiguration.module)
            log.debug("Injetion completed [${sw}]")

            // Uncaught threaded exception handler
            Thread.setDefaultUncaughtExceptionHandler(object : Thread.UncaughtExceptionHandler {
                override fun uncaughtException(t: Thread, e: Throwable) {
                    log.error(e.message, e)
                    Platform.runLater {
                        this@Application.exit(-1)
                    }
                }
            })

            // Parse leoz-boot command line params
            JCommander(this.settings, *this.parameters.raw.toTypedArray())

            this.primaryStage = primaryStage

            if (Strings.isNullOrEmpty(this.settings.bundle)) {
                // Nothing to do
                throw IllegalArgumentException("Missing or empty bundle parameter. Nothing to do, exiting");
            }

            val boot = Boot()
            val bootTask = if (settings.uninstall)
                boot.uninstallTask(this.settings.bundle)
            else
                boot.installTask(
                        bundleName = this.settings.bundle,
                        forceDownload = this.settings.forceDownload,
                        versionAlias = "",
                        versionPattern = this.settings.versionPattern,
                        bundleRepositoryUri = this.settings.repositoryUriString)

            val task = Observable.concat(
                    boot.selfInstallTask()
                            .map {
                                Boot.Event(this.skewProgress(0.0, 0.3, it.progress))
                            },
                    bootTask
                            .map {
                                Boot.Event(this.skewProgress(0.3, 1.0, it.progress))
                            })

            // Show splash screen
            val splash = SplashScreen.getSplashScreen()

            if (settings.hideUi || GraphicsEnvironment.isHeadless()) {
                // Execute boot task on this thread
                task.subscribeWith {
                    onCompleted {
                        this@Application.exit(0)
                    }
                    onError {
                        log.error(it.message, it)
                        this@Application.exit(-1)
                    }
                }
            } else {
                // Setup JavaFX stage
                val loader = FXMLLoader(this.javaClass.getResource("/fx/Main.fxml"))
                val root = loader.load<Parent>()
                val controller = loader.getController<MainController>()
                controller.exitEvent.subscribe {
                    this.exit(it)
                }

                primaryStage.title = "Leoz"
                primaryStage.scene = Scene(root, 800.0, 475.0)
                ResizeHelper.addResizeListener(primaryStage)

                val screenBounds = Screen.getPrimary().bounds
                val rootBounds = root.boundsInLocal

                val img = this.javaClass.getResourceAsStream("/images/DEKU.icon.256px.png")
                primaryStage.icons.add(Image(img))
                primaryStage.initStyle(StageStyle.UNDECORATED)
                primaryStage.y = (screenBounds.height - rootBounds.height) / 2
                primaryStage.x = (screenBounds.width - rootBounds.width) / 2
                if (this.settings.hideUi) {
                    primaryStage.hide()
                } else {
                    primaryStage.show()
                }

                // Dismiss splash
                if (splash != null) {
                    splash.close()
                }

                // Execute boot task via main controller
                Platform.runLater {
                    controller.run(task)
                }
            }
        } catch(e: Exception) {
            log.error(e.message, e)
            this.exit(-1)
        }
    }

    /**
     * Exit application
     */
    fun exit(exitCode: Int) {
        this.primaryStage.close()

        // Platform exit code is rather slow, thus delaying invocation to prevent perceivable delay closing the stage/window
        Platform.runLater {
            Platform.exit()
            System.exit(exitCode)
        }
    }
}
