package org.deku.leoz.boot.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.eagerSingleton
import com.github.salomonbrys.kodein.singleton
import org.apache.commons.lang3.SystemUtils
import org.deku.leoz.bundle.BundleType
import java.io.File
import java.nio.file.Paths

/**
 * Created by masc on 21.09.15.
 */
class StorageConfiguration : org.deku.leoz.config.StorageConfiguration(appName = BundleType.LEOZ_BOOT.value) {
    companion object {
        val module = Kodein.Module {
            bind<StorageConfiguration>() with eagerSingleton { StorageConfiguration() }
        }
    }
}