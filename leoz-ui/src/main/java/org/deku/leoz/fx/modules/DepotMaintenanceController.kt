package org.deku.leoz.fx.modules

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Node
import org.deku.leoz.Main
import org.deku.leoz.fx.ModuleController
import org.deku.leoz.fx.components.DepotDetailsController
import org.deku.leoz.fx.components.DepotListController
import org.deku.leoz.rest.entities.internal.v1.Station

import java.net.URL
import java.util.ResourceBundle

/**
 * Created by masc on 22.09.14.
 */
class DepotMaintenanceController : ModuleController(), Initializable, DepotListController.Listener {
    @FXML
    private val mDepotList: Node? = null
    @FXML
    private val mDepotListController: DepotListController? = null
    @FXML
    private val mDepotDetails: Node? = null
    @FXML
    private val mDepotDetailsController: DepotDetailsController? = null

    override val title: String
        get() = Main.instance().getLocalizedString("menu.depots")

    override fun initialize(location: URL, resources: ResourceBundle) {
        mDepotListController!!.listener = this
    }

    public override fun onActivation() {
        mDepotListController!!.activate()
    }

    override fun onDepotListItemSelected(station: Station) {
        mDepotDetailsController!!.station = station
    }

    fun selectDepot(id: Int?) {
        mDepotListController!!.requestDepotSelection(id)
    }

    override fun close() {
        super.close()
        mDepotListController!!.close()
    }
}
