package org.deku.leoz.messaging

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import org.apache.activemq.broker.region.virtual.CompositeTopic
import org.deku.leoz.config.MessagingTestConfiguration
import org.deku.leoz.config.MqConfiguration
import org.deku.leoz.log.LogMessage
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.slf4j.LoggerFactory
import sx.io.serialization.KryoSerializer
import sx.io.serialization.gzip
import sx.mq.MqChannel
import sx.mq.MqClient
import sx.mq.DestinationType
import sx.mq.MqHandler
import sx.mq.jms.activemq.ActiveMQBroker
import sx.mq.jms.activemq.ActiveMQContext
import sx.mq.jms.activemq.ActiveMQPooledConnectionFactory
import sx.mq.jms.client
import sx.mq.jms.listeners.SpringJmsListener
import sx.mq.jms.toJms
import sx.mq.mqtt.MqttContext
import sx.mq.mqtt.MqttListener
import sx.mq.mqtt.client
import sx.mq.mqtt.toMqtt
import sx.time.Duration
import java.net.URI
import java.util.concurrent.Executors

/**
 * Mqtt prototype tests
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
        // Add composite destination, forwarding topic messages to queue
        val d = CompositeTopic()
        d.name = this.queueTopicChannel.destinationName
        d.forwardTo = listOf(this.jmsQueueChannel.destination)
        d.isForwardOnly = true

        this.broker.addCompositeDestination(d)

        this.broker.start()
    }

    @After
    fun tearDown() {
        this.broker.stop()
    }

    private val mqttClient: MqttAsyncClient by lazy {
        val mqttClient = MqttAsyncClient("tcp://localhost:61616", "mqtt-client-1", MemoryPersistence())
        mqttClient
    }

    private val connectOptions: MqttConnectOptions by lazy {
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isCleanSession = false
        mqttConnectOptions.userName = MqConfiguration.USERNAME
        mqttConnectOptions.password = MqConfiguration.PASSWORD.toCharArray()
        mqttConnectOptions
    }

    @Test
    fun testConnection() {
        this.mqttClient.connect(connectOptions)
        this.mqttClient.disconnect()
    }

    val jmsContext by lazy {
        ActiveMQContext(ActiveMQPooledConnectionFactory(URI.create("tcp://localhost:61616"),
                username = MqConfiguration.USERNAME,
                password = MqConfiguration.PASSWORD))
    }

    val mqttContext by lazy {
        MqttContext(
                client = { this.mqttClient },
                connectOptions = this.connectOptions
        )
    }

    /** Topic channel for testing notifications */
    val topicChannel by lazy {
        MqChannel(
                destinationName = "leoz.test.topic",
                destinationType = DestinationType.Topic,
                persistent = true,
                serializer = KryoSerializer().gzip
        )
    }

    /** Queue channel for testing queues with topic forwarding via mqtt */
    val queueChannel by lazy {
        MqChannel(
                destinationName = "leoz.test.queue",
                destinationType = DestinationType.Queue,
                persistent = true,
                serializer = KryoSerializer().gzip
        )
    }

    /** Virtual topic used for mqtt clients to post to queue */
    val queueTopicChannel by lazy {
        MqChannel(
                destinationName = "leoz.test.queue.topic",
                destinationType = DestinationType.Topic,
                serializer = KryoSerializer().gzip
        )
    }

    // Referring JMS channels
    val jmsQueueChannel by lazy {
        this.queueChannel.toJms(
                context = this.jmsContext
        )
    }

    val jmsTopicChannel by lazy {
        this.topicChannel.toJms(
                context = this.jmsContext
        )
    }

    val jmsQueueTopicChannel by lazy {
        this.queueTopicChannel.toJms(
                context = this.jmsContext
        )
    }

    // Referring MQTT channels
    val mqttQueueTopicChannel by lazy {
        this.queueTopicChannel.toMqtt(
                context = this.mqttContext,
                qos = 2
        )
    }

    val mqttTopicChannel by lazy {
        this.topicChannel.toMqtt(
                context = this.mqttContext
        )
    }

    @Test
    fun testListener() {
        val listener = object : SpringJmsListener(
                jmsQueueChannel,
                Executors.newSingleThreadExecutor(),
                "mqtt-subscriber-1") {

        }

        // Setup log message listener
        listener.addDelegate(object : MqHandler<LogMessage> {
            override fun onMessage(message: LogMessage, replyClient: MqClient?) {
                log.info("${message}: ${message.logEntries.count()}")
            }
        })

        listener.start()

        Thread.sleep(Long.MAX_VALUE)
    }

    @Test
    fun testMqttListener() {
        this.mqttClient.connect(
                this.connectOptions
        ).waitForCompletion()

        val listener = MqttListener(
                mqttClient = this.mqttClient,
                mqttChannel = this.mqttTopicChannel
        )

        listener.start()

        // Setup log message listener
        listener.addDelegate(object : MqHandler<Any> {
            @MqHandler.Types(LogMessage::class)
            override fun onMessage(message: Any, replyClient: MqClient?) {
                when (message) {
                    is LogMessage -> {
                        log.info("MQTT ${message}: ${message.logEntries.count()}")
                    }
                }
            }
        })

        Thread.sleep(Long.MAX_VALUE)
    }

    /**
     * For fallback testing via JMS
     */
    @Test
    fun testPublishToQueueTopicViaJms() {
        val logMessage = LogMessage(nodeKey = "MqttPublisher", logEntries = arrayOf())

        // Receive
        jmsQueueTopicChannel.client().use {
            it.ttl = Duration.ofMinutes(5)

            for (i in 0..100)
                it.send(logMessage)
        }
    }

    /**
     * For fallback testing via JMS
     */
    @Test
    fun testPublishToTopicTopicViaJms() {
        val logMessage = LogMessage(nodeKey = "MqttPublisher", logEntries = arrayOf())

        // Receive
        jmsTopicChannel.client().use {
            it.ttl = Duration.ofMinutes(5)

            for (i in 0..100)
                it.send(logMessage)
        }
    }

    /**
     * Test publishing to virtual topic via mqtt
     */
    @Test
    fun testPublishToQueueTopicViaMqtt() {
        this.mqttQueueTopicChannel.client().use {
            for (i in 0..100) {
                val logMessage = LogMessage(nodeKey = "MqttPublisher", logEntries = arrayOf())
                it.send(logMessage)
            }
        }
    }

    /**
     * Test plain channel client send/receive on mqtt topic
     */
    @Test
    fun testSendAndReceiveMqttTopic() {
        this.mqttTopicChannel.client().use {
            it.send(LogMessage(nodeKey = "MqttPublisher", logEntries = arrayOf()))
        }

        this.mqttTopicChannel.client().use {
            log.info("${it.receive(LogMessage::class.java)}")
        }
    }
}
