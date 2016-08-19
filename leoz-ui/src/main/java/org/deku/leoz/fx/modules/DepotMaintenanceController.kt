package org.deku.leoz.fx.modules

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Node
import org.deku.leoz.Localization
import org.deku.leoz.Main
import org.deku.leoz.fx.ModuleController
import org.deku.leoz.fx.components.DepotDetailsController
import org.deku.leoz.fx.components.DepotListController
import org.deku.leoz.rest.entities.internal.v1.Station
import java.net.URL
import java.util.*

/**
 * Created by masc on 22.09.14.
 */
class DepotMaintenanceController : ModuleController(), Initializable, DepotListController.Listener {
    @FXML
    private lateinit var fxDepotList: Node
    @FXML
    private lateinit var fxDepotListController: DepotListController
    @FXML
    private lateinit var fxDepotDetails: Node
    @FXML
    private lateinit var fxDepotDetailsController: DepotDetailsController

    private val i18n: Localization by Kodein.global.lazy.instance()

    override val title: String
        get() = this.i18n.resources.getString("menu.depots")

    override fun initialize(location: URL, resources: ResourceBundle) {
        fxDepotListController.listener = this
    }

    public override fun onActivation() {
        fxDepotListController.activate()
    }

    override fun onDepotListItemSelected(station: Station?) {
        fxDepotDetailsController.station = station
    }

    fun selectDepot(id: Int?) {
        fxDepotListController.requestDepotSelection(id)
    }

    override fun close() {
        super.close()
        fxDepotListController.close()
    }
}
