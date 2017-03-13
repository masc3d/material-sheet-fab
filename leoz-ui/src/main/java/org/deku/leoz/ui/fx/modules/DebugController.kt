package org.deku.leoz.ui.fx.modules

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import javafx.scene.image.Image
import org.deku.leoz.ui.Settings
import org.deku.leoz.ui.bridge.LeoBridge
import org.deku.leoz.ui.bridge.Message
import org.deku.leoz.ui.fx.ModuleController
import org.slf4j.LoggerFactory
import io.reactivex.subjects.PublishSubject
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

    val ovDepotSelect by lazy { PublishSubject.create<Unit>() }

    /** Global settings */
    private val settings: Settings by Kodein.global.lazy.instance()

    /** LeoBridge instance */
    private val leoBridge: LeoBridge by Kodein.global.lazy.instance()

    override val title: String
        get() = "Debug"

    override val titleImage: Image by lazy { Image(this.javaClass.getResourceAsStream("/images/debug-144px.png")) }

    override fun initialize(location: URL, resources: ResourceBundle) {
        fxUiAnimationsEnabled.isSelected = this.settings.isAnimationsEnabled
        fxUiAnimationsEnabled.selectedProperty().addListener { _, _, nv -> this.settings.isAnimationsEnabled = nv }
    }

    fun onLeoBridgeSend() {
        try {
            this.leoBridge.sendMessage(Message(fxLeoBridgeMessageTextField.text))
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
            this.leoBridge.sendMessage(msg)
        } catch (e: Exception) {
            log.error(e.message, e)
        }

    }

    fun onDepotSelect() {
        this.ovDepotSelect.onNext(Unit)
    }
}
