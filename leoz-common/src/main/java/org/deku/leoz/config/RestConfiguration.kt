package org.deku.leoz.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.factory
import org.deku.leoz.rest.RestClient
import java.net.URI

/**
 * Created by masc on 07/11/2016.
 */
abstract class RestConfiguration {
    companion object {
        val DEFAULT_PORT = 13000
        val MAPPING_PREFIX = "/rs/api"
    }

    /**
     * Factory method for creating a leoz rest client
     * @param host REST server host name
     * @param port REST server port
     * @param https Use https or regular http. Defaults to false (=http)
     */
    fun createClient(host: String, port: Int, https: Boolean = false): RestClient {
        val scheme = when(https) {
            true -> "https"
            false -> "http"
        }

        val uri = URI("${scheme}://${host}:${port}")
                .resolve(RestConfiguration.MAPPING_PREFIX)

        // Ignore SSL certificate for (usually testing/dev) remote hosts which are not in the business domain
        val ignoreSslCertificate = !host.endsWith("derkurier.de")

        return RestClient(
                baseUri = uri,
                ignoreSslCertificate = ignoreSslCertificate)
    }
}