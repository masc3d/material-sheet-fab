package org.deku.leoz.config

import org.deku.leoz.identity.Identity
import sx.io.serialization.JacksonSerializer
import sx.io.serialization.KryoSerializer
import sx.io.serialization.gzip
import sx.mq.DestinationType
import sx.mq.MqChannel

/**
 * Leoz channel templates
 */
object MqChannels {
    /**
     * Compound channel collection, exposing queues for protocols and referring virtual topics (for mqtt)
     */
    class QueueChannels(
            /** Destination base name */
            val destinationBaseName: String,
            val persistent: Boolean = false) {

        val kryo = MqChannel(
                destinationType = DestinationType.Queue,
                destinationName = "${destinationBaseName}.q.kryo",
                persistent = persistent,
                serializer = KryoSerializer().gzip
        )

        val json = MqChannel(
                destinationType = DestinationType.Queue,
                destinationName = "${destinationBaseName}.q.json",
                persistent = persistent,
                serializer = JacksonSerializer().gzip
        )

        inner class Mqtt() {
            val kryo = MqChannel(
                    destinationType = DestinationType.Topic,
                    destinationName = "${this@QueueChannels.kryo.destinationName}.mqtt",
                    persistent = false,
                    serializer = this@QueueChannels.kryo.serializer
            )

            val json = MqChannel(
                    destinationType = DestinationType.Topic,
                    destinationName = "${this@QueueChannels.json.destinationName}.mqtt",
                    persistent = false,
                    serializer = this@QueueChannels.json.serializer
            )
        }

        /**
         * Virtual topic channels for MQTT
         */
        val mqtt = Mqtt()
    }

    object central {
        /**
         * Central main queue
         */
        val main = QueueChannels(
                destinationBaseName = "leoz.central.main",
                persistent = true)

        /**
         * Central transient queue
         */
        val transient = QueueChannels(
                destinationBaseName = "leoz.central.transient",
                persistent = true
        )

        object entitySync {
            /**
             * Entity sync queue
             */
            val queue: MqChannel by lazy {
                MqChannel(
                        destinationType = DestinationType.Queue,
                        destinationName = "leoz.entity-sync.q.kryo",
                        serializer = KryoSerializer().gzip
                )
            }

            /**
             * Entity sync notification topic
             */
            val topic: MqChannel by lazy {
                MqChannel(
                        destinationType = DestinationType.Topic,
                        destinationName = "leoz.entity-sync.t.kryo",
                        serializer = KryoSerializer().gzip
                )
            }
        }
    }

    object node {
        fun queue(identityKey: Identity.Key): MqChannel {
            return MqChannel(
                    destinationType = DestinationType.Queue,
                    destinationName = "leoz.node.queue.${identityKey.short}",
                    serializer = KryoSerializer().gzip
            )
        }

        val topic: MqChannel by lazy {
            MqChannel(
                    destinationType = DestinationType.Topic,
                    destinationName = "leoz.node.topic",
                    serializer = KryoSerializer().gzip
            )
        }
    }

    object mobile {
        val topic: MqChannel by lazy {
            MqChannel(
                    destinationType = DestinationType.Topic,
                    destinationName = "leoz.mobile.topic",
                    persistent = true,
                    serializer = JacksonSerializer().gzip
            )
        }
    }
}

