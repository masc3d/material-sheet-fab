package org.deku.leoz.ui

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.layout.Pane
import javafx.scene.text.Font
import javafx.stage.Screen
import javafx.stage.Stage
import org.controlsfx.control.Notifications
import org.deku.leoz.discovery.DiscoveryService
import org.deku.leoz.ui.bridge.LeoBridge
import org.deku.leoz.ui.config.Configurations
import org.deku.leoz.ui.config.LogConfiguration
import org.deku.leoz.ui.config.StorageConfiguration
import org.deku.leoz.ui.fx.Controller
import org.deku.leoz.ui.fx.MainController
import org.slf4j.LoggerFactory
import sx.util.Utf8ResourceBundleControl
import java.io.IOException
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * Main application class
 */
class Main : Application() {
    private val log = LoggerFactory.getLogger(Main::class.java)

    companion object {
        /**
         * Application main entry point
         * @param args
         */
        @JvmStatic fun main(args: Array<String>) {
            javafx.application.Application.launch(Main::class.java, *args)
        }
    }

    /** Primary stage. Intialized when javafx application is started */
    private lateinit var primaryStage: Stage

    /** Localization */
    private val i18n: org.deku.leoz.ui.Localization by Kodein.global.lazy.instance()

    /**
     * Main user interface controller
     * @return
     */
    val mainController: MainController by lazy {
        val mc = Controller.fromFxml<MainController>("/fx/Main.fxml")
        mc
    }

    /**
     * Utility method for loading font. Once a font has been loaded (even without explicitly using it),
     * it can be referenced within css stylesheetss.
     * @param resourcePath
     * @return
     */
    fun loadFont(resourcePath: String): Font {
        return Font.loadFont(javaClass.getResource(resourcePath).toExternalForm(), 10.0)
    }

    /**
     * Scene start (javafx main)
     * @param primaryStage
     * @throws Exception
     */
    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        log.info("JavaFX application start")

        // Support for command line interface
        val setup = Setup()
        val command = setup.parse(this.parameters.raw.toTypedArray())
        if (command != null) {
            try {
                command.run()
                System.exit(0)
            } catch (e: Exception) {
                log.error(e.message, e)
                System.exit(-1)
            }

            return
        }

        log.debug("Initializing injection")

        StorageConfiguration.initalize()

        LogConfiguration.logFile = StorageConfiguration.logFile
        LogConfiguration.initialize()

        // Setup injection
        Kodein.global.addImport(Configurations.application)
        Kodein.global.addImport(Configurations.messenging)

        log.debug("Detaching background initialization")

        // Initialize application
        val executor: ExecutorService = Kodein.global.instance()

        // Background task initialization
        executor.submit {
            val leoBridge: LeoBridge = Kodein.global.instance()

            // Start leo bridge (asynchronously so it doesn't slow down startup)
            try {
                leoBridge.ovMessageReceived.subscribe {
                    this.toForeground()
                }
                leoBridge.start()
            } catch (e: IOException) {
                log.error(e.message, e)
            }

            // Start discovery service
            Kodein.global.instance<DiscoveryService>().start()
        }

        log.debug("Initializing primary stage")

        // Setup stage
        this.primaryStage = primaryStage

        log.debug("Loading fonts")
        // Load embedded fonts
        this.loadFont("/fonts/Futura-CondensedExtraBold.ttf")
        this.loadFont("/fonts/Futura-CondensedMedium.ttf")
        this.loadFont("/fonts/Futura-Medium.ttf")
        this.loadFont("/fonts/Futura-MediumItalic.ttf")

        // Main scene
        //TODO: User preferences? Check if last size and position should be remembered. Dont think so (PHPR)
        val primScreenBounds: Rectangle2D = Screen.getPrimary().visualBounds // Used to access the computers screen resolution/size

        //Set default scene size which is used when primary stage is no more in maximized mode
        val width = if(primScreenBounds.width < 1366.0) primScreenBounds.width - 50 else 1366.0
        val height = if(primScreenBounds.height < 768.0) primScreenBounds.height - 50 else 768.0

        log.debug("Creating main controller")
        val scene = Scene(mainController.fxRoot, width, height)
        log.debug("Created main controller")

        primaryStage.title = this.i18n.resources.getString("global.title")!!
        primaryStage.icons.add(Image(this.javaClass.getResourceAsStream("/images/DEKU.icon.256px.png")))
        primaryStage.scene = scene

        log.info("Showing primary stage")
        // Maximizing by default is usually annoying for users and developers alike.
        // Default should be the minimum supported size.
        // If it's necessary to maximize by default on partidular occasions/installations it should be paremeterized.
        primaryStage.show()
    }

    @Throws(Exception::class)
    override fun stop() {
        this.mainController.close()

        // Stop services
        Kodein.global.instance<LeoBridge>().stop()
        Kodein.global.instance<DiscoveryService>().stop()
        Kodein.global.instance<ExecutorService>().shutdown()
        super.stop()
    }

    /**
     * Makes primary stage the current desktop foreground window
     */
    fun toForeground() {
        // toFront doesn't suffice
        this.primaryStage.isAlwaysOnTop = true
        this.primaryStage.toFront()
        this.primaryStage.requestFocus()
        this.primaryStage.isAlwaysOnTop = false
    }
}

