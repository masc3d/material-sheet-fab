package org.deku.leo2.fx.modules;

import javafx.fxml.Initializable;
import org.deku.leo2.fx.ModuleController;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by masc on 27.09.14.
 */
public class DebugController extends ModuleController implements Initializable {
    @Override
    public String getTitle() {
        return "Debug";
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
