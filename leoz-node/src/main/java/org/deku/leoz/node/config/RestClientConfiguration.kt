package org.deku.leoz.node.config

import org.deku.leoz.node.web.WebContextInitializer
import org.deku.leoz.rest.RestClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.URI
import javax.inject.Inject

/**
 * Created by masc on 07/11/2016.
 */
@Configuration
open class RestClientConfiguration {

    @Inject
    private lateinit var remotePeerConfiguration: RemotePeerConfiguration

    @Bean
    open fun restClient(): RestClient {
        val uri = URI("https://${remotePeerConfiguration.host!!}:${remotePeerConfiguration.httpPort!!}")
                .resolve(remotePeerConfiguration.httpPath!!)
                .resolve(WebContextInitializer.RESTEASY_MAPPING_PATH)

        // Ignore SSL certificate for (usually testing/dev) remote hosts which are not in the business domain
        val ignoreSslCertificate = !remotePeerConfiguration.host!!.endsWith("derkurier.de")

        return RestClient(
                baseUri = uri,
                ignoreSslCertificate = ignoreSslCertificate)
    }
}