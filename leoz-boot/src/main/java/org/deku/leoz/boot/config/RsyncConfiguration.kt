package org.deku.leoz.boot.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.eagerSingleton

/**
 * Created by masc on 08/11/2016.
 */
class RsyncConfiguration : org.deku.leoz.config.RsyncConfiguration() {
    companion object {
        val module = Kodein.Module {
            bind<RsyncConfiguration>() with eagerSingleton { RsyncConfiguration() }
        }
    }
}