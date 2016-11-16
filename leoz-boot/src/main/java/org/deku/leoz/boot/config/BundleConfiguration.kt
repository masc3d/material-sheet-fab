package org.deku.leoz.boot.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import org.deku.leoz.bundle.BundleInstaller

/**
 * Created by masc on 16/11/2016.
 */
class BundleConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<BundleInstaller>() with singleton {
                val storage: StorageConfiguration = instance()
                BundleInstaller(storage.bundleInstallationDirectory)
            }
        }
    }
}