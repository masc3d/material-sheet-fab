package org.deku.leoz.boot

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.apache.commons.logging.LogFactory
import org.deku.leoz.boot.config.StorageConfiguration
import org.deku.leoz.boot.fx.ResizeHelper
import org.deku.leoz.bundle.Bundle
import org.deku.leoz.bundle.Bundles
import sx.JarManifest
import sx.rsync.Rsync
import sx.rsync.RsyncClient
import java.awt.SplashScreen
import java.io.File
import java.util.*
import kotlin.properties.Delegates

/**
 * Main application entry point
 */
fun main(args: Array<String>) {
    javafx.application.Application.launch(Application::class.java, *args)
}

/**
 * Main application (javafx) class
 * Created by masc on 29-Jul-15.
 */
class Application : javafx.application.Application() {

    object Parameters {
        @Parameter(description = "Command args")
        val args: List<String> = ArrayList()

        @Parameter(names = arrayOf("--bundle"), description = "Bundle to boot")
        var bundle: String = ""

        @Parameter(names = arrayOf("--repository"), description = "Repository URI")
        var repositoryUriString: String? = null

        @Parameter(names = arrayOf("--no-ui"), description = "Don't show user interface")
        var hideUi: Boolean = false

        @Parameter(names = arrayOf("--force-download"), description = "Force download")
        var forceDownload: Boolean = false

        @Parameter(names = arrayOf("--version"), description = "Version pattern override")
        var versionPattern: String = "+RELEASE"

        @Parameter(names = arrayOf("--uninstall"), description = "Uninstall bundle")
        var uninstall: Boolean = false
    }

    private val log = LogFactory.getLog(this.javaClass)

    /** Bundle to install */
    val bundle by lazy({
        Parameters.bundle
    })

    /** Bundle repository URI */
    val repositoryUri: Rsync.URI
        get() = Rsync.URI(Parameters.repositoryUriString!!)

    /** Primary stage */
    var primaryStage: Stage by Delegates.notNull()
        private set

    /** Application jar manifest */
    val jarManifest: JarManifest by lazy({
        JarManifest(this.javaClass)
    })

    /** Application process exit code */
    var exitCode: Int = 0

    /**
     * Performs self-instllation of leoz-boot (into leoz bundles directory)
     * @param onProgress Progress callback
     */
    fun selfInstall(onProgress: ((p: Double) -> Unit) = {}) {
        if (StorageConfiguration.nativeBundleBasePath == null)
            return

        val nativeBundlePath = StorageConfiguration.nativeBundleBasePath!!
        log.info("Native bundle path [${nativeBundlePath}")

        if (nativeBundlePath.parentFile.equals(StorageConfiguration.bundleInstallationDirectory))
            return

        log.info("Performing self verification")
        Bundle.load(nativeBundlePath).verify()

        val srcPath = nativeBundlePath
        val destPath = File(StorageConfiguration.bundleInstallationDirectory, Bundles.LEOZ_BOOT.value)

        val rc = RsyncClient()
        val source = Rsync.URI(srcPath)
        val destination = Rsync.URI(destPath)
        rc.delete = true
        rc.preserveExecutability = true
        rc.preservePermissions = false

        log.info("Synchronizing [${source}] -> [${destination}]")
        rc.sync(source, destination,
                onFile = { r ->
                    log.info("Updating [${r.flags}] [${r.path}]")
                },
                onProgress = { pr ->
                    onProgress(pr.percentage.toDouble() / 100)
                })
    }

    override fun start(primaryStage: Stage) {
        Application.set(this)

        try {
            // Leoz bundle process commandline interface
            val setup = Setup()
            val command = setup.parse(this.parameters.raw.toTypedArray())
            if (command != null) {
                command.run()
                System.exit(0)
                return
            }

            // Parse leoz-boot command line params
            JCommander(Parameters, *instance.parameters.raw.toTypedArray())

            this.primaryStage = primaryStage

            // Uncaught threaded exception handler
            Thread.setDefaultUncaughtExceptionHandler(object : Thread.UncaughtExceptionHandler {
                override fun uncaughtException(t: Thread, e: Throwable) {
                    log.error(e.message, e)
                    System.exit(-1)
                }
            })

            // Show splash screen
            var splash = SplashScreen.getSplashScreen()

            // Setup JavaFX stage
            val root = FXMLLoader.load<Parent>(this.javaClass.getResource("/fx/Main.fxml"))
            primaryStage.title = "Leoz"
            primaryStage.scene = Scene(root, 800.0, 475.0)
            ResizeHelper.addResizeListener(primaryStage)

            var screenBounds = Screen.getPrimary().bounds
            var rootBounds = root.boundsInLocal

            var img = this.javaClass.getResourceAsStream("/images/DEKU.icon.256px.png")
            primaryStage.icons.add(Image(img))
            primaryStage.initStyle(StageStyle.UNDECORATED)
            primaryStage.y = (screenBounds.height - rootBounds.height) / 2
            primaryStage.x = (screenBounds.width - rootBounds.width) / 2
            if (Parameters.hideUi) {
                primaryStage.hide()
            } else {
                primaryStage.show()
            }

            // Dismiss splash
            if (splash != null) {
                splash.close()
            }
        } catch(e: Exception) {
            log.error(e.message, e)
            this.exitCode = -1
            this.stop()
        }
    }

    override fun stop() {
        super.stop()
        System.exit(this.exitCode)
    }

    companion object {
        /** Application singleton instance */
        var instance: Application by Delegates.notNull()
            private set

        private fun set(instance: Application) {
            this.instance = instance
        }
    }
}
