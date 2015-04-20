package org.deku.leo2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import org.deku.leo2.bridge.LeoBridge;
import org.deku.leo2.fx.MainController;
import sx.util.Utf8ResourceBundleControl;

import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main application entry point
 */
public class Main extends Application {
    private static Main mInstance;
    private static Logger mLogger = Logger.getLogger(Main.class.getName());

    private Stage mPrimaryStage;
    private Locale mLocale;
    private ResourceBundle mLocalizedResourceBundle;

    private Pane mMainPane;
    private MainController mMainController;

    public static Main instance() {
        return mInstance;
    }

    /**
     * Utility method for loading a specific language resource bundle
     * @param locale
     * @return
     */
    private ResourceBundle getLanguageResourceBundle(Locale locale) {
        return ResourceBundle.getBundle("i18n.leo2", locale, new Utf8ResourceBundleControl());
    }

    /**
     * Intializes language related resources
     */
    private void initializeLanguage() {
        Locale.setDefault(Locale.GERMAN);
        Locale locale = Locale.getDefault();
        try {
            mLocalizedResourceBundle = this.getLanguageResourceBundle(locale);
        } catch(MissingResourceException e) {
            // Reverting to default language (eg. english)
            locale = Locale.ENGLISH;
            mLocalizedResourceBundle = this.getLanguageResourceBundle(locale);
        }
        mLocale = locale;
    }

    /**
     * Initialize main pane/controller
     */
    private void initializeMainPane() {
        // Load main UI
        FXMLLoader fxmlMain = this.loadFxPane("/fx/Main.fxml");
        mMainPane = fxmlMain.getRoot();
        mMainController = fxmlMain.getController();
    }

    /**
     * Application langauge resource bundle
     * @return ResourceBundle
     */
    private ResourceBundle getLocalizedResourceBundle() {
        if (mLocalizedResourceBundle == null) {
            this.initializeLanguage();
        }
        return mLocalizedResourceBundle;
    }

    /**
     * Application locale
     * @return Locale
     */
    public Locale getLocale() {
        if (mLocale == null) {
            this.initializeLanguage();
        }

        return mLocale;
    }

    /**
     * Get string from localized resource bundle
     * @param key
     * @return
     */
    public String getLocalizedString(String key) {
        return this.getLocalizedResourceBundle().getString(key);
    }

    /**
     * Main user interface pane
     * @return
     */
    public Pane getMainPane() {
        if (mMainPane == null) {
            this.initializeMainPane();
        }
        return mMainPane;
    }

    /**
     * Main user interface controller
     * @return
     */
    public MainController getMainController() {
        if (mMainController == null) {
            this.initializeMainPane();
        }
        return mMainController;
    }

    /**
     * Utility method for loading javafx pane from fxml resource
     * @param resourcePath
     * @return
     */
    public FXMLLoader loadFxPane(String resourcePath) {
        FXMLLoader fxml = new FXMLLoader(getClass().getResource(resourcePath));
        fxml.setResources(this.getLocalizedResourceBundle());
        try {
            fxml.load();
            return fxml;
        } catch (IOException e) {
            throw new RuntimeException("Could not load pane", e);
        }
    }

    /**
     * Utility method for loading font. Once a font has been loaded (even without explicitly using it),
     * it can be referenced within css stylesheetss.
     * @param resourcePath
     * @return
     */
    public Font loadFont(String resourcePath) {
        return Font.loadFont(getClass().getResource(resourcePath).toExternalForm(), 10);
    }

    /**
     * Application main entry point
     * @param args
     */

    public static void main(String[] args) {
        System.out.println("start");
        launch(args);
    }

    /**
     * Scene start
     *
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        mInstance = this;
        mPrimaryStage = primaryStage;

        // Load embedded fonts
        this.loadFont("/fonts/Futura-CondensedExtraBold.ttf");
        this.loadFont("/fonts/Futura-CondensedMedium.ttf");
        this.loadFont("/fonts/Futura-Medium.ttf");
        this.loadFont("/fonts/Futura-MediumItalic.ttf");

        System.out.println("meh1");

        // Main scene
        Scene scene = new Scene(this.getMainPane(), 1600, 800);
        primaryStage.setTitle("leo2");
        primaryStage.setScene(scene);
        primaryStage.show();

        System.out.println("meh2");

        Executors.newSingleThreadExecutor().submit( () ->
        {
            try {
                LeoBridge.instance().start();
            } catch (IOException e) {
                mLogger.log(Level.SEVERE, e.getMessage(), e);
            }
        });
    }

    @Override
    public void stop() throws Exception {
        mMainController.dispose();
        LeoBridge.instance().stop();
        super.stop();
    }

    /**
     * Makes primary stage the current desktop foreground window
     */
    public void toForeground() {
        // toFront doesn't suffice
        mPrimaryStage.setAlwaysOnTop(true);
        mPrimaryStage.toFront();
        mPrimaryStage.requestFocus();
        mPrimaryStage.setAlwaysOnTop(false);
    }

    public void showError(String message) {
        Notifications.create()
                .title("Leo2")
                .text(message)
                .showError();
    }

    public void showMessage(String message) {
        Notifications.create()
                .title("Leo2")
                .text(message)
                .showInformation();
    }
}

