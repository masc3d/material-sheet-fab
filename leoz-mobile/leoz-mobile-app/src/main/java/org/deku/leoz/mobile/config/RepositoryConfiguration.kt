package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.erased.singleton
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.model.repository.OrderRepository
import org.deku.leoz.mobile.model.repository.ParcelRepository
import org.deku.leoz.mobile.model.repository.StopRepository
import org.slf4j.LoggerFactory

/**
 * Database repository configuration
 * Created by masc on 12/12/2016.
 */
class RepositoryConfiguration {
    private val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        val module = Kodein.Module {

            bind<OrderRepository>() with singleton {
                OrderRepository(instance<Database>().store)
            }

            bind<StopRepository>() with singleton {
                StopRepository(instance<Database>().store)
            }

            bind<ParcelRepository>() with singleton {
                ParcelRepository(instance<Database>().store)
            }
        }
    }
}