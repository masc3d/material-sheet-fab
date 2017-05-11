package sx.mq.mqtt

import org.eclipse.paho.client.mqttv3.MqttClient
import sx.mq.Channel
import sx.io.serialization.Serializer

/**
 * Mqtt channel
 * @param contextg Mqtt context
 * @param topicName Mqtt topic name
 * @param qos Mqtt QOS
 * Created by masc on 10.05.17.
 */
class MqttChannel(
        val context: MqttContext,
        val topicName: String,
        val qos: Int,
        val serializer: Serializer
)
