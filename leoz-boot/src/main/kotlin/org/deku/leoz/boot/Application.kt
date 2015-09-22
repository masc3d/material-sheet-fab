package org.deku.leoz.boot

//import com.beust.jcommander.JCommander
//import com.beust.jcommander.Parameter
import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Screen
import javafx.stage.Stage
import org.apache.commons.logging.LogFactory
import org.deku.leoz.bundle.Bundle
import org.deku.leoz.bundle.BundleRepositoryFactory
import org.deku.leoz.bundle.Bundles
import sx.rsync.Rsync
import sx.rsync.RsyncClient
import java.awt.SplashScreen
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.concurrent.thread
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
    @Throws(Exception::class)

    private object Parameters {
        @Parameter(description = "Bundle to boot")
        var bundle: List<String> = ArrayList()

        @Parameter(names = arrayOf("--repository"), description = "Repository URI")
        var repositoryUriString: String? = null
    }

    private val log = LogFactory.getLog(this.javaClass)

    /** Bundle to install */
    val bundle by lazy({
        Parameters.bundle.first()
    })

    /** Bundle repository URI */
    val repositoryUri: Rsync.URI
        get() = Rsync.URI(Parameters.repositoryUriString!!)


    fun selfInstall() {
        if (LocalStorage.nativeBundleBasePath == null)
            return

        val nativeBundlePath = LocalStorage.nativeBundleBasePath!!
        log.info(nativeBundlePath)

        if (nativeBundlePath.parentFile.equals(LocalStorage.bundlesDirectory))
            return

        log.info("Installing leoz-boot")

        Bundle.load(nativeBundlePath).verify()

        val srcPath = nativeBundlePath
        val destPath = File(LocalStorage.bundlesDirectory, Bundles.LEOZ_BOOT)

        val rc = RsyncClient()
        rc.source = Rsync.URI(srcPath)
        rc.destination = Rsync.URI(destPath)
        rc.preservePermissions = false

        log.info("Synchronizing [${rc.source}] -> [${rc.destination}]")
        rc.sync( onFile = { r ->
            log.info("Updating [${r.flags}] [${r.path}]")
        })
    }

    override fun start(primaryStage: Stage) {
        Application.set(this)

        // Parse command line params
        JCommander(Parameters, *this.parameters.raw.toTypedArray())

        // Initialize local storage
        LocalStorage.appName = "leoz-boot"

        // Initialize rsync
        Rsync.executable.baseFilename = "leoz-rsync"

        // Show splash screen
        var splash = SplashScreen.getSplashScreen()

        // Setup JavaFX stage
        val root = FXMLLoader.load<Parent>(this.javaClass.getResource("/fx/Main.fxml"))
        primaryStage.title = "Leoz"
        primaryStage.scene = Scene(root, 800.0, 475.0)

        var screenBounds = Screen.getPrimary().bounds
        var rootBounds = root.boundsInLocal

        var img = this.javaClass.getResourceAsStream("/images/DEKU.icon.256px.png")
        primaryStage.icons.add(Image(img))
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
