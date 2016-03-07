package org.deku.leoz.config.messaging

import sx.jms.Channel
import sx.jms.embedded.Broker

/**
 * Messaging configuration base interface
 * Created by masc on 07.06.15.
 */
interface MessagingConfiguration {
    /** Broker for this messaging context  */
    val broker: Broker

    fun centralQueueChannel(): Channel

    fun centralLogChannel(): Channel

    fun centralEntitySyncChannel(): Channel

    fun nodeEntitySyncBroadcastChannel(): Channel
}
