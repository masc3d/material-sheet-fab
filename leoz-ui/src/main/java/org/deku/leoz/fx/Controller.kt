package org.deku.leoz.fx

import javafx.application.Platform
import sx.Disposable

/**
 * Base class for controllers, supporting activation and disposal

 * Created by masc on 23.09.14.
 */
abstract class Controller : Activatable, Disposable {
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
