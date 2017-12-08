package sx.mq.mqtt

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import org.apache.activemq.broker.region.virtual.CompositeTopic
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.slf4j.LoggerFactory
import org.threeten.bp.Duration
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.mq.Channels
import sx.mq.config.MqTestConfiguration
import sx.mq.jms.activemq.ActiveMQBroker
import sx.mq.jms.activemq.ActiveMQContext
import sx.mq.jms.activemq.ActiveMQPooledConnectionFactory
import sx.mq.jms.channel
import sx.mq.jms.listeners.SpringJmsListener
import sx.mq.jms.toJms
import sx.mq.message.TestMessage
import java.net.URI
import java.util.concurrent.Executors

/**
 * Mqtt prototype tests
 * Created by masc on 05.05.17.
 */
class MqttTest {
    val log = LoggerFactory.getLogger(this.javaClass)

    init {
        Kodein.global.addImport(MqTestConfiguration.module)
    }

    val broker: ActiveMQBroker by Kodein.global.lazy.instance()

    @Before
    fun setup() {
        // Add composite destination, forwarding topic messages to queue
        val d = CompositeTopic()
        d.name = Channels.testQueueForwarder.destinationName
        d.forwardTo = listOf(Jms.testQueue.destination)
        d.isForwardOnly = true

        this.broker.addCompositeDestination(d)

        this.broker.start()
    }

    @After
    fun tearDown() {
        this.broker.stop()
    }

    @Test
    fun testConnect() {
        Mqtt.client.connect().blockingGet()
        Mqtt.client.disconnect().blockingGet()
    }

    object Jms {
        val context by lazy {
            ActiveMQContext(ActiveMQPooledConnectionFactory(URI.create("tcp://localhost:61616"),
                    username = MqTestConfiguration.USERNAME,
                    password = MqTestConfiguration.PASSWORD))
        }

        val testQueue by lazy {
            Channels.testQueue.toJms(
                    context = this.context
            )
        }

        val testTopic by lazy {
            Channels.testTopic.toJms(
                    context = this.context
            )
        }

        val testQueueForwarder by lazy {
            Channels.testQueueForwarder.toJms(
                    context = this.context
            )
        }
    }

    object Mqtt {
        val connectOptions: MqttConnectOptions by lazy {
            val mqttConnectOptions = MqttConnectOptions()
            mqttConnectOptions.isCleanSession = false
            mqttConnectOptions.userName = MqTestConfiguration.USERNAME
            mqttConnectOptions.password = MqTestConfiguration.PASSWORD.toCharArray()
            mqttConnectOptions
        }

        val client: MqttRxClient by lazy {
            val mqttClient = MqttRxClient(
                    parent = MqttAsyncClient("tcp://localhost:61616", "mqtt-client-1", MemoryPersistence()),
                    connectOptions = connectOptions)
            mqttClient
        }

        val context by lazy {
            MqttContext(
                    client = { this.client })
        }

        val testQueueForwarder by lazy {
            Channels.testQueueForwarder.toMqtt(
                    context = this.context,
                    qos = 2
            )
        }

        val testTopic by lazy {
            Channels.testTopic.toMqtt(
                    context = this.context
            )
        }
    }


    @Test
    fun testListener() {
        val listener = object : SpringJmsListener(
                Jms.testQueue,
                Executors.newSingleThreadExecutor(),
                "mqtt-subscriber-1") {

        }

        // Setup log message listener
        listener.addDelegate(object : MqHandler<TestMessage> {
            override fun onMessage(message: TestMessage, replyChannel: MqChannel?) {
                log.info("${message}: ${message.logEntries.count()}")
            }
        })

        listener.start()

        Thread.sleep(Long.MAX_VALUE)
    }

    @Test
    fun testMqttListener() {
        Mqtt.client.connect()
                .blockingGet()

        val listener = MqttListener(
                mqttEndpoint = Mqtt.testTopic
        )

        listener.start()

        // Setup log message listener
        listener.addDelegate(object : MqHandler<Any> {
            @MqHandler.Types(TestMessage::class)
            override fun onMessage(message: Any, replyChannel: MqChannel?) {
                when (message) {
                    is TestMessage -> {
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
    fun testPublishToQueueForwarderViaJms() {
        val logMessage = TestMessage(nodeKey = "MqttPublisher", logEntries = arrayOf())

        // Receive
        Jms.testQueueForwarder.channel().use {
            it.ttl = Duration.ofMinutes(5)

            for (i in 0..100)
                it.send(logMessage)
        }
    }

    /**
     * For fallback testing via JMS
     */
    @Test
    fun testPublishToTopicViaJms() {
        val logMessage = TestMessage(nodeKey = "MqttPublisher", logEntries = arrayOf())

        // Receive
        Jms.testTopic.channel().use {
            it.ttl = Duration.ofMinutes(5)

            for (i in 0..100)
                it.send(logMessage)
        }
    }

    /**
     * Test publishing to virtual topic via mqtt
     */
    @Test
    fun testPublishToQueueForwarderViaMqtt() {
        Mqtt.client.connect()
                .blockingGet()

        Mqtt.testQueueForwarder.channel().use {
            for (i in 0..100) {
                val logMessage = TestMessage(nodeKey = "MqttPublisher", logEntries = arrayOf())
                it.send(logMessage)
            }
        }
    }

    /**
     * Test plain channel client send/receive on mqtt topic
     */
    @Test
    fun testSendAndReceiveMqttTopic() {
        Mqtt.testTopic.channel().use {
            it.send(TestMessage(nodeKey = "MqttPublisher", logEntries = arrayOf()))
        }

        Mqtt.testTopic.channel().use {
            log.info("${it.receive(TestMessage::class.java)}")
        }
    }
}
