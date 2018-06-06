package org.deku.leoz.node.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.jmx.support.ConnectorServerFactoryBean
import org.springframework.remoting.rmi.RmiRegistryFactoryBean

/**
 * Created by masc on 18.10.17.
 */
@Configuration
@Lazy(false)
class RmiConfiguration {

    private val rmiHost: String = "localhost"
    private val rmiPort: Int = 13101

    @Bean
    fun rmiRegistry(): RmiRegistryFactoryBean {
        System.setProperty("java.rmi.server.hostname", rmiHost)

        return RmiRegistryFactoryBean().also {
            it.port = rmiPort
            it.setAlwaysCreate(true)
        }
    }

    @Bean
    fun connectorServerFactoryBean(): ConnectorServerFactoryBean {
        return ConnectorServerFactoryBean().also {
            it.setObjectName("connector:name=rmi")
            it.setServiceUrl("service:jmx:rmi://${rmiHost}:${rmiPort}/jndi/rmi://${rmiHost}:${rmiPort}/jmxrmi")
        }
    }
}