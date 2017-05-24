package org.deku.leoz.config

import org.deku.leoz.identity.Identity
import sx.mq.jms.JmsChannel
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
            val mqChannel: MqChannels.QueueChannels) {

        val kryo =  mqChannel.kryo.toJms(
                context = context
        )

        val json = mqChannel.json.toJms(
                context = context
        )
    }

    object central {
        val main = QueueChannels(
                MqChannels.central.main
        )

        val transient = QueueChannels(
                MqChannels.central.transient
        )

        object entitySync {
            val queue: JmsChannel by lazy {
                MqChannels.central.entitySync.queue.toJms(
                        context = context
                )
            }

            val topic: JmsChannel by lazy {
                MqChannels.central.entitySync.topic.toJms(
                        context = context
                )
            }
        }
    }

    object node {
        fun queue(identityKey: Identity.Key): JmsChannel {
            return MqChannels.node.queue(identityKey)
                    .toJms(context = context)
        }

        val topic: JmsChannel by lazy {
            MqChannels.node.topic.toJms(
                    context = context
            )
        }
    }

    object mobile {
        val topic by lazy {
            MqChannels.mobile.topic.toJms(
                    context = context
            )
        }
    }
}