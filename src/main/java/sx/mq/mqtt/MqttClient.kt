package sx.mq.mqtt

import io.reactivex.Observable
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient
import org.eclipse.paho.client.mqttv3.IMqttMessageListener
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import sx.LazyInstance
import sx.time.Duration
import java.util.concurrent.TimeUnit

/**
 * @property channel Mqtt channel
 * Created by masc on 07.05.17.
 */
class MqttClient(
        val channel: MqttChannel,
        mqttClient: IMqttAsyncClient? = null
) : sx.mq.MqClient {

    companion object {
        val DEFAULT_RECEIVE_TIMEOUT = Duration.ofSeconds(10)
    }

    private var ownsClient: Boolean = false

    /**
     * Lazy session instance
     */
    private val mqttClientInstance = LazyInstance<IMqttAsyncClient>(
            LazyInstance.ThreadSafetyMode.None)

    val mqttClient by lazy {
        this.mqttClientInstance.get()
    }

    init {
        if (mqttClient == null) {
            // Lazily create client from context
            mqttClientInstance.set {
                val client = this.channel.context.client()
                client.connect(this.channel.context.connectOptions).waitForCompletion()
                client
            }
            // In this case we own the client and need to close it when done
            ownsClient = true
        } else {
            mqttClientInstance.set { mqttClient }
            ownsClient = false
        }
    }

    override fun close() {
        if (this.ownsClient) {
            this.mqttClientInstance.ifSet {
                it.disconnect().waitForCompletion()
            }
        }
    }

    override fun <T> receive(messageType: Class<T>): T {
        return Observable.create<T> {
            try {
                this.mqttClient.subscribe(
                        this.channel.topicName,
                        this.channel.qos,
                        object : IMqttMessageListener {
                            override fun messageArrived(topic: String, message: MqttMessage) {
                                try {
                                    @Suppress("UNCHECKED_CAST")
                                    it.onNext(
                                            this@MqttClient.channel.serializer.deserializeFrom(message.payload) as T)
                                    it.onComplete()
                                } catch(e: Throwable) {
                                    it.onError(e)
                                }
                            }
                        }).waitForCompletion(DEFAULT_RECEIVE_TIMEOUT.toMillis())
            } catch(e: Throwable) {
                it.onError(e)
            }
        }
                .timeout(DEFAULT_RECEIVE_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
                .blockingFirst()
    }

    override fun send(message: Any) {
        val mqttMessage = MqttMessage(this.channel.serializer.serializeToByteArray(message))
        mqttMessage.qos = this.channel.qos

        this.mqttClient.publish(
                this.channel.topicName,
                mqttMessage
        )
                .waitForCompletion()
    }
}
