package sx.mq.mqtt

import io.reactivex.Observable
import org.eclipse.paho.client.mqttv3.*
import org.slf4j.LoggerFactory
import sx.LazyInstance
import sx.mq.mqtt.paho.MqttRxClient
import sx.time.Duration
import java.lang.UnsupportedOperationException
import java.util.concurrent.TimeUnit

/**
 * MQTT channel client
 * @property channel Mqtt channel
 * Created by masc on 07.05.17.
 */
class MqttClient(
        val channel: MqttChannel
) : sx.mq.MqClient {

    private val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        val DEFAULT_SEND_TIMEOUT = Duration.ofSeconds(5)
    }

    val mqttClient by lazy {
        MqttRxClient(
                this.channel.context.client())
    }

    override fun close() {
        // Paho mqtt clients are always shared
    }

    override fun <T> receive(messageType: Class<T>): T {
        throw UnsupportedOperationException("MQTT channel clients do not support receiving messages on demand")
    }

    override fun send(message: Any) {
        val mqttMessage = MqttMessage(
                // Payload
                this.channel.serializer.serializeToByteArray(message))

        mqttMessage.qos = this.channel.qos

        this.mqttClient
                .publish(this.channel.topicName, mqttMessage)
                .timeout(DEFAULT_SEND_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
                .blockingAwait()
    }
}
