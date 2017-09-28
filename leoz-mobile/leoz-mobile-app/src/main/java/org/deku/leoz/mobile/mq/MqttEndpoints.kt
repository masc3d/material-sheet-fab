package org.deku.leoz.mobile.mq

import org.deku.leoz.config.MqEndpoints
import sx.mq.mqtt.MqttContext
import sx.mq.mqtt.toMqtt

/**
 * MQTT channels
 * Created by masc on 12.05.17.
 */
class MqttEndpoints(
        private val context: MqttContext) {

    inner class Central {
        val main by lazy {
            MqEndpoints.central.main.mqtt.kryo.toMqtt(
                    context = context,
                    qos = 2
            )
        }

        val transient by lazy {
            MqEndpoints.central.transient.mqtt.kryo.toMqtt(
                    context = context,
                    qos = 1
            )
        }
    }

    val central = Central()

    inner class Mobile {
        val topic by lazy {
            MqEndpoints.mobile.topic.toMqtt(
                    context = context,
                    qos = 1
            )
        }
    }

    val mobile = Mobile()
}
