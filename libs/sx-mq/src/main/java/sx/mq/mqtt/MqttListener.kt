package sx.mq.mqtt

import io.reactivex.rxkotlin.subscribeBy
import sx.mq.MqListener

/**
 * Lightweight MQTT listener
 * @param mqttClient The MQTT client to use for subscription
 * @param mqttEndpoint The MQTT channel representing the topic to subscribe to
 * Created by masc on 11.05.17.
 */
class MqttListener(
        private val mqttEndpoint: MqttEndpoint
)
    : MqListener() {

    override var isRunning: Boolean = false

    private val mqttClient by lazy {
        this.mqttEndpoint.context.client()
    }

    @Synchronized override fun start() {
        this.mqttClient.subscribe(
                this.mqttEndpoint.topicName,
                this.mqttEndpoint.qos)
                .subscribeBy(
                        onNext = {
                            try {
                                this.handleMessage(
                                        messageObject = this.mqttEndpoint.serializer.deserializeFrom(it.payload),
                                        replyChannel = null)
                            } catch(e: Throwable) {
                                this.onError(e)
                            }
                        },
                        onError = {
                            this.onError(it)
                        })

        this.isRunning = true
    }

    @Synchronized override fun stop() {
        if (this.isRunning) {
            this.mqttClient.unsubscribe(this.mqttEndpoint.topicName)
                    .blockingAwait()

            this.isRunning = false
        }
    }
}