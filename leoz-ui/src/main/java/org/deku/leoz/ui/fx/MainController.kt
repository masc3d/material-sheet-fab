package org.deku.leoz.ui.fx

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.github.thomasnield.rxkotlinfx.observeOnFx
import javafx.animation.*
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.SplitPane
import javafx.scene.control.ToggleButton
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.util.Duration
import org.controlsfx.control.Notifications
import org.deku.leoz.ui.Settings
import org.deku.leoz.ui.bridge.LeoBridge
import org.deku.leoz.ui.bridge.Message
import org.deku.leoz.ui.config.LogConfiguration
import org.deku.leoz.ui.fx.components.SidebarController
import org.deku.leoz.ui.fx.modules.DebugController
import org.deku.leoz.ui.fx.modules.DepotMaintenanceController
import org.deku.leoz.ui.fx.modules.HomeController
import org.slf4j.LoggerFactory
import sx.JarManifest
import sx.LazyInstance
import sx.fx.controls.MaterialProgressIndicator
import java.net.URL
import java.util.*
import java.util.concurrent.ExecutorService

/**
 * Main userinterface controller
 * Created by masc on 21.09.14.
 */
class MainController : Controller(), Initializable {
    @FXML
    private lateinit var fxTitleImageView: ImageView
    @FXML
    private lateinit var fxTitle: Label
    @FXML
    private lateinit var fxVersion: Label
    @FXML
    private lateinit var fxUser: Label
    @FXML
    private lateinit var fxContentPaneContainer: AnchorPane
    @FXML
    private lateinit var fxSidebar: Pane
    @FXML
    private lateinit var fxSidebarController: SidebarController
    @FXML
    private lateinit var fxProgressIndicator: MaterialProgressIndicator
    @FXML
    private lateinit var fxLogButton: ToggleButton
    @FXML
    private lateinit var fxSplitPane: SplitPane
    @FXML
    private lateinit var fxBottomPaneContainer: AnchorPane

    private val log = LoggerFactory.getLogger(this.javaClass)

    /** Global settings */
    private val settings: Settings by Kodein.global.lazy.instance()

    /** Leo bridge */
    private val leoBridge: LeoBridge by Kodein.global.lazy.instance()

    /** Log configuration */
    private val logConfiguration: LogConfiguration by Kodein.global.lazy.instance()

    private val executorService: ExecutorService by Kodein.global.lazy.instance()

    /**
     * Helper for loading a controller in the context of MainController
     * subscribing to all relevant events
     */
    private fun <T : ModuleController> loadController(fxml: String): T {
        val mc = Controller.fromFxml<T>(fxml)
        mc.ovError
                .observeOnFx()
                .subscribe {
            this.showError(it.message ?: "")
        }
        mc.ovBusy
                .observeOnFx()
                .subscribe {
            if (it.value)
                this.requestProgressIndicator()
            else
                this.releaseProgressIndicator()
        }
        return mc
    }

    // Home controller configuration
    private val lazyHomeController = LazyInstance<HomeController>({
        this.loadController("/fx/modules/Home.fxml")
    })

    // Depot maintenance controller configuration
    private val lazyDepotMaintenanceController = LazyInstance<DepotMaintenanceController>({
        this.loadController("/fx/modules/DepotMaintenance.fxml")
    })

    // Debug controller configuration
    private val lazyDebugController = LazyInstance<DebugController>({
        val c = this.loadController<DebugController>("/fx/modules/Debug.fxml")
        c.ovDepotSelect.subscribe {
            this.showModule(this.depotMaintenanceController)
            this.depotMaintenanceController.selectDepot(800)
        }
        c
    })

    /**
     * Convenience properties
     */
    val homeController: HomeController get() {
        return lazyHomeController.get()
    }
    val depotMaintenanceController: DepotMaintenanceController get() {
        return lazyDepotMaintenanceController.get()
    }
    val debugController: DebugController get() {
        return lazyDebugController.get()
    }

    /**
     * Currently active module
     */
    private var currentModuleController: ModuleController? = null
    /**
     * Progress indicator request count
     */
    private var progressIndicatorActivationCount: Int = 0

    /**
     * Tracks current content pane transition
     */
    private var contentPaneTransition: Transition? = null

