package org.deku.leoz.boot

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.deku.leoz.boot.config.*
import org.deku.leoz.boot.fx.ResizeHelper
import org.deku.leoz.bundle.Bundle
import org.deku.leoz.bundle.BundleType
import org.slf4j.LoggerFactory
import sx.JarManifest
import sx.Stopwatch
import sx.rsync.Rsync
import sx.rsync.RsyncClient
import java.awt.SplashScreen
import java.io.File
import java.util.*
import kotlin.properties.Delegates
import kotlin.reflect.KDeclarationContainer

/**
 * Main application (javafx) class
 * Created by masc on 29-Jul-15.
 */
class Application : javafx.application.Application() {

    companion object {
        /** Application singleton instance */
        var instance: Application by Delegates.notNull()
            private set

        private fun set(instance: Application) {
            this.instance = instance
        }

        /**
         * Main application entry point
         */
        @JvmStatic fun main(args: Array<String>) {
            javafx.application.Application.launch(Application::class.java, *args)
        }
    }


    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Application settings
     */
    private val settings: Settings by Kodein.global.lazy.instance()

    /** Bundle repository URI */
    val repositoryUri: Rsync.URI?
        get() = if (this.settings.repositoryUriString != null)
            Rsync.URI(this.settings.repositoryUriString!!) else
            null

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
        val storage: StorageConfiguration  = Kodein.global.instance()
        if (storage.nativeBundleBasePath == null) {
            log.warn("Skipping self-installation as native bundle base path could not be determined (not running from native bundle)")
            return
        }

        val nativeBundlePath = storage.nativeBundleBasePath!!
        log.info("Native bundle path [${nativeBundlePath}")

        if (nativeBundlePath.parentFile == storage.bundleInstallationDirectory)
            return

        log.info("Performing self verification")
        Bundle.load(nativeBundlePath).verify()

        val srcPath = nativeBundlePath
        val destPath = File(storage.bundleInstallationDirectory, BundleType.LEOZ_BOOT.value)

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

            val sw = Stopwatch.createStarted()
            Kodein.global.addImport(ApplicationConfiguration.module)
            Kodein.global.addImport(StorageConfiguration.module)
            Kodein.global.addImport(LogConfiguration.module)
            Kodein.global.addImport(RsyncConfiguration.module)
            Kodein.global.addImport(DiscoveryConfiguration.module)
            Kodein.global.addImport(RestConfiguration.module)
            log.debug("Injetion completed [${sw}]")

            // Parse leoz-boot command line params
            JCommander(this.settings, *this.parameters.raw.toTypedArray())

            this.primaryStage = primaryStage

            // Uncaught threaded exception handler
            Thread.setDefaultUncaughtExceptionHandler(object : Thread.UncaughtExceptionHandler {
                override fun uncaughtException(t: Thread, e: Throwable) {
                    log.error(e.message, e)
                    System.exit(-1)
                }
            })

            // Show splash screen
            val splash = SplashScreen.getSplashScreen()

            // Setup JavaFX stage
            val root = FXMLLoader.load<Parent>(this.javaClass.getResource("/fx/Main.fxml"))
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
}
