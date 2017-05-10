package sx.mq.mqtt

import sx.mq.Channel
import sx.mq.DestinationType
import sx.mq.jms.JmsChannel
import sx.mq.jms.JmsClient

/**
 * Create client for mqtt channel
 * Created by masc on 09.05.17.
 */
fun MqttChannel.client(): MqttClient {
    return MqttClient(this)
}

/**
 * Convert base channel to mqtt
 * @param qos Override qos. When omitted, conversion will  be as follows:
 *  persistence false -> qos(0)
 *  persistence true -> qus(2)
 */
fun Channel.toMqtt(context: MqttContext,
                   qos: Int? = null): MqttChannel {

    return MqttChannel(
            context = context,
            // Convert JMS destination name to mqtt topic name
            topicName = destinationName.replace('.', '/'),
            // Convert qos (if not overridden)
            qos = qos ?: when(this.persistent) {
                false -> 0
                true -> 2
            },
            serializer = this.serializer
    )
}