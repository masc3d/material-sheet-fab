package org.deku.leoz.config

import org.apache.activemq.artemis.api.core.TransportConfiguration
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient
import org.apache.activemq.artemis.api.jms.JMSFactoryType
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory
import org.apache.activemq.artemis.core.remoting.impl.netty.TransportConstants
import org.apache.activemq.artemis.jms.client.ActiveMQQueue
import org.apache.activemq.artemis.jms.client.ActiveMQTopic
import org.springframework.jms.connection.CachingConnectionFactory
import sx.io.serialization.KryoSerializer
import sx.io.serialization.gzip
import sx.jms.Broker
import sx.jms.Channel
import sx.jms.artemis.ArtemisBroker
import sx.jms.converters.DefaultConverter

/**
 * Created by masc on 05/10/2016.
 */
object ArtemisConfiguration {
    // Leoz broker configuration only has a single user which is defined here
    val USERNAME = "leoz"
    val PASSWORD = "iUbmQRejRI1P3SNtzwIM7wAgNazURPcVcBU7SftyZ0oha9FlnAdGAmXdEQwYlKFC"

    val broker by lazy {
        val broker = ArtemisBroker()
        broker.user = Broker.User(
                userName = ArtemisConfiguration.USERNAME,
                password = ArtemisConfiguration.PASSWORD,
                groupName = "")
        broker
    }

    val connectionFactory: CachingConnectionFactory by lazy {
        val transportParams = mutableMapOf<String, Any>()
        transportParams.put(TransportConstants.HOST_PROP_NAME, "0.0.0.0")
        transportParams.put(TransportConstants.PORT_PROP_NAME, 61616)

        val transportConfiguration = TransportConfiguration(NettyConnectorFactory::class.java.getName(), transportParams)

        val cf = ActiveMQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF, transportConfiguration)
                .setUser(USERNAME)
                .setPassword(PASSWORD)

        CachingConnectionFactory(cf)
    }

    val centralLogQueue: Channel.Configuration by lazy {
        val c = Channel.Configuration(
                connectionFactory = this.connectionFactory,
                destination = ActiveMQQueue("leoz.log.queue"),
                deliveryMode = Channel.DeliveryMode.Persistent,
                converter = DefaultConverter(KryoSerializer().gzip))

        c.priority = 1
        c
    }

    val entitySyncQueue: Channel.Configuration by lazy {
        Channel.Configuration(
                connectionFactory = this.connectionFactory,
                destination = ActiveMQQueue("leoz.entity-sync.queue"),
                converter = DefaultConverter(KryoSerializer().gzip))
    }

    val entitySyncTopic: Channel.Configuration by lazy {
        Channel.Configuration(
                connectionFactory = this.connectionFactory,
                destination = ActiveMQTopic("leoz.entity-sync.topic"),
                converter = DefaultConverter(KryoSerializer().gzip))
    }
}