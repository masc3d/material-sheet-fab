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
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.slf4j.LoggerFactory
import sx.Stopwatch
import sx.mq.Channels
import sx.mq.config.MqTestConfiguration
import sx.mq.jms.activemq.ActiveMQBroker
import sx.mq.message.TestMessage

/**
 * Created by masc on 21.05.17.
 */
class MqttDispatcherTest {
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
        d.forwardTo = listOf(MqttTest.Jms.testQueue.destination)
        d.isForwardOnly = true

        this.broker.addCompositeDestination(d)

        this.broker.start()
    }

    @After
    fun tearDown() {
        this.broker.stop()
    }

    object Mqtt {
        val connectOptions: MqttConnectOptions by lazy {
            val mqttConnectOptions = MqttConnectOptions()
            mqttConnectOptions.isCleanSession = true
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

        val dispatcher: MqttDispatcher by lazy {
            MqttDispatcher(
                    client = this.client,
                    persistence = MqttInMemoryPersistence())
        }

        val context by lazy {
            MqttContext(
                    client = { this.dispatcher })
        }

        val testQueue by lazy {
            Channels.testQueue.toMqtt(
                    context
            )
        }
    }

    @Test
    fun testConnect() {
        Mqtt.dispatcher.connect()

        // Dispatcher connects internally & asynchronously.
        Thread.sleep(1000)
        Assert.assertTrue(Mqtt.dispatcher.isConnected)

        Mqtt.dispatcher.disconnect()
                .blockingAwait()
    }

    @Test
    fun testPublish() {
        Mqtt.dispatcher.connect()

        Thread.sleep(1000)

        Stopwatch.createStarted("publish", { log.info(it) }, { _, _ ->
            for (i in 0..10000){
                Mqtt.testQueue.channel().send(TestMessage())
            }
        })


    }
}