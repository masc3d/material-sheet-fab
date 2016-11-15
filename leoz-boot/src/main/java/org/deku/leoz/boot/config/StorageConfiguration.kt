package org.deku.leoz.boot.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.eagerSingleton
import com.github.salomonbrys.kodein.singleton
import org.deku.leoz.bundle.BundleType
import java.io.File
import java.nio.file.Paths

/**
 * Created by masc on 21.09.15.
 */
class StorageConfiguration : org.deku.leoz.config.StorageConfiguration(appName = BundleType.LEOZ_BOOT.value) {
    /** Base path of native bundle.
     * @return Path of bundle or null if path could not be detected
     */
    val nativeBundleBasePath: File? by lazy {
        val codeSourcePath = Paths.get(this.javaClass.protectionDomain.codeSource.location.toURI())

        if (codeSourcePath.toString().endsWith(".jar"))
            codeSourcePath.parent.parent.toFile()
        else
            null
    }

    companion object {
        val module = Kodein.Module {
            bind<StorageConfiguration>() with eagerSingleton { StorageConfiguration() }
        }
    }
}