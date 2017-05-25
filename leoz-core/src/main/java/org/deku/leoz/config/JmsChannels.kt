package org.deku.leoz.config

import org.deku.leoz.identity.Identity
import sx.mq.jms.JmsEndpoint
import sx.mq.jms.activemq.ActiveMQContext
import sx.mq.jms.toJms

/**
 * Created by masc on 24.05.17.
 */
object JmsChannels {
    /** Local JMS broker context */
    val context = ActiveMQContext(
            connectionFactory = JmsConfiguration.connectionFactory)

    class QueueChannels(
            val mqChannel: MqEndpoints.QueueEndpoints) {

        val kryo =  mqChannel.kryo.toJms(
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
        fun queue(identityKey: Identity.Key): JmsEndpoint {
            return MqEndpoints.node.queue(identityKey)
                    .toJms(context = context)
        }

        val topic: JmsEndpoint by lazy {
            MqEndpoints.node.topic.toJms(
                    context = context
            )
        }
    }

    object mobile {
        val topic by lazy {
            MqEndpoints.mobile.topic.toJms(
                    context = context
            )
        }
    }
}