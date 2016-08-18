package org.deku.leoz.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.singleton
import org.deku.leoz.Settings

/**
 * Created by masc on 18/08/16.
 */
object Configurations {
    val application = Kodein.Module {
        bind<Settings>() with singleton { Settings() }
    }

    val messenging = Kodein.Module {
    }
}
