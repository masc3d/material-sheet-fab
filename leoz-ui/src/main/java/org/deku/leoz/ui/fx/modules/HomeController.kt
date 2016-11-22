package org.deku.leoz.ui.fx.modules

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import javafx.scene.image.Image
import org.deku.leoz.ui.fx.ModuleController

/**
 * Created by masc on 27.09.14.
 */
class HomeController : ModuleController() {
    private val i18n: org.deku.leoz.ui.Localization by Kodein.global.lazy.instance()

    override val title: String
        get() = this.i18n.resources.getString("menu.home")

    override val titleImage: Image by lazy { Image(this.javaClass.getResourceAsStream("/images/home-144px.png")) }
}
