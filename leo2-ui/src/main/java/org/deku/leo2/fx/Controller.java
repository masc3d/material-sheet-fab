package org.deku.leo2.fx;

import javafx.application.Platform;
import org.sx.Disposable;

/**
 * Base class for controllers, supporting activation and disposal
 *
 * Created by masc on 23.09.14.
 */
public abstract class Controller implements Activatable, Disposable {
    public final void activate() {
        Platform.runLater(() -> this.onActivation());
    }

    /**
     * Activation handler. Optionally overridden by derived classes
     */
    protected void onActivation() {
    }

    /**
     * Disposal. Optionally overridden by derived classes
     */
    public void dispose() {
    }
}
