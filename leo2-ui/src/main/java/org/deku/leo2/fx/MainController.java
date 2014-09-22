package org.deku.leo2.fx;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.deku.leo2.Main;
import org.deku.leo2.fx.components.SidebarController;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by masc on 21.09.14.
 */
public class MainController implements Initializable, SidebarController.Listener {
    @FXML
    private Label mTitle;
    @FXML
    private AnchorPane mContentPaneContainer;
    @FXML
    private Pane mSidebar;
    @FXML
    private SidebarController mSidebarController;

    private Pane mHomePane;
    private Pane mDepotMaintenancePane;

    public enum ModuleType {
        Home,
        DepotMaintenance
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mSidebarController.setListener(this);
        this.showModule(ModuleType.Home, false);
    }

    /** Tracks current content pane transition */
    Transition mContentPaneTransition;
    Pane mContentPane;

    private Pane getDepotMaintenancePane() {
        if (mDepotMaintenancePane == null) {
            mDepotMaintenancePane = Main.instance().loadFxPane("/fx/modules/DepotMaintenance.fxml").getRoot();
        }
        return mDepotMaintenancePane;
    }

    private Pane getHomePane() {
        if (mHomePane == null) {
            mHomePane = Main.instance().loadFxPane("/fx/modules/Home.fxml").getRoot();
        }
        return mHomePane;
    }

    private void setContentPane(Pane pane, boolean animated) {
        double duration = 500;

        if (pane == mContentPane)
            return;

        Pane oldPane = mContentPane;
        mContentPane = pane;
        ArrayList<Animation> animations = new ArrayList<>();

        if (animated) {
            if (oldPane != null) {
                // Fade out old pane if there is one
                FadeTransition ftOut = new FadeTransition(Duration.millis(duration), oldPane);
                ftOut.setFromValue(1.0);
                ftOut.setToValue(0.0);
                ftOut.setOnFinished(e -> {
                    if (oldPane != mContentPane)
                        mContentPaneContainer.getChildren().remove(oldPane);
                });
                animations.add(ftOut);

                pane.setOpacity(0.0);
            }
        } else {
            mContentPaneContainer.getChildren().clear();
        }

        // Add new pane to container
        if (!mContentPaneContainer.getChildren().contains(pane))
            mContentPaneContainer.getChildren().add(pane);
        mContentPaneContainer.setTopAnchor(pane, 0.0);
        mContentPaneContainer.setBottomAnchor(pane, 0.0);
        mContentPaneContainer.setRightAnchor(pane, 0.0);
        mContentPaneContainer.setLeftAnchor(pane, 0.0);

        if (animated) {
            // Fade in new pane
            FadeTransition ftIn = new FadeTransition(Duration.millis(duration), pane);
            ftIn.setFromValue(0.0);
            ftIn.setToValue(1.0);
            ftIn.setOnFinished(e -> {
                mContentPaneTransition = null;
            });
            animations.add(ftIn);

            EventHandler<ActionEvent> evt = e -> {
                mContentPaneTransition = new ParallelTransition(animations.toArray(new Animation[0]));
                mContentPaneTransition.play();
            };

            if (mContentPaneTransition != null)
                mContentPaneTransition.setOnFinished(evt);
            else
                evt.handle(null);
        }
    }

    /** Tracks current title transition */
    Transition mTitleTransition;

    private void setTitle(String title) {
        double duration = 175;

        FadeTransition ftOut = new FadeTransition(Duration.millis(duration), mTitle);
        ftOut.setFromValue(1.0);
        ftOut.setToValue(0.0);

        ftOut.setOnFinished(e -> {
            mTitle.setText(title);
        });

        FadeTransition ftIn = new FadeTransition(Duration.millis(duration), mTitle);
        ftIn.setFromValue(0.0);
        ftIn.setToValue(1.0);
        ftIn.setOnFinished(e -> {
            mTitleTransition = null;
        });

        // Create chained sequential transition
        EventHandler<ActionEvent> evt = e -> {
            mTitleTransition = new SequentialTransition(ftOut, ftIn);
            mTitleTransition.play();
        };

        if (mTitleTransition != null)
            mTitleTransition.setOnFinished(evt);
        else
            evt.handle(null);
    }

    public void showModule(ModuleType moduleType, boolean animated) {
        switch (moduleType) {
            case Home:
                this.setTitle("Leo 2");
                this.setContentPane(this.getHomePane(), true);
                break;
            case DepotMaintenance:
                this.setTitle("Depots");
                this.setContentPane(this.getDepotMaintenancePane(), true);
        }
    }

    public void showDepotMaintenanceModule() {
        this.setTitle("Depots");
        this.setContentPane(this.getDepotMaintenancePane(), true);
    }

    @Override
    public void OnSidebarItemSelected(SidebarController.ItemType itemType) {
        ModuleType md;
        switch(itemType) {
            case Home:
                md = ModuleType.Home;
                break;
            case Depots:
                md = ModuleType.DepotMaintenance;
                break;
            default:
                throw new RuntimeException("Unknown sidebar item");
        }
        this.showModule(md, true);
    }
}
