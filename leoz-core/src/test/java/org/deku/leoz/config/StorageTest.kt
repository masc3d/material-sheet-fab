package org.deku.leoz.config

import org.deku.leoz.Storage
import java.io.File

/**
 * Created by masc on 12.10.15.
 */
object StorageTest : Storage(appName = "test") {
    val bundlesTestDirectory by lazy({
        val d = File(this.bundlesDirectory, "test")
        d.mkdirs()
        d
    })
}