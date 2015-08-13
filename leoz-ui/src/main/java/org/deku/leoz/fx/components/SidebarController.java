package org.deku.leoz.fx.components;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import org.deku.leoz.fx.ModuleController;
import org.deku.leoz.fx.modules.DebugController;
import org.deku.leoz.fx.modules.DepotMaintenanceController;
import org.deku.leoz.fx.modules.HomeController;

import java.net.URL;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by masc on 22.09.14.
 */
public class SidebarController implements Initializable {
    @FXML
    Accordion mMenuAccordion;
    @FXML
    TitledPane mMenuPane;
    @FXML
    Button mHomeButton;
    @FXML
    Button mDepotsButton;
    @FXML
    Button mDebugButton;

    List<Button> mButtons = new ArrayList<Button>();

    public enum ItemType {
        Home,
        Depots,
        Debug
    }

    public interface Listener extends EventListener {
        void OnSidebarItemSelected(ItemType itemType);
    }

    private Listener mListener;

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void onHomeButton() {
        if (mListener != null)
            mListener.OnSidebarItemSelected(ItemType.Home);
    }

    public void onDepotButton() {
        if (mListener != null)
            mListener.OnSidebarItemSelected(ItemType.Depots);
    }

    public void onDebugButton() {
        if (mListener != null)
            mListener.OnSidebarItemSelected(ItemType.Debug);
    }

    public void highlightByController(ModuleController module) {
        for (Button b : mButtons)
            b.getStyleClass().remove("leoz-sidebar-selection");

        Button selection = null;
        if (module instanceof HomeController) {
            selection = mHomeButton;
        } else if (module instanceof DepotMaintenanceController) {
            selection = mDepotsButton;
        } else if (module instanceof DebugController) {
            selection = mDebugButton;
        }

        if (selection != null)
            selection.getStyleClass().add("leoz-sidebar-selection");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mMenuAccordion.setExpandedPane(mMenuPane);

        mButtons.add(mHomeButton);
        mButtons.add(mDepotsButton);
        mButtons.add(mDebugButton);
    }
}
