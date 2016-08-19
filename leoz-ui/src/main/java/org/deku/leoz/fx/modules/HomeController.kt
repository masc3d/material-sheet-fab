package org.deku.leoz.fx.modules

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import org.deku.leoz.Localization
import org.deku.leoz.Main
import org.deku.leoz.fx.ModuleController

/**
 * Created by masc on 27.09.14.
 */
class HomeController : ModuleController() {
    private val localization: Localization by Kodein.global.lazy.instance()

    override val title: String
        get() = this.localization.resourceBundle.getString("menu.home")
}
