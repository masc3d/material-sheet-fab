package org.deku.leoz.ui.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.eagerSingleton

/**
 * Created by masc on 05/12/2016.
 */
class RsyncConfiguration : org.deku.leoz.config.RsyncConfiguration() {
    companion object {
        val module = Kodein.Module {
            bind<RsyncConfiguration>() with eagerSingleton { RsyncConfiguration() }
        }
    }
}