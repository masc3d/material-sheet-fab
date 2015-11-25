package org.deku.leoz.fx;

import javafx.application.Platform;
import sx.Disposable;

/**
 * Base class for controllers, supporting activation and disposal
 *
 * Created by masc on 23.09.14.
 */
public abstract class Controller implements Activatable, Disposable {
    public final void activate() {
        // Run later, as requesting focus within initial activation (directly after loading from fxml) won't work (bug in javafx8)
        Platform.runLater(() ->
                this.onActivation()
        );
    }

    /**
     * Activation handler. Optionally overridden by derived classes
     */
    protected void onActivation() {
    }

    /**
     * Disposal. Optionally overridden by derived classes
     */
    public void close() {
    }
}
