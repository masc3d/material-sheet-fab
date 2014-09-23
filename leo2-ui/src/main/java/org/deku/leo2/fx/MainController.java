package org.deku.leo2.fx;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.deku.leo2.fx.components.SidebarController;
import org.deku.leo2.fx.modules.DepotMaintenanceController;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by masc on 21.09.14.
 */
public class MainController extends Controller implements Initializable, SidebarController.Listener {
    @FXML
    private Label mTitle;
    @FXML
    private AnchorPane mContentPaneContainer;
    @FXML
    private Pane mSidebar;
    @FXML
    private SidebarController mSidebarController;
    @FXML
    private ProgressIndicator mProgressIndicator;

    private Module<Object> mHomePane;
    private Module<DepotMaintenanceController> mDepotMaintenancePane;

    /**
     * Currently active module
     */
    private ModuleType mCurrentModule = ModuleType.None;

    private Integer mProgressIndicatorActivationCount = 0;

    public enum ModuleType {
        None,
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
    Node mContentNode;

    private Module<DepotMaintenanceController> getDepotMaintenanceModule() {
        if (mDepotMaintenancePane == null) {
            mDepotMaintenancePane = Module.fromFxml("/fx/modules/DepotMaintenance.fxml", DepotMaintenanceController.class);
        }
        return mDepotMaintenancePane;
    }

    private Module<Object> getHomeModule() {
        if (mHomePane == null) {
            mHomePane = Module.fromFxml("/fx/modules/Home.fxml", Object.class);
        }
        return mHomePane;
    }

    /**
     * Sets and shows a content pane
     * @param module
     * @param animated
     */
    private void setModule(Module<?> module, boolean animated) {
        double duration = 500;

        Node node = module.getNode();

        Node oldNode = mContentNode;
        mContentNode = node;

        ArrayList<Animation> animations = new ArrayList<>();

        if (animated) {
            if (oldNode != null) {
                // Fade out old pane if there is one
                FadeTransition ftOut = new FadeTransition(Duration.millis(duration), oldNode);
                ftOut.setFromValue(1.0);
                ftOut.setToValue(0.0);
                ftOut.setOnFinished(e -> {
                    if (oldNode != mContentNode)
                        mContentPaneContainer.getChildren().remove(oldNode);
                });
                animations.add(ftOut);

                node.setOpacity(0.0);
            }
        } else {
            mContentPaneContainer.getChildren().clear();
        }

        // Add new pane to container
        if (!mContentPaneContainer.getChildren().contains(node))
            mContentPaneContainer.getChildren().add(node);
        mContentPaneContainer.setTopAnchor(node, 0.0);
        mContentPaneContainer.setBottomAnchor(node, 0.0);
        mContentPaneContainer.setRightAnchor(node, 0.0);
        mContentPaneContainer.setLeftAnchor(node, 0.0);

        if (animated) {
            // Fade in new pane
            FadeTransition ftIn = new FadeTransition(Duration.millis(duration), node);
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

        if (module.getController() instanceof Controller) {
            Controller c = (Controller)module.getController();
            c.activate();
        }
    }

    /** Tracks current title transition */
    Transition mTitleTransition;

    /**
     * Set title
     * @param title
     */
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

    /**
     * Show user interface module
     * @param moduleType UI module type
     * @param animated Animated or not
     */
    public void showModule(ModuleType moduleType, boolean animated) {
        if (mCurrentModule == moduleType)
            return;

        switch (moduleType) {
            case Home:
                this.setTitle("Leo 2");
                this.setModule(this.getHomeModule(), true);
                break;
            case DepotMaintenance:
                this.setTitle("Depots");
                this.setModule(this.getDepotMaintenanceModule(), true);
        }

        mCurrentModule = moduleType;
    }

    /**
     * Request progress indication
     * Each call to request requires release to be called for the indicator to disappear as soon as all consumers released it
     */
    public void requestProgressIndicator() {
        mProgressIndicatorActivationCount++;
        mProgressIndicator.setVisible(true);
    }

    /**
     * Release progress indication
     */
    public void releaseProgressIndicator() {
        if (--mProgressIndicatorActivationCount <= 0) {
            mProgressIndicator.setVisible(false);
            mProgressIndicatorActivationCount = 0;
        }
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

    @Override
    public void dispose() {
        super.dispose();

        if (mHomePane != null)
            mHomePane.dispose();
        if (mDepotMaintenancePane != null)
            mDepotMaintenancePane.dispose();
    }
}
