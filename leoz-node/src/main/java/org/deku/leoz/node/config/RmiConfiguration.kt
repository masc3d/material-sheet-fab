package org.deku.leoz.node.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jmx.support.ConnectorServerFactoryBean
import org.springframework.context.annotation.DependsOn
import org.springframework.remoting.rmi.RmiRegistryFactoryBean
import org.springframework.context.annotation.Lazy

/**
 * Created by masc on 18.10.17.
 */
@Configuration
@Lazy(false)
open class RmiConfiguration {

    private val rmiHost: String = "localhost"
    private val rmiPort: Int = 13101

    @Bean
    open fun rmiRegistry(): RmiRegistryFactoryBean =
            RmiRegistryFactoryBean().also {
                it.port = rmiPort
                it.setAlwaysCreate(true)
            }

    @Bean
    open fun connectorServerFactoryBean(): ConnectorServerFactoryBean =
            ConnectorServerFactoryBean().also {
                it.setObjectName("connector:name=rmi")
                it.setServiceUrl("service:jmx:rmi://${rmiHost}:${rmiPort}/jndi/rmi://${rmiHost}:${rmiPort}/jmxrmi")
            }
}