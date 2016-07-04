package org.deku.leoz.fx.modules

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import org.deku.leoz.Main
import org.deku.leoz.Settings
import org.deku.leoz.bridge.LeoBridge
import org.deku.leoz.bridge.Message
import org.deku.leoz.fx.ModuleController
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*

/**
 * Created by masc on 27.09.14.
 */
class DebugController : ModuleController(), Initializable {
    internal var log = LoggerFactory.getLogger(DebugController::class.java)

    @FXML
    private lateinit var fxLeoBridgeMessageTextField: TextField
    @FXML
    private lateinit var fxUiAnimationsEnabled: CheckBox

    override val title: String
        get() = "Debug"

    override fun initialize(location: URL, resources: ResourceBundle) {
        fxUiAnimationsEnabled.isSelected = Settings.instance().isAnimationsEnabled
        fxUiAnimationsEnabled.selectedProperty().addListener { o, ov, nv -> Settings.instance().isAnimationsEnabled = nv }
    }

    fun onLeoBridgeSend() {
        try {
            LeoBridge.instance().sendMessage(Message(fxLeoBridgeMessageTextField.text))
        } catch (e: Exception) {
            log.error(e.message, e)
        }

    }

    fun onLeoBridgeSendComplexMessage() {
        val msg = Message()
        msg.put("text", "hey")
        msg.put("int", 123)
        msg.put("float", 34.5)
        msg.put("date", Date())
        try {
            LeoBridge.instance().sendMessage(msg)
        } catch (e: Exception) {
            log.error(e.message, e)
        }

    }

    fun onDepotSelect() {
        Main.instance().mainController.showModule(Main.instance().mainController.depotMaintenanceController)
        Main.instance().mainController.depotMaintenanceController.selectDepot(800)
    }
}
