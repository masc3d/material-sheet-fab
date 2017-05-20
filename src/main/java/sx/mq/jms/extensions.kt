package sx.mq.jms

import sx.mq.MqChannel
import sx.mq.jms.converters.DefaultJmsConverter
import sx.time.Duration

/**
 * Create client for jms channel
 * Created by masc on 09.05.17.
 */
fun JmsChannel.client(): JmsClient {
    return JmsClient(this)
}

/**
 * @param priority Priority for messages sent through this channel
 * @param ttl Time to live for messages sent through this channel
 * @param autoCommit Auto-commit messages on send, defaults to true
 * @param receiveTimeout Default receive timeout for receive/sendReceive calls. Defaults to 10 seconds
 */
fun MqChannel.toJms(
        context: JmsContext,
        sessionTransacted: Boolean = true,
        priority: Int? = null,
        ttl: Duration = JmsClient.DEFAULT_JMS_TTL,
        autoCommit: Boolean = true,
        receiveTimeout: Duration = JmsClient.DEFAULT_RECEIVE_TIMEOUT): JmsChannel {

    return JmsChannel(
            context = context,
            sessionTransacted = sessionTransacted,
            deliveryMode = if (this.persistent)
                JmsClient.DeliveryMode.Persistent
            else
                JmsClient.DeliveryMode.NonPersistent,
            destination = context.createDestination(this),
            converter = DefaultJmsConverter(this.serializer),
            priority = priority,
            ttl = ttl,
            autoCommit = autoCommit,
            receiveTimeout = receiveTimeout
    )
}
