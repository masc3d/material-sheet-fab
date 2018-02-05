package sx.mq.mqtt

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Observable
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
import sx.log.slf4j.info
import sx.mq.TestChannels
import sx.mq.config.MqTestConfiguration
import sx.mq.jms.activemq.ActiveMQBroker
import sx.mq.message.TestMessage
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Created by masc on 21.05.17.
 */
class MqttDispatcherTest {
    val log = LoggerFactory.getLogger(this.javaClass)

    init {
        Kodein.global.mutable = true
        Kodein.global.clear()
        Kodein.global.addImport(MqTestConfiguration.module)
    }

    val broker: ActiveMQBroker by Kodein.global.lazy.instance()

    @Before
    fun setup() {
        // Add composite destination, forwarding topic messages to queue
        val d = CompositeTopic()
        d.name = TestChannels.testQueueForwarder.destinationName
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
                    executorService = Executors.newCachedThreadPool(),
                    persistence = MqttInMemoryPersistence()
            )
        }

        val context by lazy {
            MqttContext(
                    client = { this.dispatcher })
        }

        val testQueue by lazy {
            TestChannels.testQueue.toMqtt(
                    context
            )
        }

        val testQueue2 by lazy {
            TestChannels.testQueue2.toMqtt(
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
        val publisher = Observable
                .interval(1, TimeUnit.SECONDS)
                .take(5)
                .doOnNext {
                    Stopwatch.createStarted("publish", { log.info(it) }, { _, _ ->
                        for (i in 0..250) {
                            Mqtt.testQueue.channel().send(TestMessage())
                        }
                        for (i in 0..5) {
                            Mqtt.testQueue2.channel().send(TestMessage())
                        }
                    })
                }
                .doOnComplete {
                    log.info { "PUBLISHER COMPLETED" }
                }

        Mqtt.dispatcher.connect()

        Mqtt.dispatcher.statisticsUpdateEvent
                .map { it.get(Mqtt.testQueue2.topicName) == 0 }
                .distinctUntilChanged()
                .filter { it == true }
                .subscribe {
                    log.info { "QUEUE2 DONE" }
                }

        // Wait for completion
        Completable.concat(listOf(
                // Wait for publisher to complete
                publisher.ignoreElements(),
                // And then for statistics to indicate everything has been processed
                Mqtt.dispatcher.statisticsUpdateEvent
                        .takeUntil { it.values.all { it == 0 } }
                        .ignoreElements()
        ))
                .blockingAwait()
    }
}