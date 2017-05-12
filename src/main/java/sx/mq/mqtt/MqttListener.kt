package sx.mq.mqtt

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient
import org.eclipse.paho.client.mqttv3.IMqttMessageListener
import sx.mq.MqListener

/**
 * Lightweight MQTT listener
 * @param mqttClient The MQTT client to use for subscription
 * @param mqttChannel The MQTT channel representing the topic to subscribe to
 * Created by masc on 11.05.17.
 */
class MqttListener(
        private val mqttClient: IMqttAsyncClient,
        private val mqttChannel: MqttChannel
)
    : MqListener() {
    private var isStarted: Boolean = false

    @Synchronized override fun start() {
        this.mqttClient.subscribe(
                this.mqttChannel.topicName,
                this.mqttChannel.qos,
                { topic, message ->
                    try {
                        this.handleMessage(
                                messageObject = this.mqttChannel.serializer.deserializeFrom(message.payload),
                                replyClient = null)
                    } catch(e: Throwable) {
                        this.onError(e)
                    }
                }
        ).waitForCompletion()

        this.isStarted = true
    }

    @Synchronized override fun stop() {
        if (this.isStarted) {
            this.mqttClient.unsubscribe(
                    this.mqttChannel.topicName
            ).waitForCompletion()

            this.isStarted = false
        }
    }
}