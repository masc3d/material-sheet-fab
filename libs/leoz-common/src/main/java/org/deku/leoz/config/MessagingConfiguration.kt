package org.deku.leoz.config

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
    /** Central message queue for entity sync  */
    val centralEntitySyncQueue: Queue
    /** Central message queue for logs  */
    val centralLogQueue: Queue

    /** Node message queue for lightweight message exchange */
    fun nodeQueue(id: Int): Queue

    /** Node notification topic for broadcastss  */
    val nodeNotificationTopic: Topic
}
