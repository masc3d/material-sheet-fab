package org.deku.leoz.node.config

import org.deku.leoz.node.Application
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import sx.rs.client.RestClient
import javax.annotation.PostConstruct
import javax.inject.Inject

/**
 * Created by masc on 07/11/2016.
 */
@Configuration
@Profile(Application.PROFILE_NODE)
class RestClientConfiguration {
    @Inject
    private lateinit var remotePeerConfiguration: RemotePeerConfiguration

    @get:Bean
    val restClientFactory: org.deku.leoz.node.rest.RestClientFactory
        get() = org.deku.leoz.node.rest.RestClientFactory()

    @get:Bean
    val restClient: RestClient
        get() = this.restClientFactory.create()

    @PostConstruct
    fun onInitialize() {
        this.restClientFactory.also {
            it.host = remotePeerConfiguration.host!!
            it.port = remotePeerConfiguration.httpPort!!
            it.https = true
        }
    }
}