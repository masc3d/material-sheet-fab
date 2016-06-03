package org.deku.leoz.fx.modules

import org.deku.leoz.Main
import org.deku.leoz.fx.ModuleController

/**
 * Created by masc on 27.09.14.
 */
class HomeController : ModuleController() {
    override val title: String
        get() = Main.instance().getLocalizedString("menu.home")
}
