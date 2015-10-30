package org.deku.leoz.config.messaging

import sx.jms.embedded.Broker
import sx.jms.embedded.activemq.ActiveMQBroker

import javax.jms.Queue
import javax.jms.Topic

/**
 * ActiveMQ specific messaging configuration
 * Created by masc on 16.04.15.
 */
class ActiveMQConfiguration private constructor() : MessagingConfiguration {

    companion object {
        val USERNAME = "leoz"
        val PASSWORD = "iUbmQRejRI1P3SNtzwIM7wAgNazURPcVcBU7SftyZ0oha9FlnAdGAmXdEQwYlKFC"
        val GROUPNAME = "leoz"

        /** Singleton instance  */
        @JvmStatic val instance by lazy({
            ActiveMQConfiguration()
        })
    }

    init {
        // Configure broker authentication
        this.broker.user = Broker.User(USERNAME, PASSWORD, GROUPNAME)
    }

    override val broker: Broker
        get() = ActiveMQBroker.instance()

    override val centralQueue: Queue by lazy({
        this.broker.createQueue("leoz.central.queue")
    })

    override val centralEntitySyncQueue: Queue by lazy({
        this.broker.createQueue("leoz.entity-sync.queue")
    })

    override val nodeEntitySyncTopic: Topic by lazy({
        this.broker.createTopic("leoz.entity-sync.topic")
    })

    override val centralLogQueue: Queue by lazy({
        this.broker.createQueue("leoz.log.queue")
    })

    override fun nodeQueue(id: Int): Queue {
        return this.broker.createQueue("leoz.node.queue." + id.toString())
    }

    override val nodeNotificationTopic: Topic by lazy({
        this.broker.createTopic("leoz.node.notification.topic")
    })
}
