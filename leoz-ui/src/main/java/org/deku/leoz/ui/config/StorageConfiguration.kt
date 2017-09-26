package org.deku.leoz.ui.config

import com.github.salomonbrys.kodein.*
import org.deku.leoz.Storage
import org.deku.leoz.bundle.BundleType

/**
 * Storage configuration
 * Created by masc on 26/09/2016.
 */
class StorageConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<Storage>() with eagerSingleton { createStorage() }
        }

        fun createStorage(): Storage {
            return Storage(appName = BundleType.LEOZ_UI.value)
        }
    }
}