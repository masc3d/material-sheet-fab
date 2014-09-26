package org.deku.leo2.fx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.deku.leo2.Main;
import org.sx.Disposable;

/**
 * User interface module
 *
 * Created by masc on 23.09.14.
 */
public class Module <T> implements Disposable {
    T mController;
    Node mNode;

    public Module(Pane pane, T controller) {
        mNode = pane;
        mController = controller;
    }

    public static <T> Module fromFxml(String path, Class<T> controllerClass) {
        FXMLLoader fxml = Main.instance().loadFxPane(path);

        return new Module<T>(fxml.getRoot(), fxml.getController());
    }

    public T getController() {
        return mController;
    }

    public Node getNode() {
        return mNode;
    }

    @Override
    public void dispose() {
        if (mController instanceof Disposable) {
            ((Disposable)mController).dispose();
        }
    }
}
