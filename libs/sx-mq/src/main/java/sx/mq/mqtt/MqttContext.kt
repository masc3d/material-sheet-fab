package sx.mq.mqtt

import org.eclipse.paho.client.mqttv3.MqttConnectOptions

/**
 * Mqtt context
 * Created by masc on 10.05.17.
 */
class MqttContext(
        val client: () -> org.eclipse.paho.client.mqttv3.IMqttAsyncClient,
        val connectOptions: MqttConnectOptions? = null)
