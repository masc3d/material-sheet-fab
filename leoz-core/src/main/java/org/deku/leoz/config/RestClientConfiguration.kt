package org.deku.leoz.config

import sx.rs.proxy.RestClientProxy
import sx.rs.proxy.RestEasyClientProxy
import java.net.URI

/**
 * Base class for JAX/RS REST client configurations
 * Created by masc on 07/11/2016.
 */
abstract class RestClientConfiguration {
    /**
     * Overridden in derived configurations to provide a specific proxy client
     */
    protected abstract fun createClientProxyImpl(baseUri: URI, ignoreSsl: Boolean): RestClientProxy

    /**
     * Factory method for creating a leoz rest client
     * @param host REST server host name
     * @param port REST server port
     * @param https Use https or regular http. Defaults to false (=http)
     */
    fun createClientProxy(host: String, port: Int, https: Boolean = false): RestClientProxy {
        val scheme = when(https) {
            true -> "https"
            false -> "http"
        }

        val uri = URI("${scheme}://${host}:${port}")
                .resolve(RestConfiguration.MAPPING_PREFIX)

        // Ignore SSL certificate for (usually testing/dev) remote hosts which are not in the business domain
        val ignoreSslCertificate = !host.endsWith(HostConfiguration.CENTRAL_DOMAIN)

        return this.createClientProxyImpl(uri, ignoreSslCertificate)
    }
}