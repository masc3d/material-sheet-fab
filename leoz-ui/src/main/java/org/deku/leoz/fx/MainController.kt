package org.deku.leoz.fx

import javafx.animation.*
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
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
import java.util.*

/**
 * Main userinterface controller
 * Created by masc on 21.09.14.
 */
class MainController : Controller(), Initializable, SidebarController.Listener, LeoBridge.Listener {
    @FXML
    private lateinit var fxTitle: Label
    @FXML
    private lateinit var fxContentPaneContainer: AnchorPane
    @FXML
    private lateinit var fxSidebar: Pane
    @FXML
    private lateinit var fxSidebarController: SidebarController
    @FXML
    private lateinit var fxProgressIndicator: ProgressIndicator

    private var homeController: HomeController? = null
    private var depotMaintenanceController: DepotMaintenanceController? = null
    private var debugController: DebugController? = null

    /**
     * Currently active module
     */
    private var currentModuleController: ModuleController? = null
    /**
     * Progress indicator request count
     */
    private var progressIndicatorActivationCount: Int = 0

    override fun initialize(location: URL, resources: ResourceBundle) {
        fxSidebarController.setListener(this)
        LeoBridge.instance().listenerEventDelegate.add(this)

        this.showModule(this.homeModule, false)
    }

    /**
     * Tracks current content pane transition
     */
    internal var contentPaneTransition: Transition? = null

    val depotMaintenanceModule: DepotMaintenanceController
        get() {
            if (depotMaintenanceController == null) {
                depotMaintenanceController = ModuleController.fromFxml<DepotMaintenanceController>("/fx/modules/DepotMaintenance.fxml")
            }
            return depotMaintenanceController!!
        }

    val homeModule: HomeController
        get() {
            if (homeController == null) {
                homeController = ModuleController.fromFxml<HomeController>("/fx/modules/Home.fxml")
            }
            return homeController!!
        }

    val debugPane: DebugController
        get() {
            if (debugController == null) {
                debugController = ModuleController.fromFxml<DebugController>("/fx/modules/Debug.fxml")
            }
            return debugController!!
        }

    /**
     * Sets and shows a content pane

     * @param moduleController
     * *
     * @param animated
     */
    private fun showModule(moduleController: ModuleController, animated: Boolean) {
        val duration = 500.0

        if (currentModuleController === moduleController)
            return

        val oldModule = currentModuleController
        currentModuleController = moduleController

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
                    if (oldNode !== currentModuleController!!.rootNode)
                        fxContentPaneContainer.children.remove(oldNode)
                }
                animations.add(ftOut)

                node!!.opacity = 0.0
            }
        } else {
            fxContentPaneContainer.children.clear()
        }

        // Add new pane to container
        if (!fxContentPaneContainer.children.contains(node))
            fxContentPaneContainer.children.add(node)
        AnchorPane.setTopAnchor(node, 0.0)
        AnchorPane.setBottomAnchor(node, 0.0)
        AnchorPane.setRightAnchor(node, 0.0)
        AnchorPane.setLeftAnchor(node, 0.0)

        if (animated) {
            // Fade in new pane
            val ftIn = FadeTransition(Duration.millis(duration), node)
            ftIn.fromValue = 0.0
            ftIn.toValue = 1.0
            ftIn.setOnFinished { e -> contentPaneTransition = null }
            animations.add(ftIn)

            val evt: EventHandler<ActionEvent> = EventHandler {
                contentPaneTransition = ParallelTransition(*animations.toTypedArray())
                contentPaneTransition!!.play()
            }

            if (contentPaneTransition != null)
                contentPaneTransition!!.onFinished = evt
            else
                evt.handle(null)
        } else {
            node!!.opacity = 1.0
        }

        moduleController.activate()
        fxSidebarController.highlightByController(moduleController)
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

            val ftOut = FadeTransition(Duration.millis(duration), fxTitle)
            ftOut.fromValue = 1.0
            ftOut.toValue = 0.0

            ftOut.setOnFinished { e -> fxTitle.text = title }

            val ftIn = FadeTransition(Duration.millis(duration), fxTitle)
            ftIn.fromValue = 0.0
            ftIn.toValue = 1.0
            ftIn.setOnFinished { e -> mTitleTransition = null }

            // Create chained sequential transition
            val evt: EventHandler<ActionEvent> = EventHandler {
                mTitleTransition = SequentialTransition(ftOut, ftIn)
                mTitleTransition!!.play()
            }

            if (mTitleTransition != null)
                mTitleTransition!!.onFinished = evt
            else
                evt.handle(null)
        } else {
            fxTitle.text = title
        }
    }

    /**
     * Request progress indication
     * Each call to request requires release to be called for the indicator to disappear as soon as all consumers released it
     */
    fun requestProgressIndicator() {
        progressIndicatorActivationCount += 1
        fxProgressIndicator.isVisible = true
    }

    /**
     * Release progress indication
     */
    fun releaseProgressIndicator() {
        progressIndicatorActivationCount -= 1
        if (progressIndicatorActivationCount <= 0) {
            fxProgressIndicator.isVisible = false
            progressIndicatorActivationCount = 0
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

        if (homeController != null)
            homeController!!.close()
        if (depotMaintenanceController != null)
            depotMaintenanceController!!.close()
        if (debugController != null)
            debugController!!.close()
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
