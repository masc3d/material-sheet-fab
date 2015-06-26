package org.deku.leo2.fx.modules;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.Main;
import org.deku.leo2.Settings;
import org.deku.leo2.bridge.LeoBridge;
import org.deku.leo2.bridge.Message;
import org.deku.leo2.fx.ModuleController;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by masc on 27.09.14.
 */
public class DebugController extends ModuleController implements Initializable {
    Log mLog = LogFactory.getLog(DebugController.class);

    @FXML
    private TextField mLeoBridgeMessageTextField;
    @FXML
    private CheckBox mUiAnimationsEnabled;

    @Override
    public String getTitle() {
        return "Debug";
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mUiAnimationsEnabled.setSelected(Settings.instance().isAnimationsEnabled());
        mUiAnimationsEnabled.selectedProperty().addListener((o, ov, nv) -> {
            Settings.instance().setAnimationsEnabled(nv);
        });
    }

    public void onLeoBridgeSend() {
        try {
            LeoBridge.instance().sendMessage(new Message(mLeoBridgeMessageTextField.getText()));
        } catch(Exception e) {
            mLog.error(e.getMessage(), e);
        }
    }

    public void onLeoBridgeSendComplexMessage() {
        Message msg = new Message();
        msg.put("text", "hey");
        msg.put("int", 123);
        msg.put("float", 34.5);
        msg.put("date", new Date());
        try {
            LeoBridge.instance().sendMessage(msg);
        } catch(Exception e) {
            mLog.error(e.getMessage(), e);
        }
    }

    public void onDepotSelect() {
        Main.instance().getMainController().showModule(Main.instance().getMainController().getDepotMaintenanceModule());
        Main.instance().getMainController().getDepotMaintenanceModule().selectDepot(800);
    }
}
