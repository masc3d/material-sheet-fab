package org.deku.leoz.ui

import com.github.salomonbrys.kodein.*
import org.deku.leoz.config.Rest
import sx.rs.proxy.RestClient
import org.deku.leoz.service.internal.BundleServiceV2
import org.deku.leoz.service.internal.StationService
import org.slf4j.LoggerFactory
import sx.rs.proxy.JerseyClient
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

        return JerseyClient(baseUri, ignoreSsl)
    }

    init {
        this.host = "localhost"
        this.https = false
        this.port = Rest.DEFAULT_PORT
    }
}