package sx.mq.mqtt

import org.eclipse.paho.client.mqttv3.MqttMessage
import org.slf4j.LoggerFactory
import sx.time.Duration
import java.lang.UnsupportedOperationException
import java.util.concurrent.TimeUnit

/**
 * MQTT channel client
 * @property endpoint Mqtt channel
 * Created by masc on 07.05.17.
 */
class MqttChannel(
        val endpoint: MqttEndpoint
) : sx.mq.MqChannel {

    private val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        val DEFAULT_SEND_TIMEOUT = Duration.ofSeconds(5)
    }

    val mqttClient by lazy {
        this.endpoint.context.client()
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
                this.endpoint.serializer.serializeToByteArray(message))

        mqttMessage.qos = this.endpoint.qos

        this.mqttClient
                .publish(this.endpoint.topicName, mqttMessage)
                .timeout(DEFAULT_SEND_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
                .blockingAwait()
    }
}
