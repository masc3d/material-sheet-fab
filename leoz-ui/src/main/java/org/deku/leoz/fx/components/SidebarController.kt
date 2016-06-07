package org.deku.leoz.fx.components

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Accordion
import javafx.scene.control.Button
import javafx.scene.control.TitledPane
import org.deku.leoz.fx.ModuleController
import org.deku.leoz.fx.modules.DebugController
import org.deku.leoz.fx.modules.DepotMaintenanceController
import org.deku.leoz.fx.modules.HomeController
import java.net.URL
import java.util.*

/**
 * Created by masc on 22.09.14.
 */
class SidebarController : Initializable {
    @FXML
    private lateinit var fxMenuAccordion: Accordion
    @FXML
    private lateinit var fxMenuPane: TitledPane
    @FXML
    private lateinit var fxHomeButton: Button
    @FXML
    private lateinit var fxDepotsButton: Button
    @FXML
    private lateinit var fxDebugButton: Button

    private var buttons: MutableList<Button> = ArrayList()

    enum class ItemType {
        Home,
        Depots,
        Debug
    }

    interface Listener : EventListener {
        fun OnSidebarItemSelected(itemType: ItemType)
    }

    private var listener: Listener? = null

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun onHomeButton() {
        if (listener != null)
            listener!!.OnSidebarItemSelected(ItemType.Home)
    }

    fun onDepotButton() {
        if (listener != null)
            listener!!.OnSidebarItemSelected(ItemType.Depots)
    }

    fun onDebugButton() {
        if (listener != null)
            listener!!.OnSidebarItemSelected(ItemType.Debug)
    }

    fun highlightByController(module: ModuleController) {
        for (b in buttons)
            b.styleClass.remove("leoz-sidebar-selection")

        var selection: Button? = null
        if (module is HomeController) {
            selection = fxHomeButton
        } else if (module is DepotMaintenanceController) {
            selection = fxDepotsButton
        } else if (module is DebugController) {
            selection = fxDebugButton
        }

        if (selection != null)
            selection.styleClass.add("leoz-sidebar-selection")
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        fxMenuAccordion.expandedPane = fxMenuPane

        buttons.add(fxHomeButton)
        buttons.add(fxDepotsButton)
        buttons.add(fxDebugButton)
    }
}
