package org.deku.leoz.config

import org.deku.leoz.identity.Identity
import org.threeten.bp.Duration
import sx.mq.jms.JmsEndpoint
import sx.mq.jms.activemq.ActiveMQContext
import sx.mq.jms.toJms

/**
 * Created by masc on 24.05.17.
 */
object JmsEndpoints {
    /** Local JMS broker context */
    val context = ActiveMQContext(
            connectionFactory = JmsConfiguration.connectionFactory)

    class QueueChannels(
            val mqChannel: MqEndpoints.QueueEndpoints) {

        val kryo = mqChannel.kryo.toJms(
                context = context
        )

        val json = mqChannel.json.toJms(
                context = context
        )
    }

    object central {
        val main = QueueChannels(
                MqEndpoints.central.main
        )

        val transient = QueueChannels(
                MqEndpoints.central.transient
        )

        object entitySync {
            val queue: JmsEndpoint by lazy {
                MqEndpoints.central.entitySync.queue.toJms(
                        context = context
                )
            }

            val topic: JmsEndpoint by lazy {
                MqEndpoints.central.entitySync.topic.toJms(
                        context = context
                )
            }
        }
    }

    object node {
        /**
         * Node main queue
         */
        fun main(identityUid: Identity.Uid) =
                QueueChannels(
                        MqEndpoints.node.main(identityUid)
                )

        /**
         * Node transient queue
         */
        fun transient(identityUid: Identity.Uid) =
                QueueChannels(
                        MqEndpoints.node.transient(identityUid)
                )

        @Deprecated("Superseded by main / transient queue groups")
        fun queue(identityUid: Identity.Uid): JmsEndpoint {
            return MqEndpoints.node.queue(identityUid)
                    .toJms(context = context)
        }

        fun topic(identityUid: Identity.Uid): JmsEndpoint {
            return MqEndpoints.node.topic(identityUid)
                    .toJms(
                            context = context,
                            // As nodes may vanish, messages need to have a reasonable TTL
                            ttl = Duration.ofDays(1)
                    )
        }

        val broadcast: JmsEndpoint by lazy {
            MqEndpoints.node.broadcast.toJms(
                    context = context
            )
        }
    }

    object mobile {
        val broadcast by lazy {
            MqEndpoints.mobile.broadcast.toJms(
                    context = context
            )
        }
    }
}