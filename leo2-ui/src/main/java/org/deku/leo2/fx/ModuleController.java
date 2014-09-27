package org.deku.leo2.fx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import org.deku.leo2.Main;

/**
 * User interface module
 *
 * Created by masc on 23.09.14.
 */
public abstract class ModuleController extends Controller {
    protected Node mNode;

    public static <T extends ModuleController> T fromFxml(String path) {
        FXMLLoader fxml = Main.instance().loadFxPane(path);
        T m = (T)fxml.getController();
        m.mNode = fxml.getRoot();
        return m;
    }

    public Node getNode() {
        return mNode;
    }

    public abstract String getTitle();
}
