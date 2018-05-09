package sx.mq.jms

import org.threeten.bp.Duration
import sx.mq.MqEndpoint
import sx.mq.jms.converters.DefaultJmsConverter

/**
 * Create client for jms channel
 * Created by masc on 09.05.17.
 */
fun JmsEndpoint.channel(): JmsChannel {
    return JmsChannel(this)
}

/**
 * @param priority Priority for messages sent through this channel
 * @param ttl Time to live for messages sent through this channel
 * @param autoCommit Auto-commit messages on send, defaults to true
 * @param receiveTimeout Default receive timeout for receive/sendReceive calls. Defaults to 10 seconds
 */
fun MqEndpoint.toJms(
        context: JmsContext,
        sessionTransacted: Boolean = true,
        priority: Int? = null,
        ttl: Duration = JmsChannel.DEFAULT_JMS_TTL,
        autoCommit: Boolean = true,
        receiveTimeout: Duration = JmsChannel.DEFAULT_RECEIVE_TIMEOUT): JmsEndpoint {

    return JmsEndpoint(
            context = context,
            sessionTransacted = sessionTransacted,
            deliveryMode = if (this.persistent)
                JmsChannel.DeliveryMode.Persistent
            else
                JmsChannel.DeliveryMode.NonPersistent,
            destination = context.createDestination(this),
            converter = DefaultJmsConverter(this.serializer),
            priority = priority,
            ttl = ttl,
            autoCommit = autoCommit,
            receiveTimeout = receiveTimeout
    )
}
