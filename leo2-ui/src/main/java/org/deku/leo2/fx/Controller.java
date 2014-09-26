package org.deku.leo2.fx;

import javafx.application.Platform;
import org.sx.Disposable;

/**
 * Created by masc on 23.09.14.
 */
public abstract class Controller implements Activatable, Disposable {
    public final void activate() {
        Platform.runLater(() -> this.onActivation());
    }

    protected void onActivation() {
    }

    public void dispose() {
    }
}
