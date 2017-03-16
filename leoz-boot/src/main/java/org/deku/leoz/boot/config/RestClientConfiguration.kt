package org.deku.leoz.boot.config

import com.github.salomonbrys.kodein.*
import org.deku.leoz.config.HostConfiguration
import org.deku.leoz.config.RestConfiguration
import sx.rs.proxy.RestClientProxy
import sx.rs.proxy.RestEasyClientProxy
import java.net.URI

/**
 * Rest configuration
 * Created by masc on 08/11/2016.
 */
class RestClientConfiguration : org.deku.leoz.config.RestClientConfiguration() {
    override fun createClientProxyImpl(baseUri: URI, ignoreSsl: Boolean): RestClientProxy {
        return RestEasyClientProxy(baseUri, ignoreSsl)
    }

    companion object {
        val module = Kodein.Module {
            /** Rest configuration */
            bind<RestClientConfiguration>() with eagerSingleton {
                RestClientConfiguration()
            }

            /** Rest client */
            bind<RestClientProxy>() with provider {
                val config: RestClientConfiguration = instance()
                config.createClientProxy(config.httpHost, RestConfiguration.DEFAULT_PORT, config.https)
            }
        }
    }

    /**
     * HTTP host to use for rest clients
     */
    var httpHost: String = HostConfiguration.CENTRAL_HOST

    /**
     * Connect via HTTPS
     */
    var https: Boolean = true
}