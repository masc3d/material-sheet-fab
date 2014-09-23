package org.deku.leo2.fx.modules;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import org.deku.leo2.fx.Controller;
import org.deku.leo2.fx.components.DepotListController;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by masc on 22.09.14.
 */
public class DepotMaintenanceController extends Controller implements Initializable {
    @FXML
    Pane mDepotList;
    @FXML
    DepotListController mDepotListController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public void onActivation() {
        mDepotListController.activate();
    }

    @Override
    public void dispose() {
        super.dispose();
        mDepotListController.dispose();
    }
}
