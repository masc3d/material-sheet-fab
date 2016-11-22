package org.deku.leoz.ui.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.eagerSingleton
import org.deku.leoz.bundle.BundleType

/**
 * Storage configuration
 * Created by masc on 26/09/2016.
 */
class StorageConfiguration : org.deku.leoz.config.StorageConfiguration(appName = BundleType.LEOZ_UI.value) {
    companion object {
        val module = Kodein.Module {
            bind<StorageConfiguration>() with eagerSingleton { StorageConfiguration() }
        }
    }
}