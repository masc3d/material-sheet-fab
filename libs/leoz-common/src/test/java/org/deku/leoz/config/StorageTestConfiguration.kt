package org.deku.leoz.config

import org.deku.leoz.config.StorageConfiguration
import org.deku.leoz.bundle.Bundles
import java.io.File

/**
 * Created by masc on 12.10.15.
 */
object StorageTestConfiguration : StorageConfiguration(appName = "test") {
    val bundlesTestDirectory by lazy({
        val d = File(this.bundlesDirectory, "test")
        d.mkdirs()
        d
    })
}