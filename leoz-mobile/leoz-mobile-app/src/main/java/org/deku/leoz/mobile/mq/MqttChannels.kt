package org.deku.leoz.mobile.mq

import org.deku.leoz.config.MqConfiguration
import sx.mq.mqtt.MqttContext
import sx.mq.mqtt.toMqtt

/**
 * MQTT channels
 * Created by masc on 12.05.17.
 */
class MqttChannels(
        private val context: MqttContext) {

    inner class Central {
        val mainQueue by lazy {
            MqConfiguration.central.mainQueueMqtt.toMqtt(
                    context = context,
                    qos = 2
            )
        }

        val logQueue by lazy {
            MqConfiguration.central.logQueueMqtt.toMqtt(
                    context = context,
                    qos = 1
            )
        }
    }
    val central = Central()

    inner class Mobile {
        val topic by lazy {
            MqConfiguration.mobile.topic.toMqtt(
                    context = context,
                    qos = 1
            )
        }
    }
    val mobile = Mobile()
}
