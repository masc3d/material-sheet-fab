package sx.mq.mqtt

import io.reactivex.rxkotlin.subscribeBy
import sx.mq.MqListener

/**
 * Lightweight MQTT listener
 * @param mqttClient The MQTT client to use for subscription
 * @param mqttChannel The MQTT channel representing the topic to subscribe to
 * Created by masc on 11.05.17.
 */
class MqttListener(
        private val mqttChannel: MqttChannel
)
    : MqListener() {
    private var isStarted: Boolean = false

    private val mqttClient by lazy {
        this.mqttChannel.context.client()
    }

    @Synchronized override fun start() {
        this.mqttClient.subscribe(
                this.mqttChannel.topicName,
                this.mqttChannel.qos)
                .subscribeBy(
                        onNext = {
                            try {
                                this.handleMessage(
                                        messageObject = this.mqttChannel.serializer.deserializeFrom(it.payload),
                                        replyClient = null)
                            } catch(e: Throwable) {
                                this.onError(e)
                            }
                        },
                        onError = {
                            this.onError(it)
                        })

        this.isStarted = true
    }

    @Synchronized override fun stop() {
        if (this.isStarted) {
            this.mqttClient.unsubscribe(this.mqttChannel.topicName)
                    .blockingGet()

            this.isStarted = false
        }
    }
}