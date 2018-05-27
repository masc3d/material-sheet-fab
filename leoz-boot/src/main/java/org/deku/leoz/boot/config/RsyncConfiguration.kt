package org.deku.leoz.boot.config

import com.github.salomonbrys.kodein.*

/**
 * Created by masc on 08/11/2016.
 */
class RsyncConfiguration : org.deku.leoz.config.RsyncConfiguration() {
    companion object {
        val module = Kodein.Module {
            bind<RsyncConfiguration>() with singleton {
                RsyncConfiguration()
            }
        }
    }
}