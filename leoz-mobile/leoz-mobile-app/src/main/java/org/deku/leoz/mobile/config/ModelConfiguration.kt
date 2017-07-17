package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.*
import org.deku.leoz.mobile.model.process.Delivery
import org.deku.leoz.mobile.model.process.DeliveryList
import org.deku.leoz.mobile.model.process.Login

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

            bind<Delivery>() with singleton {
                Delivery()
            }

            bind<DeliveryList>() with singleton {
                DeliveryList()
            }
        }
    }
}