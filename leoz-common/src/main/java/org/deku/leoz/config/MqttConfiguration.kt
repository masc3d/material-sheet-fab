package org.deku.leoz.config

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import sx.mq.mqtt.MqttContext
import sx.mq.mqtt.toMqtt

/**
 * MQTT configuration
 * Created by n3 on 12.05.17.
 */
open class MqttConfiguration(
        private val clientSupplier: () -> IMqttAsyncClient) {

    private val context by lazy {
        MqttContext(client = clientSupplier)
    }

    val centralQueueTopic by lazy {
        MqConfiguration.centralQueueTopic.toMqtt(
                context = this.context,
                qos = 2
        )
    }

    val centralLogQueueTopic by lazy {
        MqConfiguration.centralLogQueueTopic.toMqtt(
                context = this.context,
                qos = 1
        )
    }

    val mobileTopic by lazy {
        MqConfiguration.mobileTopic.toMqtt(
                context = this.context
        )
    }
}