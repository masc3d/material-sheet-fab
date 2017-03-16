package org.deku.leoz.node.config

import org.deku.leoz.node.Application
import sx.rs.proxy.RestClientProxy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import sx.rs.proxy.RestEasyClientProxy
import java.net.URI
import javax.inject.Inject

/**
 * Created by masc on 07/11/2016.
 */
@Configuration
@Profile(Application.PROFILE_CLIENT_NODE)
open class RestClientConfiguration : org.deku.leoz.config.RestClientConfiguration() {
    override fun createClientProxyImpl(baseUri: URI, ignoreSsl: Boolean): RestClientProxy {
        return RestEasyClientProxy(baseUri, ignoreSsl)
    }

    @Inject
    private lateinit var remotePeerConfiguration: RemotePeerConfiguration

    @get:Bean
    open val restClient: RestClientProxy
        get() = this.createClientProxy(
                host = remotePeerConfiguration.host!!,
                port = remotePeerConfiguration.httpPort!!,
                https = true)
}