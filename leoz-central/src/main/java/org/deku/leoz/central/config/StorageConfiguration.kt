package org.deku.leoz.central.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.eagerSingleton
import com.github.salomonbrys.kodein.instance
import org.deku.leoz.central.Application

/**
 * Leoz-central storage configuration (deriving from leoz-node's storage configuration)
 * Created by masc on 10-Nov-15.
 */
class StorageConfiguration private constructor(appName: String)
:
        org.deku.leoz.node.config.StorageConfiguration(appName)
{
    companion object {
        val module = Kodein.Module {
            bind<StorageConfiguration>() with eagerSingleton {
                val application: Application = instance()
                StorageConfiguration(application.name)
            }

            bind<org.deku.leoz.node.config.StorageConfiguration>() with eagerSingleton {
                val storageConfiguration: StorageConfiguration = instance()
                storageConfiguration
            }
        }
    }
}