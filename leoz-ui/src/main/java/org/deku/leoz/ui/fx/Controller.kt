package org.deku.leoz.ui.fx

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import org.deku.leoz.ui.Localization
import org.slf4j.LoggerFactory
import sx.Disposable
import sx.Stopwatch

/**
 * Base class for controllers, supporting activation and disposals
 * Created by masc on 23.09.14.
 */
abstract class Controller : Activatable, Disposable {
    companion object {
        /** Logger */
        private val log = LoggerFactory.getLogger(Controller::class.java)
        /** Localization */
        val i18n: Localization by Kodein.global.lazy.instance()

        /**
         * Create module controller from fxml
         * @param path
         * @return
         */
        fun <T : Controller> fromFxml(path: String): T {
            val sw = Stopwatch.createStarted()

            val fxml = FXMLLoader(Controller::class.java.getResource(path))
            fxml.resources = this.i18n.resources
            fxml.load<Parent>()

            val m = fxml.getController<T>()
            m.fxRoot = fxml.getRoot()

            log.debug("Created fx controller from [${path}] in ${sw}")
            return m
        }
    }

    /**
     * Root fx node.
     * For controllers which are deserialized implicitly (eg. nested) the fx:id must be `fxRoot` by convention.
     * When loaded via `Controller.fromFxml` it's set in the process without considering fx:id
     * @return
     */
    @FXML
    lateinit var fxRoot: Parent

    override fun activate() {
        // Run later, as requesting focus within initial activation (directly after loading from fxml) won't work (bug in javafx8)
        Platform.runLater { this.onActivation() }
    }

    /**
     * Activation handler. Optionally overridden by derived classes
     */
    protected open fun onActivation() {
    }

    /**
     * Disposal. Optionally overridden by derived classes
     */
    override fun close() {
    }
}
