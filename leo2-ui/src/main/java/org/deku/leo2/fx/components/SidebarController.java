package org.deku.leo2.fx.components;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;

import java.net.URL;
import java.util.EventListener;
import java.util.ResourceBundle;

/**
 * Created by masc on 22.09.14.
 */
public class SidebarController implements Initializable {
    @FXML
    Accordion mMenuAccordion;
    @FXML
    TitledPane mMenuPane;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mMenuAccordion.setExpandedPane(mMenuPane);
    }
}
