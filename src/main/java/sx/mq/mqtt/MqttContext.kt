package sx.mq.mqtt

/**
 * Mqtt context
 * Created by masc on 10.05.17.
 */
class MqttContext(
        val client: () -> IMqttRxClient)

