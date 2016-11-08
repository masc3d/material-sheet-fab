package org.deku.leoz.boot.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.singleton
import org.deku.leoz.boot.Settings

/**
 * Application configuration
 * Created by masc on 08/11/2016.
 */
object ApplicationConfiguration {
    val module = Kodein.Module {
        /** Application wide settings */
        bind<Settings>() with singleton { Settings() }
    }
}