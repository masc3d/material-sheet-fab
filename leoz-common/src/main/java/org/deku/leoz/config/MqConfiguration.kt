package org.deku.leoz.config

import org.deku.leoz.identity.Identity
import sx.io.serialization.KryoSerializer
import sx.io.serialization.gzip
import sx.mq.MqChannel
import sx.mq.DestinationType

/**
 * Created by masc on 08.05.17.
 */
object MqConfiguration {
    // Leoz broker configuration only has a single user which is defined here
    val USERNAME = "leoz"
    val PASSWORD = "iUbmQRejRI1P3SNtzwIM7wAgNazURPcVcBU7SftyZ0oha9FlnAdGAmXdEQwYlKFC"
    val GROUPNAME = "leoz"

    /**
     * Central messaging queue for all messages that require central processing
     */
    val centralQueue: MqChannel by lazy {
        MqChannel(
                destinationType = DestinationType.Queue,
                destinationName = "leoz.central.queue",
                persistent = true,
                serializer = KryoSerializer().gzip
        )
    }

    /**
     * Central queue topic for mqtt
     */
    val centralQueueTopic: MqChannel by lazy {
        MqChannel(
                destinationType = DestinationType.Topic,
                destinationName = "${this.centralQueue.destinationName}.topic",
                persistent = false,
                serializer = this.centralQueue.serializer
        )
    }

    /**
     * Central log queue, receiving log messages from all instances
     */
    val centralLogQueue: MqChannel by lazy {
        MqChannel(
                destinationType = DestinationType.Queue,
                destinationName = "leoz.log.queue",
                persistent = true,
                serializer = KryoSerializer().gzip
        )
    }

    /**
     * Central log queue topic for mqtt
     */
    val centralLogQueueTopic: MqChannel by lazy {
        MqChannel(
                destinationType = DestinationType.Topic,
                destinationName = "${this.centralLogQueue.destinationName}.topic",
                persistent = false,
                serializer = this.centralLogQueue.serializer
        )
    }

    /**
     * Entity sync queue
     */
    val entitySyncQueue: MqChannel by lazy {
        MqChannel(
                destinationType = DestinationType.Queue,
                destinationName = "leoz.entity-sync.queue",
                serializer = KryoSerializer().gzip
        )
    }

    /**
     * Entity sync notification topic
     */
    val entitySyncTopic: MqChannel by lazy {
        MqChannel(
                destinationType = DestinationType.Topic,
                destinationName = "leoz.entity-sync.topic",
                serializer = KryoSerializer().gzip
        )
    }

    /**
     *
     */
    fun nodeQueue(identityKey: Identity.Key): MqChannel {
        return MqChannel(
                destinationType = DestinationType.Queue,
                destinationName = "leoz.node.queue.${identityKey.short}",
                serializer = KryoSerializer().gzip
        )
    }

    val nodeTopic: MqChannel by lazy {
        MqChannel(
                destinationType = DestinationType.Topic,
                destinationName = "leoz.node.topic",
                serializer = KryoSerializer().gzip
        )
    }
}