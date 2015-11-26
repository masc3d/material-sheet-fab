package org.deku.leoz.central.config

import org.deku.leoz.central.App
import java.io.File

/**
 * Leoz-central storage configuration (deriving from leoz-node's storage configuration)
 * Created by n3 on 10-Nov-15.
 */
class StorageConfiguration private constructor(appName: String)
:
        org.deku.leoz.node.config.StorageConfiguration(appName) {
    companion object {
        val instance by lazy({ StorageConfiguration(App.instance.name) })
    }

    val transferDataDirectory by lazy({
        val d = File(this.dataDirectory, "transfer")
        d.mkdirs()
        d
    })
}