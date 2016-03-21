package org.deku.leoz.config.messaging

import org.deku.leoz.Identity
import sx.jms.Channel
import sx.jms.embedded.Broker

/**
 * Messaging configuration base interface
 * Created by masc on 07.06.15.
 */
interface MessagingConfiguration {
    /** Broker for this messaging context  */
    val broker: Broker

    /**
     * Common central queue
     */
    val centralQueue: Channel.Configuration

    /**
     * Central log queue
     */
    val centralLogQueue: Channel.Configuration

    /**
     * Entity sync queue
     */
    val entitySyncQueue: Channel.Configuration

    /**
     * Entity sync notification topic
     */
    val entitySyncTopic: Channel.Configuration

    /**
     * Node notification topic
     */
    val nodeNotificationTopic: Channel.Configuration

    /**
     * Node queue
     */
    fun nodeQueue(identityKey: Identity.Key): Channel.Configuration
}
