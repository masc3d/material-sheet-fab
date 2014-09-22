package org.deku.leo2;

import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Global resources
 *
 * Created by masc on 22.09.14.
 */
public class Global {
    private static Global mInstance;

    private Locale mLanguageLocale;
    private ResourceBundle mLanguageResourceBundle;

    public static Global instance() {
        if (mInstance == null) {
            synchronized(Global.class) {
                mInstance = new Global();
            }
        }
        return mInstance;
    }

    private Global() {

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
        fxml.setResources(Global.instance().getLanguageResourceBundle());
        try {
            fxml.load();
            return fxml;
        } catch (IOException e) {
            throw new RuntimeException("Could not load pane", e);
        }
    }
}
