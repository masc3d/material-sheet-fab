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
import org.deku.leo2.Global;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mSidebarController.setListener(this);
    }

    Pane mContentPane;
    /** Tracks current content pane transition */
    Transition mContentPaneTransition;

    public void setContentPane(Pane pane) {
        double duration = 500;

        if (pane == mContentPane)
            return;

        Pane oldPane = mContentPane;
        mContentPane = pane;

        ArrayList<Animation> animations = new ArrayList<>();
        if (oldPane!= null) {
            FadeTransition ftOut = new FadeTransition(Duration.millis(duration), oldPane);
            ftOut.setFromValue(1.0);
            ftOut.setToValue(0.0);
            ftOut.setOnFinished(e -> {
                if (oldPane != mContentPane)
                    mContentPaneContainer.getChildren().remove(oldPane);
            });
            animations.add(ftOut);
        }

        pane.setOpacity(0.0);
        if (!mContentPaneContainer.getChildren().contains(pane))
            mContentPaneContainer.getChildren().add(pane);
        mContentPaneContainer.setTopAnchor(pane, 0.0);
        mContentPaneContainer.setBottomAnchor(pane, 0.0);
        mContentPaneContainer.setRightAnchor(pane, 0.0);
        mContentPaneContainer.setLeftAnchor(pane, 0.0);

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

    /** Tracks current title transition */
    Transition mTitleTransition;

    public void setTitle(String title) {
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

    @Override
    public void OnSidebarItemSelected(SidebarController.ItemType itemType) {
        switch(itemType) {
            case Home:
                this.setTitle("Leo 2");
                this.setContentPane(this.getHomePane());
                break;
            case Depots:
                this.setTitle("Depots");
                this.setContentPane(this.getDepotMaintenancePane());
                break;
        }
    }

    private Pane getDepotMaintenancePane() {
        if (mDepotMaintenancePane == null) {
            mDepotMaintenancePane = Global.instance().loadFxPane("/fx/content/DepotMaintenance.fxml").getRoot();
        }
        return mDepotMaintenancePane;
    }

    private Pane getHomePane() {
        if (mHomePane == null) {
            mHomePane = Global.instance().loadFxPane("/fx/content/Home.fxml").getRoot();
        }
        return mHomePane;
    }
}
