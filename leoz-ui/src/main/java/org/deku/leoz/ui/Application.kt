package org.deku.leoz.ui

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import javafx.application.Application
import javafx.application.Platform
import javafx.geometry.Rectangle2D
import javafx.scene.Scene
import javafx.scene.control.ProgressIndicator
import javafx.scene.image.Image
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Background
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.apache.commons.lang3.SystemUtils
import org.deku.leoz.service.internal.DiscoveryService
import org.deku.leoz.ui.bridge.LeoBridge
import org.deku.leoz.ui.config.*
import org.deku.leoz.ui.fx.Controller
import org.deku.leoz.ui.fx.MainController
import org.slf4j.LoggerFactory
import sx.fx.controls.MaterialProgressIndicator
import sx.io.ProcessLockFile
import java.util.concurrent.ExecutorService

/**
 * Main application class
 */
class Application : Application() {
    private val log = LoggerFactory.getLogger(org.deku.leoz.ui.Application::class.java)

    companion object {
        /**
         * Application main entry point
         * @param args
         */
        @JvmStatic fun main(args: Array<String>) {
            // Support for command line interface
            val setup = Setup()
            val command = setup.parse(args)
            if (command != null) {
                command.run()
                System.exit(0)
                return
            }

            javafx.application.Application.launch(org.deku.leoz.ui.Application::class.java, *args)
        }
    }

    /** Primary stage. Intialized when javafx application is started */
    private lateinit var primaryStage: Stage

    /** Localization */
    private val i18n: org.deku.leoz.ui.Localization by Kodein.global.lazy.instance()
    private val logConfiguration: LogConfiguration by Kodein.global.lazy.instance()
    private val leoBridge: LeoBridge by Kodein.global.lazy.instance()

    private val processLockFile by lazy {
        val storage = StorageConfiguration.createStorage()
        ProcessLockFile(
                lockFile = storage.bundleLockFile,
                pidFile = storage.bundlePidFile)
    }

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

        if (!this.processLockFile.isOwner) {
            log.warn("Process locked, shutting down [${this.processLockFile}}")
            System.exit(-1)
            return
        }

        log.info("Acquired process lock [${this.processLockFile}]")

        log.debug("Initializing injection")

        // Injection
        Kodein.global.addImport(StorageConfiguration.module)
        Kodein.global.addImport(ApplicationConfiguration.module)
        Kodein.global.addImport(LogConfiguration.module)
        Kodein.global.addImport(RsyncConfiguration.module)
        Kodein.global.addImport(MessagingConfiguration.module)
        Kodein.global.addImport(RestClientFactory.module)
        Kodein.global.addImport(BundleConfiguration.module)
        Kodein.global.addImport(BundleUpdateConfiguration.module)
        Kodein.global.addImport(LeoBridgeConfiguration.module)
        Kodein.global.addImport(DiscoveryConfiguration.module)
        Kodein.global.addImport(ConnectionConfiguration.module)

        log.debug("Initializing executor")
        // Initialize application
        val executor: ExecutorService = Kodein.global.instance()

        log.debug("Initializing primary stage")

        // Setup stage
        this.primaryStage = primaryStage
        val splashStage = Stage()

        executor.submit {
            // Sidechained UI initialization
            executor.submit {
                log.debug("Loading fonts")
                // Load embedded fonts
                this.loadFont("/fonts/Futura-CondensedExtraBold.ttf")
                this.loadFont("/fonts/Futura-CondensedMedium.ttf")
                this.loadFont("/fonts/Futura-Medium.ttf")
                this.loadFont("/fonts/Futura-MediumItalic.ttf")
                this.loadFont("/fonts/Menlo-Regular.ttf")
                this.loadFont("/fonts/Menlo-Bold.ttf")

                val root = mainController.fxRoot

                leoBridge.ovMessageReceived.subscribe {
                    this.toForeground()
                }

                Platform.runLater {
                    // Main scene
                    //TODO: User preferences? Check if last size and position should be remembered. Dont think so (PHPR)
                    val primScreenBounds: Rectangle2D = Screen.getPrimary().visualBounds // Used to access the computers screen resolution/size

                    //Set default scene size which is used when primary stage is no more in maximized mode
                    val width = if (primScreenBounds.width < 1366.0) primScreenBounds.width - 50 else 1366.0
                    val height = if (primScreenBounds.height < 768.0) primScreenBounds.height - 50 else 768.0

                    val scene = Scene(root, width, height)

                    primaryStage.title = this.i18n.resources.getString("global.title")!!
                    primaryStage.scene = scene

                    val primaryStageIcon = Image(this.javaClass.getResourceAsStream("/images/deku-icon.256px.png"))

                    val updateIcon = {
                        primaryStage.icons.clear()
                        primaryStage.icons.add(primaryStageIcon)
                    }

                    updateIcon()
                    // TODO: workaround for OSX issue where window icon is garbled on resize
                    if (SystemUtils.IS_OS_MAC) {
                        primaryStage.widthProperty().addListener { _, _, _ -> updateIcon() }
                        primaryStage.heightProperty().addListener { _, _, _ -> updateIcon() }
                    }

                    Platform.runLater {
                        log.info("Showing primary stage")
                        // Maximizing by default is usually annoying for users and developers alike.
                        // Default should be the minimum supported size.
                        // If it's necessary to maximize by default on partidular occasions/installations it should be paremeterized.
                        primaryStage.show()
                        splashStage.close()
                    }
                }
            }
        }

        log.debug("Showing splash stage")
        splashStage.initStyle(StageStyle.TRANSPARENT)
        val splashView = MaterialProgressIndicator()
        splashView.stylesheets.add(this.javaClass.getResource("/css/leoz.css").toExternalForm())
        splashView.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS)
        splashView.background = Background.EMPTY
        val pane = AnchorPane(splashView)
        pane.background = Background.EMPTY
        AnchorPane.setTopAnchor(splashView, 10.0)
        AnchorPane.setBottomAnchor(splashView, 10.0)
        AnchorPane.setRightAnchor(splashView, 10.0)
        AnchorPane.setLeftAnchor(splashView, 10.0)
        val splashScene = Scene(pane, 94.0, 94.0)
        splashScene.fill = Color.TRANSPARENT
        splashStage.scene = splashScene

        splashStage.show()

        log.debug("Shown splash stage")

        log.info("End start")
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

