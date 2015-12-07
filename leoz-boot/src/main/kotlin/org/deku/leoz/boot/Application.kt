package org.deku.leoz.boot

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import com.google.common.base.Strings
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.apache.commons.logging.LogFactory
import org.deku.leoz.boot.config.LogConfiguration
import org.deku.leoz.boot.config.StorageConfiguration
import org.deku.leoz.boot.fx.ResizeHelper
import org.deku.leoz.bundle.Bundle
import org.deku.leoz.bundle.Bundles
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

        @Parameter(names = arrayOf("--version-pattern"), description = "Version pattern override")
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

    fun selfInstall() {
        if (StorageConfiguration.nativeBundleBasePath == null)
            return

        val nativeBundlePath = StorageConfiguration.nativeBundleBasePath!!
        log.info(nativeBundlePath)

        if (nativeBundlePath.parentFile.equals(StorageConfiguration.bundleInstallationDirectory))
            return

        log.info("Performing self verification")
        Bundle.load(nativeBundlePath).verify()

        val srcPath = nativeBundlePath
        val destPath = File(StorageConfiguration.bundleInstallationDirectory, Bundles.LEOZ_BOOT)

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
                })
    }

    override fun start(primaryStage: Stage) {
        Application.set(this)

        LogConfiguration.initialize()

        try {
            log.info("Initializing")

            this.primaryStage = primaryStage

            // Uncaught threaded exception handler
            Thread.setDefaultUncaughtExceptionHandler(object : Thread.UncaughtExceptionHandler {
                override fun uncaughtException(t: Thread, e: Throwable) {
                    log.error(e.message, e)
                    System.exit(-1)
                }
            })

            // Parse command line params
            JCommander(Parameters, *this.parameters.raw.toTypedArray())
            if (Strings.isNullOrEmpty(Parameters.bundle)) {
                // Nothing to do
                log.warn("Missing or empty bundle parameter. Nothing to do, exiting")
                System.exit(0)
                return
            }

            // Initialize rsync
            Rsync.executable.baseFilename = "leoz-rsync"

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
            primaryStage.show()

            // Dismiss splash
            if (splash != null) {
                splash.close()
            }
        } catch(e: Exception) {
            log.error(e.message, e)
            System.exit(-1)
        }

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
