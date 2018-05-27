package org.deku.leoz.boot

import org.deku.leoz.rest.RestClientFactory
import sx.rs.client.RestClient
import sx.rs.client.RestEasyClient
import java.net.URI

/**
 * Rest configuration
 * Created by masc on 08/11/2016.
 */
class RestClientFactory : RestClientFactory() {
    override fun create(
            baseUri: URI,
            ignoreSsl: Boolean,
            apiKey: String?): RestClient {

        return RestEasyClient(
                baseUri = baseUri,
                ignoreSslCertificate = ignoreSsl
        )
    }
}