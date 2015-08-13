package org.deku.leoz.fx.modules;

import org.deku.leoz.Main;
import org.deku.leoz.fx.ModuleController;

/**
 * Created by masc on 27.09.14.
 */
public class HomeController extends ModuleController {
    @Override
    public String getTitle() {
        return Main.instance().getLocalizedString("menu.home");
    }
}
