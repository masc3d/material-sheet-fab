package org.deku.leoz.mobile.mq

import org.deku.leoz.config.MqEndpoints
import org.deku.leoz.identity.Identity
import sx.mq.mqtt.MqttContext
import sx.mq.mqtt.toMqtt

/**
 * MQTT channels
 * Created by masc on 12.05.17.
 * @param context Mqtt context
 * @param identityUid Identity uid (lazy supplier)
 */
class MqttEndpoints(
        private val context: MqttContext,
        private val identityUid: () -> Identity.Uid) {

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
        val broadcast by lazy {
            MqEndpoints.mobile.broadcast.toMqtt(
                    context = context,
                    qos = 1
            )
        }
    }

    val mobile = Mobile()

    inner class Node {
        val topic by lazy {
            MqEndpoints.node.topic(identityUid()).toMqtt(
                    context = context,
                    qos = 1
            )
        }
    }

    val node = Node()
}
