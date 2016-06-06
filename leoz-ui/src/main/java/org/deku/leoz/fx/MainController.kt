package org.deku.leoz.fx

import javafx.animation.*
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.ProgressIndicator
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.util.Duration
import org.deku.leoz.Main
import org.deku.leoz.Settings
import org.deku.leoz.bridge.LeoBridge
import org.deku.leoz.bridge.Message
import org.deku.leoz.fx.components.SidebarController
import org.deku.leoz.fx.modules.DebugController
import org.deku.leoz.fx.modules.DepotMaintenanceController
import org.deku.leoz.fx.modules.HomeController

import java.net.URL
import java.util.ArrayList
import java.util.ResourceBundle

/**
 * Main userinterface controller
 * Created by masc on 21.09.14.
 */
class MainController : Controller(), Initializable, SidebarController.Listener, LeoBridge.Listener {
    @FXML
    private val mTitle: Label? = null
    @FXML
    private val mContentPaneContainer: AnchorPane? = null
    @FXML
    private val mSidebar: Pane? = null
    @FXML
    private val mSidebarController: SidebarController? = null
    @FXML
    private val mProgressIndicator: ProgressIndicator? = null

    private var mHomeController: HomeController? = null
    private var mDepotMaintenanceController: DepotMaintenanceController? = null
    private var mDebugController: DebugController? = null

    /**
     * Currently active module
     */
    private var mCurrentModuleController: ModuleController? = null
    /**
     * Progress indicator request count
     */
    private var mProgressIndicatorActivationCount: Int = 0

    override fun initialize(location: URL, resources: ResourceBundle) {
        mSidebarController!!.setListener(this)
        LeoBridge.instance().listenerEventDelegate.add(this)

        this.showModule(this.homeModule, false)
    }

    /**
     * Tracks current content pane transition
     */
    internal var mContentPaneTransition: Transition? = null

    val depotMaintenanceModule: DepotMaintenanceController
        get() {
            if (mDepotMaintenanceController == null) {
                mDepotMaintenanceController = ModuleController.fromFxml<DepotMaintenanceController>("/fx/modules/DepotMaintenance.fxml")
            }
            return mDepotMaintenanceController!!
        }

    val homeModule: HomeController
        get() {
            if (mHomeController == null) {
                mHomeController = ModuleController.fromFxml<HomeController>("/fx/modules/Home.fxml")
            }
            return mHomeController!!
        }

    val debugPane: DebugController
        get() {
            if (mDebugController == null) {
                mDebugController = ModuleController.fromFxml<DebugController>("/fx/modules/Debug.fxml")
            }
            return mDebugController!!
        }

    /**
     * Sets and shows a content pane

     * @param moduleController
     * *
     * @param animated
     */
    private fun showModule(moduleController: ModuleController, animated: Boolean) {
        val duration = 500.0

        if (mCurrentModuleController === moduleController)
            return

        val oldModule = mCurrentModuleController
        mCurrentModuleController = moduleController

        val node = moduleController.rootNode
        val oldNode = oldModule?.rootNode

        this.setTitle(moduleController.title, animated)

        val animations = ArrayList<Animation>()

        if (animated) {
            if (oldNode != null) {
                // Fade out old pane if there is one
                val ftOut = FadeTransition(Duration.millis(duration), oldNode)
                ftOut.fromValue = 1.0
                ftOut.toValue = 0.0
                ftOut.setOnFinished { e ->
                    if (oldNode !== mCurrentModuleController!!.rootNode)
                        mContentPaneContainer!!.children.remove(oldNode)
                }
                animations.add(ftOut)

                node!!.opacity = 0.0
            }
        } else {
            mContentPaneContainer!!.children.clear()
        }

        // Add new pane to container
        if (!mContentPaneContainer!!.children.contains(node))
            mContentPaneContainer.children.add(node)
        AnchorPane.setTopAnchor(node, 0.0)
        AnchorPane.setBottomAnchor(node, 0.0)
        AnchorPane.setRightAnchor(node, 0.0)
        AnchorPane.setLeftAnchor(node, 0.0)

        if (animated) {
            // Fade in new pane
            val ftIn = FadeTransition(Duration.millis(duration), node)
            ftIn.fromValue = 0.0
            ftIn.toValue = 1.0
            ftIn.setOnFinished { e -> mContentPaneTransition = null }
            animations.add(ftIn)

            val evt: EventHandler<ActionEvent> = EventHandler {
                mContentPaneTransition = ParallelTransition(*animations.toTypedArray())
                mContentPaneTransition!!.play()
            }

            if (mContentPaneTransition != null)
                mContentPaneTransition!!.setOnFinished(evt)
            else
                evt.handle(null)
        } else {
            node!!.opacity = 1.0
        }

        moduleController.activate()
        mSidebarController!!.highlightByController(moduleController)
    }

