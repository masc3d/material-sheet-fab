package org.deku.leo2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.deku.leo2.fx.MainController;

import java.io.IOException;
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

    private ResourceBundle getLanguageResourceBundle(Locale locale) {
        return ResourceBundle.getBundle("i18n.leo2", locale);
    }

    /**
     * Intializes language related resources
     */
    private void initializeLanguage() {
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
     * Locale relating to language
     * @return Locale
     */
    public Locale getLanguageLocale() {
        if (mLanguageLocale == null) {
            this.initializeLanguage();
        }

        return mLanguageLocale;
    }

    /**
     * ResourceBundle relating to language
     * @return ResourceBundle
     */
    public ResourceBundle getLanguageResourceBundle() {
        if (mLanguageResourceBundle == null) {
            this.initializeLanguage();
        }
        return mLanguageResourceBundle;
    }

    /** Dynamically load javafx pane from fxml resource */
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

    public Pane getMainPane() {
        if (mMainPane == null) {
            this.initializeMainPane();
        }
        return mMainPane;
    }

    public MainController getMainController() {
        if (mMainController == null) {
            this.initializeMainPane();
        }
        return mMainController;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        mInstance = this;

        // Main scene
        Scene scene = new Scene(this.getMainPane(), 1600, 800);
        primaryStage.setTitle("leo2");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        mMainController.dispose();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

