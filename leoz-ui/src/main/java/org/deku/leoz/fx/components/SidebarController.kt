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
import java.util.ArrayList
import java.util.EventListener
import java.util.ResourceBundle

/**
 * Created by masc on 22.09.14.
 */
class SidebarController : Initializable {
    @FXML
    internal var mMenuAccordion: Accordion? = null
    @FXML
    internal var mMenuPane: TitledPane? = null
    @FXML
    internal var mHomeButton: Button? = null
    @FXML
    internal var mDepotsButton: Button? = null
    @FXML
    internal var mDebugButton: Button? = null

    internal var mButtons: MutableList<Button> = ArrayList()

    enum class ItemType {
        Home,
        Depots,
        Debug
    }

    interface Listener : EventListener {
        fun OnSidebarItemSelected(itemType: ItemType)
    }

    private var mListener: Listener? = null

    fun setListener(listener: Listener) {
        mListener = listener
    }

    fun onHomeButton() {
        if (mListener != null)
            mListener!!.OnSidebarItemSelected(ItemType.Home)
    }

    fun onDepotButton() {
        if (mListener != null)
            mListener!!.OnSidebarItemSelected(ItemType.Depots)
    }

    fun onDebugButton() {
        if (mListener != null)
            mListener!!.OnSidebarItemSelected(ItemType.Debug)
    }

    fun highlightByController(module: ModuleController) {
        for (b in mButtons)
            b.styleClass.remove("leoz-sidebar-selection")

        var selection: Button? = null
        if (module is HomeController) {
            selection = mHomeButton
        } else if (module is DepotMaintenanceController) {
            selection = mDepotsButton
        } else if (module is DebugController) {
            selection = mDebugButton
        }

        if (selection != null)
            selection.styleClass.add("leoz-sidebar-selection")
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        mMenuAccordion!!.expandedPane = mMenuPane

        mButtons.add(mHomeButton)
        mButtons.add(mDepotsButton)
        mButtons.add(mDebugButton)
    }
}
