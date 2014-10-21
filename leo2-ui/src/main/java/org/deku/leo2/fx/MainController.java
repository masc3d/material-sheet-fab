package org.deku.leo2.fx;

import javafx.animation.*;
import javafx.application.Platform;
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
import org.deku.leo2.Main;
import org.deku.leo2.Settings;
import org.deku.leo2.bridge.LeoBridge;
import org.deku.leo2.bridge.Message;
import org.deku.leo2.fx.components.SidebarController;
import org.deku.leo2.fx.modules.DebugController;
import org.deku.leo2.fx.modules.DepotMaintenanceController;
import org.deku.leo2.fx.modules.HomeController;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Main userinterface controller
 * Created by masc on 21.09.14.
 */
public class MainController extends Controller implements Initializable, SidebarController.Listener, LeoBridge.Listener {
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

    private HomeController mHomeController;
    private DepotMaintenanceController mDepotMaintenanceController;
    private DebugController mDebugController;

    /**
     * Currently active module
     */
    private ModuleController mCurrentModuleController;
    /**
     * Progress indicator request count
     */
    private Integer mProgressIndicatorActivationCount = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mSidebarController.setListener(this);
        LeoBridge.instance().getListenerEventDelegate().add(this);

        this.showModule(this.getHomeModule(), false);
    }

    /**
     * Tracks current content pane transition
     */
    Transition mContentPaneTransition;

    public DepotMaintenanceController getDepotMaintenanceModule() {
        if (mDepotMaintenanceController == null) {
            mDepotMaintenanceController = ModuleController.fromFxml("/fx/modules/DepotMaintenance.fxml");
        }
        return mDepotMaintenanceController;
    }

    public HomeController getHomeModule() {
        if (mHomeController == null) {
            mHomeController = ModuleController.fromFxml("/fx/modules/Home.fxml");
        }
        return mHomeController;
    }

    public DebugController getDebugPane() {
        if (mDebugController == null) {
            mDebugController = ModuleController.fromFxml("/fx/modules/Debug.fxml");
        }
        return mDebugController;
    }

    /**
     * Sets and shows a content pane
     *
     * @param moduleController
     * @param animated
     */
    private void showModule(ModuleController moduleController, boolean animated) {
        double duration = 500;

        if (mCurrentModuleController == moduleController)
            return;

        ModuleController oldModule = mCurrentModuleController;
        mCurrentModuleController = moduleController;

        Node node = moduleController.getRootNode();
        Node oldNode = (oldModule != null) ? oldModule.getRootNode() : null;

        this.setTitle(moduleController.getTitle(), animated);

        ArrayList<Animation> animations = new ArrayList<>();

        if (animated) {
            if (oldNode != null) {
                // Fade out old pane if there is one
                FadeTransition ftOut = new FadeTransition(Duration.millis(duration), oldNode);
                ftOut.setFromValue(1.0);
                ftOut.setToValue(0.0);
                ftOut.setOnFinished(e -> {
                    if (oldNode != mCurrentModuleController.getRootNode())
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
        } else {
            node.setOpacity(1.0);
        }

        moduleController.activate();
    }

    public void showModule(ModuleController moduleController) {
        this.showModule(moduleController, Settings.instance().isAnimationsEnabled());
    }

    /**
     * Tracks current title transition
     */
    Transition mTitleTransition;

    /**
     * Set title
     *
     * @param title
     */
    private void setTitle(String title, boolean animated) {
        if (animated) {
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
        } else {
            mTitle.setText(title);
        }
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
        ModuleController m;
        switch (itemType) {
            case Home:
                m = this.getHomeModule();
                break;
            case Depots:
                m = this.getDepotMaintenanceModule();
                break;
            case Debug:
                m = this.getDebugPane();
                break;
            default:
                throw new RuntimeException(String.format("Unknown sidebar item [%s]", itemType));
        }

        this.showModule(m);
    }

    @Override
    public void dispose() {
        super.dispose();

        if (mHomeController != null)
            mHomeController.dispose();
        if (mDepotMaintenanceController != null)
            mDepotMaintenanceController.dispose();
        if (mDebugController != null)
            mDebugController.dispose();
    }

    @Override
    public void onLeoBridgeMessageReceived(Message message) {
        Platform.runLater(() ->
        {
            Main.instance().toForeground();
            if (message.get("view").equals("depot")) {
                Integer id = (Integer) message.get("id");
                this.showModule(this.getDepotMaintenanceModule());
                this.getDepotMaintenanceModule().selectDepot(id);
            } else {
                Main.instance().showMessage(String.format("Received message [%s]", message));
            }
        });
    }
}
