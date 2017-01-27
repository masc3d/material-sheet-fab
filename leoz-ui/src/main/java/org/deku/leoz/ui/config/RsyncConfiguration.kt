package org.deku.leoz.ui.config

import com.github.salomonbrys.kodein.*

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