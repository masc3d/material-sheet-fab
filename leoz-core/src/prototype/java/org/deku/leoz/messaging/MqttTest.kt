package org.deku.leoz.messaging

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import org.apache.activemq.command.ActiveMQTopic
import org.deku.leoz.config.ActiveMQConfiguration
import org.deku.leoz.config.MessagingTestConfiguration
import org.deku.leoz.log.LogMessage
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.slf4j.LoggerFactory
import sx.io.serialization.KryoSerializer
import sx.io.serialization.gzip
import sx.jms.Channel
import sx.jms.Handler
import sx.jms.activemq.ActiveMQBroker
import sx.jms.activemq.ActiveMQPooledConnectionFactory
import sx.jms.converters.DefaultConverter
import sx.jms.listeners.SpringJmsListener
import sx.time.Duration
import java.net.URI
import java.util.concurrent.Executors

/**
 * Created by masc on 05.05.17.
 */
class MqttTest {
    val log = LoggerFactory.getLogger(this.javaClass)

    init {
        Kodein.global.addImport(MessagingTestConfiguration.module)
    }

    val broker: ActiveMQBroker by Kodein.global.lazy.instance()

    @Before
    fun setup() {
        this.broker.start()
    }

    @After
    fun tearDown() {
        this.broker.stop()
    }

    private val client: MqttClient by lazy {
        val mqttClient = MqttClient("tcp://localhost:61616", "testclient", MemoryPersistence())
        mqttClient
    }

    private val connectOptions: MqttConnectOptions by lazy {
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isCleanSession = true
        mqttConnectOptions.userName = ActiveMQConfiguration.USERNAME
        mqttConnectOptions.password = ActiveMQConfiguration.PASSWORD.toCharArray()
        mqttConnectOptions
    }

    @Test
    fun testConnection() {
        this.client.connect(connectOptions)
        this.client.disconnect()
    }

    val channelConfig by lazy {
        Channel.Configuration(
                destination = ActiveMQTopic("leoz.test.mqtt"),
                connectionFactory = ActiveMQPooledConnectionFactory(URI.create("tcp://localhost:61616"),
                        username = ActiveMQConfiguration.USERNAME,
                        password = ActiveMQConfiguration.PASSWORD),
                converter = DefaultConverter(KryoSerializer().gzip),
                deliveryMode = Channel.DeliveryMode.Persistent)
    }

    @Test
    fun testStartBroker() {
        this.broker.start()
        Thread.sleep(Long.MAX_VALUE)
    }

    @Test
    fun testListener() {
        val listener = object : SpringJmsListener(
                { Channel(channelConfig) },
                Executors.newSingleThreadExecutor(),
                "mqtt-subscriber-1") {

        }

        // Setup log message listener
        listener.addDelegate(object : Handler<LogMessage> {
            override fun onMessage(message: LogMessage, replyChannel: Channel?) {
                log.info("${message}: ${message.logEntries.count()}")
            }
        })

        listener.start()

        Thread.sleep(Long.MAX_VALUE)
    }

    @Test
    fun testPublishViaJms() {
        val logMessage = LogMessage(nodeKey = "MqttPublisher", logEntries = arrayOf())

        val channel = Channel(
                configuration = channelConfig)

        // Receive
        channel.use {
            it.ttl = Duration.ofMinutes(5)

            for (i in 0..100)
                it.send(logMessage)
        }

    }

    @Test
    fun testPublish() {
        this.client.connect(connectOptions)

        val TOPIC = "leoz/test/mqtt"
        val serializer = KryoSerializer().gzip

        val logMessage = LogMessage(nodeKey = "MqttPublisher", logEntries = arrayOf())

        val message = MqttMessage(serializer.serializeToByteArray(logMessage))
        message.qos = 2

        for (i in 0..100) {
            this.client.publish(TOPIC, message)
        }

        this.client.disconnect()
    }
}