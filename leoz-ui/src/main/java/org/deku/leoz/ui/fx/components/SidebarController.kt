package org.deku.leoz.ui.fx.components

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Accordion
import javafx.scene.control.Button
import javafx.scene.control.TitledPane
import org.deku.leoz.ui.fx.ModuleController
import org.deku.leoz.ui.fx.modules.DebugController
import org.deku.leoz.ui.fx.modules.DepotMaintenanceController
import org.deku.leoz.ui.fx.modules.HomeController
import rx.lang.kotlin.PublishSubject
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

    private val buttons: MutableList<Button> = ArrayList()

    val ovItemSelected by lazy { PublishSubject<ItemType>() }

    enum class ItemType {
        Home,
        Depots,
        Debug
    }

    fun onHomeButton() {
        this.ovItemSelected.onNext(ItemType.Home)
    }

    fun onDepotButton() {
        this.ovItemSelected.onNext(ItemType.Depots)
    }

    fun onDebugButton() {
        this.ovItemSelected.onNext(ItemType.Debug)
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

        this.buttons.add(fxHomeButton)
        this.buttons.add(fxDepotsButton)
        this.buttons.add(fxDebugButton)
    }
}
