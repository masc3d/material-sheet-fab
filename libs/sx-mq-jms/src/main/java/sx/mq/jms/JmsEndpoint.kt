package sx.mq.jms

import org.threeten.bp.Duration
import javax.jms.Destination

/**
 * JMS channel endpoint
 * All common channel configuration settings are grouped into this shallow structure which
 * can be easily (and automatically) replicated
 * @param context JMS context
 * @param sessionTransacted Sessions created through connection factory should be transacted
 * @param destination Destination
 * @param converter Message converter
 * @param deliveryMode JMS delivery mode
 */
class JmsEndpoint @JvmOverloads constructor(val context: JmsContext?,
                                            sessionTransacted: Boolean = true,
                                            destination: Destination,
                                            val converter: JmsConverter,
                                            deliveryMode: JmsChannel.DeliveryMode = JmsChannel.DeliveryMode.NonPersistent,
                                            /** Priority for messages sent through this channel */
                                           val priority: Int? = null,/** Time to live for messages sent through this channel */
                                           val ttl: Duration = JmsChannel.DEFAULT_JMS_TTL,
                                            /** Auto-commit messages on send, defaults to true */
                                           val autoCommit: Boolean = true,
                                            /** Default receive timeout for receive/sendReceive calls. Defaults to 10 seconds. */
                                           val receiveTimeout: Duration = JmsChannel.DEFAULT_RECEIVE_TIMEOUT)
                                           :
        Cloneable {

    var destination: Destination
        private set

    var deliveryMode: JmsChannel.DeliveryMode
        private set

    var sessionTransacted: Boolean
        private set

    init {
        this.destination = destination
        this.deliveryMode = deliveryMode
        this.sessionTransacted = sessionTransacted
    }

    /**
     * Clone channel configuration, optionally overriding properties
     * @param sessionTransacted Override session transaction mode
     * @param destination Override destination
     * @param deliveryMode Override delivery mode
     * @return Cloned configuration
     */
    fun clone(
            sessionTransacted: Boolean? = null,
            destination: Destination? = null,
            deliveryMode: JmsChannel.DeliveryMode? = null
    ): JmsEndpoint {

        // Clone configuration field by field
        val newChannel = this.clone() as JmsEndpoint
        // Override settings
        if (sessionTransacted != null)
            newChannel.sessionTransacted = sessionTransacted
        if (destination != null)
            newChannel.destination = destination
        if (deliveryMode != null)
            newChannel.deliveryMode = deliveryMode

        return newChannel
    }

    override fun toString(): String {
        return "Destination [${this.destination}]"
    }
}
