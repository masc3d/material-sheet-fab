package sx.mq.mqtt

import sx.io.serialization.Serializer

/**
 * Mqtt endpoint
 * @param contextg Mqtt context
 * @param topicName Mqtt topic name
 * @param qos Mqtt QOS
 * Created by masc on 10.05.17.
 */
class MqttEndpoint(
        val context: MqttContext,
        val topicName: String,
        val qos: Int,
        val serializer: Serializer
)
