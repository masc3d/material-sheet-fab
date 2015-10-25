package org.deku.leoz.boot

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.apache.commons.logging.LogFactory
import org.deku.leoz.boot.config.StorageConfiguration
import org.deku.leoz.boot.fx.ResizeHelper
import org.deku.leoz.bundle.Bundle
import org.deku.leoz.bundle.BundleRepositoryFactory
import org.deku.leoz.bundle.Bundles
import sx.io.PermissionUtil
import sx.rsync.Rsync
import sx.rsync.RsyncClient
import java.awt.SplashScreen
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
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

    private object Parameters {
        @Parameter(description = "Bundle to boot")
        var bundles: List<String> = ArrayList()

        @Parameter(names = arrayOf("--repository"), description = "Repository URI")
        var repositoryUriString: String? = null

        @Parameter(names = arrayOf("--no-ui"), description = "Don't show user interface")
        var hideUi: Boolean? = true
    }

    private val log = LogFactory.getLog(this.javaClass)

    /** Bundle to install */
    val bundle by lazy({
        Parameters.bundles.first()
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

        if (nativeBundlePath.parentFile.equals(StorageConfiguration.bundlesDirectory))
            return

        log.info("Performing self verification")
        Bundle.load(nativeBundlePath).verify()

        val srcPath = nativeBundlePath
        val destPath = File(StorageConfiguration.bundlesDirectory, Bundles.LEOZ_BOOT)

        val rc = RsyncClient()
        rc.source = Rsync.URI(srcPath)
        rc.destination = Rsync.URI(destPath)
        rc.delete = true
        rc.preserveExecutability = true
        rc.preservePermissions = false

        log.info("Synchronizing [${rc.source}] -> [${rc.destination}]")
        rc.sync( onFile = { r ->
            log.info("Updating [${r.flags}] [${r.path}]")
        })
    }

    override fun start(primaryStage: Stage) {
        Application.set(this)

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
        if (Parameters.bundles.size == 0) {
            // Nothing to do
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
