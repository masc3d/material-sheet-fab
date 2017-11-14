package org.deku.leoz.node.rest

import org.deku.leoz.RestClientFactory
import sx.rs.proxy.RestClient
import sx.rs.proxy.RestEasyClient
import java.net.URI

/**
 * Rest client factory
 * Created by masc on 07/11/2016.
 */
open class RestClientFactory : RestClientFactory() {
    override fun create(
            baseUri: URI,
            ignoreSsl: Boolean,
            apiKey: String?): RestClient {

        return RestEasyClient(baseUri, ignoreSsl)
    }
}