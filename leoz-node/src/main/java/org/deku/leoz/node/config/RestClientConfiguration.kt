package org.deku.leoz.node.config

import org.deku.leoz.config.RestClientConfiguration
import org.deku.leoz.node.Application
import sx.rs.proxy.RestClientProxy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import sx.rs.proxy.RestEasyClientProxy
import java.net.URI
import javax.annotation.PostConstruct
import javax.inject.Inject

/**
 * Created by masc on 07/11/2016.
 */
@Configuration
@Profile(Application.PROFILE_CLIENT_NODE)
open class RestClientConfiguration : RestClientConfiguration() {

    override fun createClientProxy(baseUri: URI, ignoreSsl: Boolean, apiKey: String?): RestClientProxy {
        return RestEasyClientProxy(baseUri, ignoreSsl)
    }

    @Inject
    private lateinit var remotePeerConfiguration: RemotePeerConfiguration

    @PostConstruct
    open fun onInitialize() {
        this.host = remotePeerConfiguration.host!!
        this.port = remotePeerConfiguration.httpPort!!
        this.https = true
    }

    @get:Bean
    open val restClient: RestClientProxy
        get() = this.createDefaultClientProxy()
}