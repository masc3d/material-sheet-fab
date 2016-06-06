package org.deku.leoz

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.layout.Pane
import javafx.scene.text.Font
import javafx.stage.Stage
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.controlsfx.control.Notifications
import org.deku.leoz.bridge.LeoBridge
import org.deku.leoz.fx.MainController
import sx.util.Utf8ResourceBundleControl

import java.io.IOException
import java.util.Locale
import java.util.MissingResourceException
import java.util.ResourceBundle
import java.util.concurrent.Executors

/**
 * Main application entry point
 */
class Main : Application() {

    private var mPrimaryStage: Stage? = null
    private var mLocale: Locale? = null
    private var mLocalizedResourceBundle: ResourceBundle? = null

    private var mMainPane: Pane? = null
    private var mMainController: MainController? = null

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
        Locale.setDefault(Locale.GERMAN)
        var locale = Locale.getDefault()
        try {
            mLocalizedResourceBundle = this.getLanguageResourceBundle(locale)
        } catch (e: MissingResourceException) {
            // Reverting to default language (eg. english)
            locale = Locale.ENGLISH
            mLocalizedResourceBundle = this.getLanguageResourceBundle(locale)
        }

        mLocale = locale
    }

    /**
     * Initialize main pane/controller
     */
    private fun initializeMainPane() {
        // Load main UI
        val fxmlMain = this.loadFxPane("/fx/Main.fxml")
        mMainPane = fxmlMain.getRoot<Pane>()
        mMainController = fxmlMain.getController<MainController>()
    }

    /**
     * Application langauge resource bundle
     * @return ResourceBundle
     */
    private val localizedResourceBundle: ResourceBundle
        get() {
            if (mLocalizedResourceBundle == null) {
                this.initializeLanguage()
            }
            return mLocalizedResourceBundle!!
        }

    /**
     * Application locale
     * @return Locale
     */
    val locale: Locale
        get() {
            if (mLocale == null) {
                this.initializeLanguage()
            }

            return mLocale!!
        }

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
    val mainPane: Pane
        get() {
            if (mMainPane == null) {
                this.initializeMainPane()
            }
            return mMainPane!!
        }

    /**
     * Main user interface controller
     * @return
     */
    val mainController: MainController
        get() {
            if (mMainController == null) {
                this.initializeMainPane()
            }
            return mMainController!!
        }

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
            fxml.load<Any>()
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
        mInstance = this

        val setup = Setup()
        val command = setup.parse(this.parameters.raw.toTypedArray())
        if (command != null) {
            try {
                command.run()
                System.exit(0)
            } catch (e: Exception) {
                mLogger.error(e.message, e)
                System.exit(-1)
            }

            return
        }

        mPrimaryStage = primaryStage

        // Load embedded fonts
        this.loadFont("/fonts/Futura-CondensedExtraBold.ttf")
        this.loadFont("/fonts/Futura-CondensedMedium.ttf")
        this.loadFont("/fonts/Futura-Medium.ttf")
        this.loadFont("/fonts/Futura-MediumItalic.ttf")

        // Main scene
        val scene = Scene(this.mainPane, 1600.0, 800.0)
        primaryStage.title = "Leoz UI"
        primaryStage.icons.add(Image(this.javaClass.getResourceAsStream("/images/DEKU.icon.256px.png")))
        primaryStage.scene = scene
        primaryStage.show()

        Executors.newSingleThreadExecutor().submit {
            try {
                LeoBridge.instance().start()
            } catch (e: IOException) {
                mLogger.error(e.message, e)
            }
        }
    }

    @Throws(Exception::class)
    override fun stop() {
        mMainController!!.close()
        LeoBridge.instance().stop()
        super.stop()
    }

    /**
     * Makes primary stage the current desktop foreground window
     */
    fun toForeground() {
        // toFront doesn't suffice
        mPrimaryStage!!.isAlwaysOnTop = true
        mPrimaryStage!!.toFront()
        mPrimaryStage!!.requestFocus()
        mPrimaryStage!!.isAlwaysOnTop = false
    }

    fun showError(message: String) {
        Notifications.create().title("Leoz").text(message).showError()
    }

    fun showMessage(message: String) {
        Notifications.create().title("Leoz").text(message).showInformation()
    }

    companion object {
        private var mInstance: Main? = null
        private val mLogger = LogFactory.getLog(Main::class.java)

        fun instance(): Main {
            return mInstance!!
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

