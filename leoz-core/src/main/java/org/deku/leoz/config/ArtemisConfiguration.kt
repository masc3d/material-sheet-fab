package org.deku.leoz.config

import org.apache.activemq.artemis.api.core.TransportConfiguration
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient
import org.apache.activemq.artemis.api.jms.JMSFactoryType
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory
import org.apache.activemq.artemis.core.remoting.impl.netty.TransportConstants
import org.springframework.jms.connection.CachingConnectionFactory
import sx.io.serialization.KryoSerializer
import sx.io.serialization.gzip
import sx.mq.MqBroker
import sx.mq.jms.JmsEndpoint
import sx.mq.jms.JmsChannel
import sx.mq.jms.artemis.ArtemisBroker
import sx.mq.jms.artemis.ArtemisContext
import sx.mq.jms.converters.DefaultJmsConverter

/**
 * Created by masc on 05/10/2016.
 */
object ArtemisConfiguration {
    // Leoz broker configuration only has a single user which is defined here
    val USERNAME = "leoz"
    val PASSWORD = "iUbmQRejRI1P3SNtzwIM7wAgNazURPcVcBU7SftyZ0oha9FlnAdGAmXdEQwYlKFC"

    val broker by lazy {
        val broker = ArtemisBroker()
        broker.user = MqBroker.User(
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

    val context = ArtemisContext(connectionFactory = this.connectionFactory)

    val centralLogQueue: JmsEndpoint by lazy {
        JmsEndpoint(
                context = this.context,
                destination = this.context.createQueue("leoz.log.queue"),
                deliveryMode = JmsChannel.DeliveryMode.Persistent,
                converter = DefaultJmsConverter(KryoSerializer().gzip),
                priority = 1)
    }

    val entitySyncQueue: JmsEndpoint by lazy {
        JmsEndpoint(
                context = this.context,
                destination = this.context.createQueue("leoz.entity-sync.queue"),
                converter = DefaultJmsConverter(KryoSerializer().gzip))
    }

    val entitySyncTopic: JmsEndpoint by lazy {
        JmsEndpoint(
                context = this.context,
                destination = this.context.createTopic("leoz.entity-sync.topic"),
                converter = DefaultJmsConverter(KryoSerializer().gzip))
    }
}