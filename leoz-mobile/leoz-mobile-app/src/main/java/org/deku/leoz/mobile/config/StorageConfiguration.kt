package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.*
import org.deku.leoz.mobile.model.Storage

/**
 * Storage configuration
 * Created by masc on 12/12/2016.
 */
class StorageConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<Storage>() with singleton {
                Storage(context = instance())
            }
        }
    }
}