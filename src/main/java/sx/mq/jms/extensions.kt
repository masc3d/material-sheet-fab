package sx.mq.jms

import sx.mq.Channel
import sx.mq.jms.converters.DefaultJmsConverter
import sx.time.Duration

/**
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
fun Channel.toJms(
        context: JmsContext,
        sessionTransacted: Boolean = true,
        priority: Int? = null,
        ttl: Duration = JmsClient.DEFAULT_JMS_TTL,
        autoCommit: Boolean = true,
        receiveTimeout: Duration = JmsClient.DEFAULT_RECEIVE_TIMEOUT): JmsChannel {

    return JmsChannel(
            context = context,
            destination = context.createDestination(this),
            converter = DefaultJmsConverter(this.serializer),
            sessionTransacted = sessionTransacted,
            priority = priority,
            ttl = ttl,
            autoCommit = autoCommit,
            receiveTimeout = receiveTimeout
    )
}
