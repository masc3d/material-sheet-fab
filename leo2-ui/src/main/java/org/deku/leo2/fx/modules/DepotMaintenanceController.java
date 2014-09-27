package org.deku.leo2.fx.modules;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import org.deku.leo2.Main;
import org.deku.leo2.fx.ModuleController;
import org.deku.leo2.fx.components.DepotDetailsController;
import org.deku.leo2.fx.components.DepotListController;
import org.deku.leo2.rest.v1.entities.Depot;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by masc on 22.09.14.
 */
public class DepotMaintenanceController extends ModuleController implements Initializable, DepotListController.Listener {
    @FXML
    private Node mDepotList;
    @FXML
    private DepotListController mDepotListController;
    @FXML
    private Node mDepotDetails;
    @FXML
    private DepotDetailsController mDepotDetailsController;

    @Override
    public String getTitle() {
        return Main.instance().getLocalizedString("menu.depots");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mDepotListController.setListener(this);
    }

    @Override
    public void onActivation() {
        mDepotListController.activate();
    }

    @Override
    public void onDepotListItemSelected(Depot depot) {
        mDepotDetailsController.setDepot(depot);
    }

    @Override
    public void dispose() {
        super.dispose();
        mDepotListController.dispose();
    }
}
