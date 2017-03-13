package org.deku.leoz.ui.fx.modules

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Node
import javafx.scene.image.Image
import org.deku.leoz.ui.event.Event
import org.deku.leoz.ui.fx.ModuleController
import org.deku.leoz.ui.fx.components.DepotDetailsController
import org.deku.leoz.ui.fx.components.DepotListController
import io.reactivex.subjects.PublishSubject
import java.net.URL
import java.util.*

/**
 * Created by masc on 22.09.14.
 */
class DepotMaintenanceController : ModuleController(), Initializable {
    @FXML
    private lateinit var fxDepotList: Node
    @FXML
    private lateinit var fxDepotListController: DepotListController
    @FXML
    private lateinit var fxDepotDetails: Node
    @FXML
    private lateinit var fxDepotDetailsController: DepotDetailsController

    private val i18n: org.deku.leoz.ui.Localization by Kodein.global.lazy.instance()

    override val title: String
        get() = this.i18n.resources.getString("menu.depots")

    override val titleImage: Image by lazy { Image(this.javaClass.getResourceAsStream("/images/depot-144px.png")) }

    override val ovBusy: PublishSubject<Event<Boolean>> by lazy {
        fxDepotListController.ovBusy
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        fxDepotListController.ovItemSelected.subscribe {
            fxDepotDetailsController.station = it
        }
    }

    public override fun onActivation() {
        fxDepotListController.activate()
    }

    fun selectDepot(id: Int?) {
        fxDepotListController.requestDepotSelection(id)
    }

    override fun close() {
        super.close()
        fxDepotListController.close()
    }
}
