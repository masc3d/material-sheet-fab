package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.singleton
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import org.deku.leoz.mobile.Settings

/**
 * Created by n3 on 15/02/2017.
 */
class SettingsConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<Settings>() with singleton {
                Settings(context = instance())
            }
        }
    }
}