    fun showModule(moduleController: ModuleController) {
        this.showModule(moduleController, Settings.instance().isAnimationsEnabled)
    }

    /**
     * Tracks current title transition
     */
    internal var mTitleTransition: Transition? = null

    /**
     * Set title

     * @param title
     */
    private fun setTitle(title: String, animated: Boolean) {
        if (animated) {
            val duration = 175.0

            val ftOut = FadeTransition(Duration.millis(duration), mTitle)
            ftOut.fromValue = 1.0
            ftOut.toValue = 0.0

            ftOut.setOnFinished { e -> mTitle!!.text = title }

            val ftIn = FadeTransition(Duration.millis(duration), mTitle)
            ftIn.fromValue = 0.0
            ftIn.toValue = 1.0
            ftIn.setOnFinished { e -> mTitleTransition = null }

            // Create chained sequential transition
            val evt: EventHandler<ActionEvent> = EventHandler {
                mTitleTransition = SequentialTransition(ftOut, ftIn)
                mTitleTransition!!.play()
            }

            if (mTitleTransition != null)
                mTitleTransition!!.setOnFinished(evt)
            else
                evt.handle(null)
        } else {
            mTitle!!.text = title
        }
    }

    /**
     * Request progress indication
     * Each call to request requires release to be called for the indicator to disappear as soon as all consumers released it
     */
    fun requestProgressIndicator() {
        mProgressIndicatorActivationCount += 1
        mProgressIndicator!!.isVisible = true
    }

    /**
     * Release progress indication
     */
    fun releaseProgressIndicator() {
        mProgressIndicatorActivationCount -= 1
        if (mProgressIndicatorActivationCount <= 0) {
            mProgressIndicator!!.isVisible = false
            mProgressIndicatorActivationCount = 0
        }
    }

    override fun OnSidebarItemSelected(itemType: SidebarController.ItemType) {
        val m: ModuleController
        when (itemType) {
            SidebarController.ItemType.Home -> m = this.homeModule
            SidebarController.ItemType.Depots -> m = this.depotMaintenanceModule
            SidebarController.ItemType.Debug -> m = this.debugPane
            else -> throw RuntimeException(String.format("Unknown sidebar item [%s]", itemType))
        }

        this.showModule(m)
    }

    override fun close() {
        super.close()

        if (mHomeController != null)
            mHomeController!!.close()
        if (mDepotMaintenanceController != null)
            mDepotMaintenanceController!!.close()
        if (mDebugController != null)
            mDebugController!!.close()
    }

    override fun onLeoBridgeMessageReceived(message: Message) {
        Platform.runLater {
            if (message.get("view") == "depot") {
                val id = message.get("id") as Int
                this.showModule(this.depotMaintenanceModule)
                this.depotMaintenanceModule.selectDepot(id)
            } else {
                Main.instance().showMessage(String.format("Received message [%s]", message))
            }
            Main.instance().toForeground()
        }
    }
}
