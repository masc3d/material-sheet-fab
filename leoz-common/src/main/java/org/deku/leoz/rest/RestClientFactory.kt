package org.deku.leoz.rest

import org.deku.leoz.config.HostConfiguration
import org.deku.leoz.config.Rest
import sx.rs.client.RestClient
import java.net.URI

/**
 * Base class for JAX/RS REST client factories
 * Created by masc on 07/11/2016.
 */
abstract class RestClientFactory {
    /**
     * Overridden in derived configurations to provide a specific proxy client
     */
    abstract fun create(baseUri: URI, ignoreSsl: Boolean, apiKey: String? = null): RestClient

    /**
     * HTTP host to use for rest clients
     */
    var host: String = "localhost"

    /**
     * Connect via HTTPS
     */
    var https: Boolean = false

    /**
     * Header API key
     */
    var apiKey: String? = null

    /**
     * HTTP/S port
     */
    var port: Int = Rest.DEFAULT_PORT

    fun createUri(https: Boolean, host: String, port: Int, basePath: String = Rest.MAPPING_PREFIX): URI {
        val scheme = when (https) {
            true -> "https"
            false -> "http"
        }

        return URI("${scheme}://${host}:${port}")
                .resolve(basePath)
    }

    /**
     * Factory method for creating a leoz rest client
     */
    fun create(uri: URI): RestClient {
        // Ignore SSL certificate for (usually testing/dev) remote hosts which are not in the business domain
        val ignoreSslCertificate = !uri.host.endsWith(HostConfiguration.CENTRAL_DOMAIN)

        return this.create(uri, ignoreSslCertificate, this.apiKey)
    }

    /**
     * Factory method for creating a default leoz rest client
     * using the configurations remote settings (host/port)
     */
    fun create(): RestClient {
        val uri = this.createUri(https, host, port)

        return this.create(uri)
    }
}