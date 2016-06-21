package org.deku.leoz

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
import org.apache.commons.logging.LogFactory
import org.controlsfx.control.Notifications
import org.deku.leoz.bridge.LeoBridge
import org.deku.leoz.fx.MainController
import sx.util.Utf8ResourceBundleControl
import java.io.IOException
import java.util.*
import java.util.concurrent.Executors

/**
 * Main application entry point
 */
fun main(args: Array<String>) {
    javafx.application.Application.launch(Main::class.java, *args)
}

/**
 * Main application class
 */
class Main : Application() {

    private var fxPrimaryStage: Stage? = null
    private var _locale: Locale? = null
    private var _localizedResourceBundle: ResourceBundle? = null

    private var _mainPane: Pane? = null
    private var _mainController: MainController? = null

    /**
     * Utility method for loading a specific language resource bundle
     * @param locale
     * *
     * @return
     */
    private fun getLanguageResourceBundle(locale: Locale): ResourceBundle {
        return ResourceBundle.getBundle("i18n.leoz", locale, Utf8ResourceBundleControl())
    }

    /**
     * Intializes language related resources
     */
    private fun initializeLanguage() {
        if (_localizedResourceBundle == null || _locale == null) {
            Locale.setDefault(Locale.GERMAN)
            var locale = Locale.getDefault()
            try {
                _localizedResourceBundle = this.getLanguageResourceBundle(locale)
            } catch (e: MissingResourceException) {
                // Reverting to default language (eg. english)
                locale = Locale.ENGLISH
                _localizedResourceBundle = this.getLanguageResourceBundle(locale)
            }

            _locale = locale
        }
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
     * Application langauge resource bundle
     * @return ResourceBundle
     */
    private val localizedResourceBundle: ResourceBundle by lazy({
        this.initializeLanguage()
        _localizedResourceBundle!!
    })

    /**
     * Application locale
     * @return Locale
     */
    val locale: Locale by lazy({
        this.initializeLanguage()
        _locale!!
    })

    /**
     * Get string from localized resource bundle
     * @param key
     * *
     * @return
     */
    fun getLocalizedString(key: String): String {
        return this.localizedResourceBundle.getString(key)
    }

    /**
     * Main user interface pane
     * @return
     */
    val mainPane: Pane by lazy({
        this.initializeMainPane()
        _mainPane!!
    })

    /**
     * Main user interface controller
     * @return
     */
    val mainController: MainController by lazy({
        this.initializeMainPane()
        _mainController!!
    })

    /**
     * Utility method for loading javafx pane from fxml resource
     * @param resourcePath
     * *
     * @return
     */
    fun loadFxPane(resourcePath: String): FXMLLoader {
        val fxml = FXMLLoader(javaClass.getResource(resourcePath))
        fxml.resources = this.localizedResourceBundle
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
     * *
     * @return
     */
    fun loadFont(resourcePath: String): Font {
        return Font.loadFont(javaClass.getResource(resourcePath).toExternalForm(), 10.0)
    }

    /**
     * Scene start

     * @param primaryStage
     * *
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

        fxPrimaryStage = primaryStage

        // Load embedded fonts
        this.loadFont("/fonts/Futura-CondensedExtraBold.ttf")
        this.loadFont("/fonts/Futura-CondensedMedium.ttf")
        this.loadFont("/fonts/Futura-Medium.ttf")
        this.loadFont("/fonts/Futura-MediumItalic.ttf")

//        try {
//            val root = FXMLLoader.load<Parent>(this.javaClass.getResource("/fx/Main.fxml"))
//        } catch(e: Exception) {
//            println(e)
//            throw(e)
//        }

        // Main scene
        //TODO: User preferences? Check if last size and position should be remembered. Dont think so (PHPR)
        var primScreenBounds: Rectangle2D = Screen.getPrimary().visualBounds
        val screenX: Double = primScreenBounds.minX
        val screenY: Double = primScreenBounds.minY
        val width: Double = primScreenBounds.width
        val height: Double = primScreenBounds.height
        val scene = Scene(this.mainPane, 1366.0, 768.0)
        primaryStage.title = "Leoz UI"
        primaryStage.icons.add(Image(this.javaClass.getResourceAsStream("/images/DEKU.icon.256px.png")))
        primaryStage.scene = scene
        primaryStage.x = screenX
        primaryStage.y = screenY
        primaryStage.width = width
        primaryStage.height = height
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
        fxPrimaryStage!!.isAlwaysOnTop = true
        fxPrimaryStage!!.toFront()
        fxPrimaryStage!!.requestFocus()
        fxPrimaryStage!!.isAlwaysOnTop = false
    }

    fun showError(message: String) {
        Notifications.create().title("Leoz").text(message).showError()
    }

    fun showMessage(message: String) {
        Notifications.create().title("Leoz").text(message).showInformation()
    }

    companion object {
        private var instance: Main? = null
        private val log = LogFactory.getLog(Main::class.java)

        fun instance(): Main {
            return instance!!
        }

        /**
         * Application main entry point
         * @param args
         */

        @JvmStatic fun main(args: Array<String>) {
            Application.launch(*args)
        }
    }
}

