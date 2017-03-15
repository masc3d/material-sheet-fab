package org.deku.leoz.node.config

import org.deku.leoz.node.Application
import org.deku.leoz.rest.RestClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import javax.inject.Inject

/**
 * Created by masc on 07/11/2016.
 */
@Configuration
@Profile(Application.PROFILE_CLIENT_NODE)
open class RestClientConfiguration : org.deku.leoz.config.RestClientConfiguration() {

    @Inject
    private lateinit var remotePeerConfiguration: RemotePeerConfiguration

    @get:Bean
    open val restClient: RestClient
        get() = this.createClient(
                host = remotePeerConfiguration.host!!,
                port = remotePeerConfiguration.httpPort!!,
                https = true)
}