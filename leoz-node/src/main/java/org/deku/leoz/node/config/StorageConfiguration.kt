package org.deku.leoz.node.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.eagerSingleton
import com.github.salomonbrys.kodein.instance
import org.deku.leoz.node.Application
import org.deku.leoz.node.Storage

/**
 * Created by masc on 13/12/2016.
 */
class StorageConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<Storage>() with eagerSingleton {
                val application: Application = instance()
                Storage(application.name)
            }
        }
    }
}