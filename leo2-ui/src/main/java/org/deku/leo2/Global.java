package org.deku.leo2;

import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Leo2 global resources
 *
 * Created by masc on 22.09.14.
 */
public class Global {
    private static Global mInstance;

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

}
