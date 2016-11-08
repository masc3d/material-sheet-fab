package org.deku.leoz.node.config

import org.deku.leoz.config.RestConfiguration
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
open class RestClientConfiguration : org.deku.leoz.config.RestConfiguration() {

    @Inject
    private lateinit var remotePeerConfiguration: RemotePeerConfiguration

    @Bean
    open fun restClient(): RestClient {
        return this.createClient(
                host = remotePeerConfiguration.host!!,
                port = remotePeerConfiguration.httpPort!!,
                https = true)
    }
}