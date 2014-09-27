package org.deku.leo2.fx.modules;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.deku.leo2.bridge.LeoBridge;
import org.deku.leo2.fx.ModuleController;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Created by masc on 27.09.14.
 */
public class DebugController extends ModuleController implements Initializable {
    Logger mLog = Logger.getLogger(DebugController.class.getName());

    @FXML
    private TextField mLeoBridgeMessageTextField;

    @Override
    public String getTitle() {
        return "Debug";
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void onLeoBridgeSend() {
        try {
            LeoBridge.instance().sendMessage(mLeoBridgeMessageTextField.getText());
        } catch(Exception e) {
            mLog.severe(e.getMessage());
        }
    }
}
