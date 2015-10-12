package org.deku.leoz.messaging.activemq

import org.deku.leoz.messaging.MessagingContext
import sx.jms.embedded.Broker
import sx.jms.embedded.activemq.ActiveMQBroker

import javax.jms.Queue
import javax.jms.Topic

/**
 * Common messaging context shared across leoz applications
 * Created by masc on 16.04.15.
 */
class ActiveMQContext private constructor() : MessagingContext {
    companion object {
        val USERNAME = "leoz"
        val PASSWORD = "iUbmQRejRI1P3SNtzwIM7wAgNazURPcVcBU7SftyZ0oha9FlnAdGAmXdEQwYlKFC"
        val GROUPNAME = "leoz"

        /** Singleton instance  */
        @JvmStatic val instance by lazy({
            ActiveMQContext()
        })
    }

    init {
        // Configure broker authentication
        this.broker.user = Broker.User(USERNAME, PASSWORD, GROUPNAME)
    }

    override val broker: Broker
        get() = ActiveMQBroker.instance()

    override val centralQueue: Queue by lazy({
        this.broker.createQueue("leoz.central")
    })

    override val centralEntitySyncQueue: Queue by lazy({
        this.broker.createQueue("leoz.entity-sync")
    })

    override val centralLogQueue: Queue by lazy({
        this.broker.createQueue("leoz.log")
    })

    override fun getNodeQueue(id: Int?): Queue {
        return this.broker.createQueue("leoz.node." + id!!.toString())
    }

    override val nodeNotificationTopic: Topic by lazy({
        this.broker.createTopic("leoz.notifications")
    })
}
