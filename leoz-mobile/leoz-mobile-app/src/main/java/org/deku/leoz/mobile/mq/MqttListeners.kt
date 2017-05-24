package org.deku.leoz.mobile.mq

import sx.mq.mqtt.MqttListener

/**
 * MQTT listeners
 * Created by masc on 12.05.17.
 */
class MqttListeners(
        private val channels: MqttChannels)
{
    inner class Mobile {
        /**
         * Listener for mobile topic channel
         */
        val topic: MqttListener by lazy {
            MqttListener(
                    mqttEndpoint = channels.mobile.topic)
        }
    }
    val mobile = Mobile()
}