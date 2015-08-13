package org.deku.leoz.fx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import org.deku.leoz.Main;

/**
 * Base class for leoz user interface modules
 *
 * Created by masc on 23.09.14.
 */
public abstract class ModuleController extends Controller {
    protected Node mRootNode;

    /**
     * Create module controller from fxml
     * @param path
     * @param <T>
     * @return
     */
    public static <T extends ModuleController> T fromFxml(String path) {
        FXMLLoader fxml = Main.instance().loadFxPane(path);
        T m = (T)fxml.getController();
        m.mRootNode = fxml.getRoot();
        return m;
    }

    /**
     * Root fx node
     * @return
     */
    public Node getRootNode() {
        return mRootNode;
    }

    /**
     * Module title
     * @return
     */
    public abstract String getTitle();
}
