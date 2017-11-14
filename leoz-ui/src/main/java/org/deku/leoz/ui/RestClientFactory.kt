package org.deku.leoz.ui

import org.deku.leoz.config.Rest
import org.deku.leoz.rest.RestClientFactory
import sx.rs.client.RestClient
import sx.rs.client.JerseyClient
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

        return JerseyClient(baseUri, ignoreSsl)
    }

    init {
        this.host = "localhost"
        this.https = false
        this.port = Rest.DEFAULT_PORT
    }
}