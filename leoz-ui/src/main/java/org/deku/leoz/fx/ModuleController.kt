package org.deku.leoz.fx

import javafx.scene.Node
import org.deku.leoz.Main

/**
 * Base class for leoz user interface modules

 * Created by masc on 23.09.14.
 */
abstract class ModuleController : Controller() {
    /**
     * Root fx node
     * @return
     */
    var rootNode: Node? = null
        protected set

    /**
     * Module title
     * @return
     */
    abstract val title: String

    companion object {

        /**
         * Create module controller from fxml
         * @param path
         * *
         * @param
         * *
         * @return
         */
        fun <T : ModuleController> fromFxml(path: String): T {
            val fxml = Main.instance().loadFxPane(path)
            val m = fxml.getController<T>()
            m.rootNode = fxml.getRoot<Node>()
            return m
        }
    }
}
