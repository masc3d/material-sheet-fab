package org.deku.leoz

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
import org.deku.leoz.bridge.LeoBridge
import org.deku.leoz.config.Configurations
import org.deku.leoz.fx.MainController
import org.slf4j.LoggerFactory
import sx.util.Utf8ResourceBundleControl
import java.io.IOException
import java.util.*
import java.util.concurrent.Executors

/**
 * Main application class
 */
class Main : Application() {
    companion object {
        private var instance: Main? = null
        private val log = LoggerFactory.getLogger(Main::class.java)

        fun instance(): Main {
            return instance!!
        }

        /**
         * Application main entry point
         * @param args
         */
        @JvmStatic fun main(args: Array<String>) {
            javafx.application.Application.launch(Main::class.java, *args)
        }
    }

    private var _fxPrimaryStage: Stage? = null

    private var _mainPane: Pane? = null
    private var _mainController: MainController? = null

    private val i18n: Localization by Kodein.global.lazy.instance()

    /**
     * Utility method for loading a specific language resource bundle
     * @param locale
     * @return
     */
    private fun getLanguageResourceBundle(locale: Locale): ResourceBundle {
        return ResourceBundle.getBundle("i18n.leoz", locale, Utf8ResourceBundleControl())
    }

    /**
     * Initialize main pane/controller
     */
    private fun initializeMainPane() {
        if (_mainPane == null || _mainController == null) {
            // Load main UI
            val fxmlMain = this.loadFxPane("/fx/Main.fxml")
            _mainPane = fxmlMain.getRoot<Pane>()
            _mainController = fxmlMain.getController<MainController>()
        }
    }

    /**
     * Main user interface pane
     * @return
     */
    val mainPane: Pane by lazy {
        this.initializeMainPane()
        _mainPane!!
    }

    /**
     * Main user interface controller
     * @return
     */
    val mainController: MainController by lazy {
        this.initializeMainPane()
        _mainController!!
    }

    /**
     * Utility method for loading javafx pane from fxml resource
     * @param resourcePath
     * @return
     */
    fun loadFxPane(resourcePath: String): FXMLLoader {
        val fxml = FXMLLoader(javaClass.getResource(resourcePath))
        fxml.resources = this.i18n.resources
        try {
            fxml.load<Parent>()
            return fxml
        } catch (e: IOException) {
            throw RuntimeException("Could not load pane", e)
        }

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
        instance = this

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

        // Setup injection
        Kodein.global.addImport(Configurations.application)
        Kodein.global.addImport(Configurations.messenging)

        // Setup stage
        _fxPrimaryStage = primaryStage

        // Load embedded fonts
        this.loadFont("/fonts/Futura-CondensedExtraBold.ttf")
        this.loadFont("/fonts/Futura-CondensedMedium.ttf")
        this.loadFont("/fonts/Futura-Medium.ttf")
        this.loadFont("/fonts/Futura-MediumItalic.ttf")

        // Main scene
        //TODO: User preferences? Check if last size and position should be remembered. Dont think so (PHPR)
        val primScreenBounds: Rectangle2D = Screen.getPrimary().visualBounds //Used to access the computers screen resolution/size
        //Set default scene size which is used when primary stage is no more in maximized mode

        val width = if(primScreenBounds.width < 1366.0) primScreenBounds.width - 50 else 1366.0
        val height = if(primScreenBounds.height < 768.0) primScreenBounds.height - 50 else 768.0
        val scene = Scene(this.mainPane, width, height)

        primaryStage.title = this.i18n.resources.getString("global.title")!!
        primaryStage.icons.add(Image(this.javaClass.getResourceAsStream("/images/DEKU.icon.256px.png")))
        primaryStage.scene = scene

        // Maximizing by default is usually annoying for users and developers alike.
        // Default should be the minimum supported size.
        // If it's necessary to maximize by default on partidular occasionas/installations it should be paremeterized.
        primaryStage.show()

        Executors.newSingleThreadExecutor().submit {
            try {
                LeoBridge.instance().start()
            } catch (e: IOException) {
                log.error(e.message, e)
            }
        }
    }

    @Throws(Exception::class)
    override fun stop() {
        _mainController!!.close()
        LeoBridge.instance().stop()
        super.stop()
    }

    /**
     * Makes primary stage the current desktop foreground window
     */
    fun toForeground() {
        // toFront doesn't suffice
        _fxPrimaryStage!!.isAlwaysOnTop = true
        _fxPrimaryStage!!.toFront()
        _fxPrimaryStage!!.requestFocus()
        _fxPrimaryStage!!.isAlwaysOnTop = false
    }

    fun showError(message: String) {
        Notifications.create().title("Leoz").text(message).showError()
    }

    fun showMessage(message: String) {
        Notifications.create().title("Leoz").text(message).showInformation()
    }
}

