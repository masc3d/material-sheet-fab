package org.deku.leo2.fx.modules;

import org.deku.leo2.Main;
import org.deku.leo2.fx.ModuleController;

/**
 * Created by masc on 27.09.14.
 */
public class HomeController extends ModuleController {
    @Override
    public String getTitle() {
        return Main.instance().getLocalizedString("menu.home");
    }
}
