package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.bind
import com.github.salomonbrys.kodein.erased.singleton
import org.deku.leoz.mobile.model.process.*

/**
 * Model configuration
 * Created by masc on 10/02/2017.
 */
class ModelConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<Login>() with singleton {
                Login()
            }

            bind<Tour>() with singleton {
                Tour()
            }
            
            bind<VehicleLoading>() with singleton {
                VehicleLoading()
            }

            bind<VehicleUnloading>() with singleton {
                VehicleUnloading()
            }
        }
    }
}