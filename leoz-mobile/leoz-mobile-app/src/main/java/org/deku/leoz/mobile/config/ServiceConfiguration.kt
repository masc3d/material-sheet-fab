package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.bind
import com.github.salomonbrys.kodein.erased.eagerSingleton
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.erased.singleton
import org.deku.leoz.RestClientFactory
import org.deku.leoz.mobile.mq.MqttListeners
import org.deku.leoz.mobile.service.LocationCache
import org.deku.leoz.mobile.service.NotificationService
import org.deku.leoz.mobile.service.UpdateService
import org.threeten.bp.Duration
import sx.ConfigurationMap
import sx.ConfigurationMapPath
import sx.rs.proxy.FeignClient

/**
 * Service configuration
 * Created by masc on 16.05.17.
 */
class ServiceConfiguration {
    companion object {
        val module = Kodein.Module {
            /**
             * Notification service
             */
            bind<NotificationService>() with eagerSingleton {
                val service = NotificationService()

                // Wire notification service with listener
                instance<MqttListeners>().mobile.topic.addDelegate(service)

                service
            }

            /**
             * Application update service
             */
            bind<UpdateService>() with singleton {

                @ConfigurationMapPath("update")
                class Settings(private val map: ConfigurationMap) {
                    val enabled: Boolean by map.value(true)
                    val bundleName: String by map.value("")
                    val versionAlias: String? by map.value<String?>(null)
                    val force: Boolean by map.value(false)
                    val period: Int by map.value(3600)

                    @sx.ConfigurationMapPath("update.remote")
                    inner class Remote {
                        val host: String by map.value("")
                        val http = Http()

                        @sx.ConfigurationMapPath("update.remote.http")
                        inner class Http {
                            val port: Int by map.value(0)
                            val ssl: Boolean by map.value(true)
                        }
                    }

                    val remote = Remote()
                }

                val settings = Settings(instance<ConfigurationMap>())

                val restClientFactory: RestClientFactory = instance()

                val service = UpdateService(
                        executorService = instance(),
                        bundleName = settings.bundleName,
                        versionAlias = settings.versionAlias,
                        identity = instance(),
                        period = Duration.ofSeconds(settings.period.toLong()),
                        restClient = restClientFactory.create(
                                uri = restClientFactory.createUri(
                                        host = settings.remote.host,
                                        port = settings.remote.http.port,
                                        https = settings.remote.http.ssl
                                )
                        ) as FeignClient
                )

                service.force = settings.force

                if (settings.enabled)
                    service.start()

                service
            }

            bind<LocationCache>() with singleton {
                LocationCache()
            }
        }
    }
}