package org.deku.leoz.config

import org.deku.leoz.identity.Identity
import sx.io.serialization.JacksonSerializer
import sx.io.serialization.KryoSerializer
import sx.io.serialization.gzip
import sx.mq.DestinationType
import sx.mq.MqEndpoint

/**
 * Leoz mq endpoints
 */
object MqEndpoints {
    /**
     * Compound endpoint collection, exposing queues for protocols and referring virtual topics (for mqtt)
     */
    class QueueEndpoints(
            /** Destination base name */
            val destinationBaseName: String,
            val persistent: Boolean = false) {

        val kryo = MqEndpoint(
                destinationType = DestinationType.Queue,
                destinationName = "${destinationBaseName}.q.kryo",
                persistent = persistent,
                serializer = KryoSerializer().gzip
        )

        val json = MqEndpoint(
                destinationType = DestinationType.Queue,
                destinationName = "${destinationBaseName}.q.json",
                persistent = persistent,
                serializer = JacksonSerializer().gzip
        )

        inner class Mqtt() {
            val kryo = MqEndpoint(
                    destinationType = DestinationType.Topic,
                    destinationName = "${this@QueueEndpoints.kryo.destinationName}.mqtt",
                    persistent = false,
                    serializer = this@QueueEndpoints.kryo.serializer
            )

            val json = MqEndpoint(
                    destinationType = DestinationType.Topic,
                    destinationName = "${this@QueueEndpoints.json.destinationName}.mqtt",
                    persistent = false,
                    serializer = this@QueueEndpoints.json.serializer
            )
        }

        /**
         * Virtual topic endpoints for MQTT
         */
        val mqtt = Mqtt()
    }

    object central {
        /**
         * Central main queue
         */
        val main = QueueEndpoints(
                destinationBaseName = "leoz.central.main",
                persistent = true)

        /**
         * Central transient queue
         */
        val transient = QueueEndpoints(
                destinationBaseName = "leoz.central.transient",
                persistent = true
        )

        object entitySync {
            /**
             * Entity sync queue
             */
            val queue: MqEndpoint by lazy {
                MqEndpoint(
                        destinationType = DestinationType.Queue,
                        destinationName = "leoz.entity-sync.q.kryo",
                        serializer = KryoSerializer().gzip
                )
            }

            /**
             * Entity sync notification topic
             */
            val topic: MqEndpoint by lazy {
                MqEndpoint(
                        destinationType = DestinationType.Topic,
                        destinationName = "leoz.entity-sync.t.kryo",
                        serializer = KryoSerializer().gzip
                )
            }
        }
    }

    object node {
        fun queue(identityUid: Identity.Uid): MqEndpoint {
            return MqEndpoint(
                    destinationType = DestinationType.Queue,
                    destinationName = "leoz.node.queue.${identityUid.short}",
                    serializer = KryoSerializer().gzip
            )
        }

        val topic: MqEndpoint by lazy {
            MqEndpoint(
                    destinationType = DestinationType.Topic,
                    destinationName = "leoz.node.topic",
                    serializer = KryoSerializer().gzip
            )
        }
    }

    object mobile {
        val topic: MqEndpoint by lazy {
            MqEndpoint(
                    destinationType = DestinationType.Topic,
                    destinationName = "leoz.mobile.topic",
                    persistent = true,
                    serializer = JacksonSerializer().gzip
            )
        }
    }
}

