package org.deku.leoz.fx.modules;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import org.deku.leoz.Main;
import org.deku.leoz.fx.ModuleController;
import org.deku.leoz.fx.components.DepotDetailsController;
import org.deku.leoz.fx.components.DepotListController;
import org.deku.leoz.rest.entities.internal.v1.Depot;

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

    public void selectDepot(Integer id) {
        mDepotListController.requestDepotSelection(id);
    }

    @Override
    public void dispose() {
        super.dispose();
        mDepotListController.dispose();
    }
}
