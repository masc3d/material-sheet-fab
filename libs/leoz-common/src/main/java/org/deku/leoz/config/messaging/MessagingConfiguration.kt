package org.deku.leoz.config.messaging

import sx.jms.embedded.Broker

import javax.jms.Queue
import javax.jms.Topic

/**
 * Messaging configuration base interface
 * Created by masc on 07.06.15.
 */
interface MessagingConfiguration {
    /** Broker for this messaging context  */
    val broker: Broker

    /** Central message queue for lightweight message exchange  */
    val centralQueue: Queue

    /** Central message queue for logs  */
    val centralLogQueue: Queue

    /** Central message queue for entity sync  */
    val centralEntitySyncQueue: Queue
    /** Node topic for entity sync broadcastss  */
    val nodeEntitySyncTopic: Topic

    /** Queue for sending messages to a specific node */
    fun nodeQueue(id: Int): Queue

    /** Topci for notifications for all nodes */
    val nodeNotificationTopic: Topic
}