    override fun initialize(location: URL, resources: ResourceBundle) {
        // Hook up to LeoBridge
        this.leoBridge.ovMessageReceived
                .observeOnFx()
                .subscribe { this.onLeoBridgeMessageReceived(it) }

        // Sidebar
        this.fxSidebarController.ovItemSelected.subscribe {
            this.onSidebarItemSelected(it)
        }

        this.fxLogButton.onAction = EventHandler { _ -> this.onLogButtonAction() }

        // Asynchronously preload heavy modules
        this.executorService.submit {
            this.lazyDepotMaintenanceController.get()
        }

        // UI initialization
        Platform.runLater {
            this.fxProgressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS)

            this.fxSplitPane.items.remove(this.fxBottomPaneContainer)

            // Initial display
            val version = Kodein.global.instance<JarManifest>().implementationVersion
            this.fxVersion.text = if (version.length > 0) "v${version}" else "N/A"

            // TODO: login name
            this.fxUser.text = "Max Mustermann"

            this.showModule(this.homeController, false)
        }
    }

    /**
     * Sets and shows a content pane
     * @param moduleController Module/controller to show
     * @param animated Show with animation or not
     */
    private fun showModule(moduleController: ModuleController, animated: Boolean) {
        fxSidebarController.highlightByController(moduleController)

        Platform.runLater {
            val duration = 500.0

            if (currentModuleController === moduleController)
                return@runLater

            val oldModule = currentModuleController
            currentModuleController = moduleController

            val node = moduleController.fxRoot
            val oldNode = oldModule?.fxRoot

            this.setTitle(moduleController.title, animated)
            this.fxTitleImageView.image = moduleController.titleImage

            val animations = ArrayList<Animation>()

            if (animated) {
                if (oldNode != null) {
                    // Fade out old pane if there is one
                    val ftOut = FadeTransition(Duration.millis(duration), oldNode)
                    ftOut.fromValue = 1.0
                    ftOut.toValue = 0.0
                    ftOut.setOnFinished { _ ->
                        if (oldNode !== currentModuleController!!.fxRoot)
                            fxContentPaneContainer.children.remove(oldNode)
                    }
                    animations.add(ftOut)

                    node.opacity = 0.0
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
                ftIn.setOnFinished { _ -> contentPaneTransition = null }
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
                node.opacity = 1.0
            }

            moduleController.activate()
        }
    }

    fun showModule(moduleController: ModuleController) {
        this.showModule(moduleController, this.settings.isAnimationsEnabled)
    }

    /**
     * Tracks current title transition
     */
    internal var titleTransition: Transition? = null

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

            ftOut.setOnFinished { _ -> fxTitle.text = title }

            val ftIn = FadeTransition(Duration.millis(duration), fxTitle)
            ftIn.fromValue = 0.0
            ftIn.toValue = 1.0
            ftIn.setOnFinished { _ -> titleTransition = null }

            // Create chained sequential transition
            val evt: EventHandler<ActionEvent> = EventHandler {
                titleTransition = SequentialTransition(ftOut, ftIn)
                titleTransition!!.play()
            }

            if (titleTransition != null)
                titleTransition!!.onFinished = evt
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
    private fun requestProgressIndicator() {
        progressIndicatorActivationCount += 1
        fxProgressIndicator.isVisible = true
    }

    /**
     * Release progress indication
     */
    private fun releaseProgressIndicator() {
        progressIndicatorActivationCount -= 1
        if (progressIndicatorActivationCount <= 0) {
            fxProgressIndicator.isVisible = false
            progressIndicatorActivationCount = 0
        }
    }

    fun onSidebarItemSelected(itemType: SidebarController.ItemType) {
        val m: ModuleController
        when (itemType) {
            SidebarController.ItemType.Home -> m = this.homeController
            SidebarController.ItemType.Depots -> m = this.depotMaintenanceController
            SidebarController.ItemType.Debug -> m = this.debugController
        }

        this.showModule(m)
    }

    /**
     * Close controller(s)
     */
    override fun close() {
        super.close()

        arrayOf(this.lazyHomeController,
                this.lazyDepotMaintenanceController,
                this.lazyDebugController
        ).forEach {
            it.ifSet {
                it.close()
            }
        }
    }

    fun showMessage(message: String) {
        Notifications.create().title("Leoz").text(message).showInformation()
    }

    fun showError(message: String) {
        Notifications.create().title("Leoz").text(message).showError()
    }

    fun onLogButtonAction() {
        val show = this.fxLogButton.isSelected

        if (show)  {
            // Create text area
            val t = this.logConfiguration.textAreaLogAppender.textArea
            t.isEditable = false
            t.styleClass.add("leoz-log")
            AnchorPane.setTopAnchor(t, 0.0)
            AnchorPane.setBottomAnchor(t, 0.0)
            AnchorPane.setRightAnchor(t, 0.0)
            AnchorPane.setLeftAnchor(t, 0.0)

            this.fxBottomPaneContainer.children.clear()
            this.fxBottomPaneContainer.children.add(t)

            if (!this.fxSplitPane.items.contains(this.fxBottomPaneContainer)) {
                this.fxSplitPane.items.add(this.fxBottomPaneContainer)
            }

            this.fxSplitPane.setDividerPositions(0.6)
        } else {
            this.fxSplitPane.items.remove(this.fxBottomPaneContainer)
            this.fxBottomPaneContainer.children.clear()

            this.fxSplitPane.items.remove(this.fxBottomPaneContainer)
        }
    }

    fun onLeoBridgeMessageReceived(message: Message) {
        if (message.get("view") == "depot") {
            val id = message.get("id") as Int
            this.showModule(this.depotMaintenanceController)
            this.depotMaintenanceController.selectDepot(id)
        } else {
            this.showMessage(String.format("Received message [%s]", message))
        }
    }
}
