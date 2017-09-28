package org.deku.leoz.mobile.mq

import sx.mq.mqtt.MqttListener

/**
 * MQTT listeners
 * Created by masc on 12.05.17.
 */
class MqttListeners(
        private val endpoints: MqttEndpoints) {
    inner class Mobile {
        /**
         * Listener for mobile topic channel
         */
        val topic: MqttListener by lazy {
            MqttListener(
                    mqttEndpoint = endpoints.mobile.topic)
        }
    }

    val mobile = Mobile()
}