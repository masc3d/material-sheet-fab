package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.*
import org.deku.leoz.mobile.update.UpdateService

/**
 * Update service configuration
 * Created by masc on 10/02/2017.
 */
class UpdateConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<UpdateService>() with singleton {
                val service = UpdateService(executorService = instance())
                service.start()
                service
            }
        }
    }
}