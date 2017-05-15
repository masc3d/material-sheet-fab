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
        val channel: MqttChannel
) : sx.mq.MqClient {

    companion object {
        val DEFAULT_RECEIVE_TIMEOUT = Duration.ofSeconds(10)
    }

    val mqttClient by lazy {
        this.channel.context.client()
    }

    override fun close() {
        // Paho mqtt clients are always shared
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
    }
}
