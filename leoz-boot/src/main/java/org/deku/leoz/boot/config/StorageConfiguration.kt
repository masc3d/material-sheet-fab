package org.deku.leoz.boot.config

import com.github.salomonbrys.kodein.*
import org.deku.leoz.Storage
import org.deku.leoz.bundle.BundleType

/**
 * Created by masc on 21.09.15.
 */
class StorageConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<Storage>() with eagerSingleton { Storage(appName = BundleType.LEOZ_BOOT.value) }
        }
    }
}