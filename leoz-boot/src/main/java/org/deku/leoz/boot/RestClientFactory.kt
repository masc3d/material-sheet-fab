package org.deku.leoz.boot

import com.github.salomonbrys.kodein.*
import sx.rs.proxy.RestClient
import sx.rs.proxy.RestEasyClient
import java.net.URI

/**
 * Rest configuration
 * Created by masc on 08/11/2016.
 */
class RestClientFactory : org.deku.leoz.RestClientFactory() {
    override fun create(
            baseUri: URI,
            ignoreSsl: Boolean,
            apiKey: String?): RestClient {

        return RestEasyClient(baseUri, ignoreSsl)
    }
}