package org.deku.leo2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.deku.leo2.bridge.LeoBridge;
import org.deku.leo2.fx.MainController;
import org.sx.util.UTF8ResourceBundleControl;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Main application entry point
 */
public class Main extends Application {
    private static Main mInstance;

    private Locale mLanguageLocale;
    private ResourceBundle mLanguageResourceBundle;

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
        return ResourceBundle.getBundle("i18n.leo2", locale, new UTF8ResourceBundleControl());
    }

    /**
     * Intializes language related resources
     */
    private void initializeLanguage() {
        Locale.setDefault(Locale.GERMAN);
        Locale locale = Locale.getDefault();
        try {
            mLanguageResourceBundle = this.getLanguageResourceBundle(locale);
        } catch(MissingResourceException e) {
            // Reverting to default language (eg. english)
            locale = Locale.ENGLISH;
            mLanguageResourceBundle = this.getLanguageResourceBundle(locale);
        }
        mLanguageLocale = locale;
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
     * Application locale
     * @return Locale
     */
    public Locale getLanguageLocale() {
        if (mLanguageLocale == null) {
            this.initializeLanguage();
        }

        return mLanguageLocale;
    }

    /**
     * Application langauge resource bundle
     * @return ResourceBundle
     */
    public ResourceBundle getLanguageResourceBundle() {
        if (mLanguageResourceBundle == null) {
            this.initializeLanguage();
        }
        return mLanguageResourceBundle;
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
        fxml.setResources(this.getLanguageResourceBundle());
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

        // Load embedded fonts
        this.loadFont("/fonts/Futura-CondensedExtraBold.ttf");
        this.loadFont("/fonts/Futura-CondensedMedium.ttf");
        this.loadFont("/fonts/Futura-Medium.ttf");
        this.loadFont("/fonts/Futura-MediumItalic.ttf");

        // Main scene
        Scene scene = new Scene(this.getMainPane(), 1600, 800);
        primaryStage.setTitle("leo2");
        primaryStage.setScene(scene);
        primaryStage.show();

        LeoBridge.instance().start();
    }

    @Override
    public void stop() throws Exception {
        mMainController.dispose();
        super.stop();
    }

}

