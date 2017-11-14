package org.deku.leoz.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.bind
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.erased.provider
import org.deku.leoz.RestClientFactory
import org.deku.leoz.service.internal.*

/**
 * Rest client configuration
 * Created by masc on 14.11.17.
 */
class RestClientConfiguration {
    companion object {
        val module = Kodein.Module {
            /**
             * Helper for creating service proxy
             */
            fun <T> createProxy(clientFactory: RestClientFactory, serviceType: Class<T>): T =
                    clientFactory.create().proxy(serviceType)

            bind<StationService>() with provider {
                createProxy(clientFactory = instance(), serviceType = StationService::class.java)
            }

            bind<BundleServiceV2>() with provider {
                createProxy(clientFactory = instance(), serviceType = BundleServiceV2::class.java)
            }

            bind<DeliveryListService>() with provider {
                createProxy(clientFactory = instance(), serviceType = DeliveryListService::class.java)
            }

            bind<OrderService>() with provider {
                createProxy(clientFactory = instance(), serviceType = OrderService::class.java)
            }

            bind<UserService>() with provider {
                createProxy(clientFactory = instance(), serviceType = UserService::class.java)
            }

            bind<AuthorizationService>() with provider {
                createProxy(clientFactory = instance(), serviceType = AuthorizationService::class.java)
            }

            bind<LocationServiceV2>() with provider {
                createProxy(clientFactory = instance(), serviceType = LocationServiceV2::class.java)
            }
        }
    }
}