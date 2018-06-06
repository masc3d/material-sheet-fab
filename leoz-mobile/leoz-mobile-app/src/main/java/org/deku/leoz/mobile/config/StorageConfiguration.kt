package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.bind
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.erased.singleton
import org.deku.leoz.mobile.Storage

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