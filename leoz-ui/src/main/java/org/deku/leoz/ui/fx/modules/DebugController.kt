package org.deku.leoz.ui.fx.modules

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import org.deku.leoz.ui.Main
import org.deku.leoz.ui.Settings
import org.deku.leoz.ui.bridge.LeoBridge
import org.deku.leoz.ui.bridge.Message
import org.deku.leoz.ui.fx.ModuleController
import org.slf4j.LoggerFactory
import rx.subjects.PublishSubject
import rx.lang.kotlin.PublishSubject
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

    val ovDepotSelect by lazy { PublishSubject<Unit>() }

    private val settings: Settings by Kodein.global.lazy.instance()

    override val title: String
        get() = "Debug"

    override fun initialize(location: URL, resources: ResourceBundle) {
        fxUiAnimationsEnabled.isSelected = this.settings.isAnimationsEnabled
        fxUiAnimationsEnabled.selectedProperty().addListener { o, ov, nv -> this.settings.isAnimationsEnabled = nv }
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
        this.ovDepotSelect.onNext(Unit)
    }
}
