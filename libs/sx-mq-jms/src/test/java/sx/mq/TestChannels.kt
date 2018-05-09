package sx.mq

import sx.io.serialization.KryoSerializer
import sx.io.serialization.gzip

/**
 * Created by masc on 22.05.17.
 */
object TestChannels {
    /** Topic channel for testing notifications */
    val testTopic by lazy {
        MqEndpoint(
                destinationName = "test.topic",
                destinationType = DestinationType.Topic,
                persistent = true,
                serializer = KryoSerializer().gzip
        )
    }

    /** Queue channel for testing queues with topic forwarding via mqtt */
    val testQueue by lazy {
        MqEndpoint(
                destinationName = "test.queue",
                destinationType = DestinationType.Queue,
                persistent = true,
                serializer = KryoSerializer().gzip
        )
    }

    /** Queue channel for testing queues with topic forwarding via mqtt */
    val testQueue2 by lazy {
        MqEndpoint(
                destinationName = "test.queue2",
                destinationType = DestinationType.Queue,
                persistent = true,
                serializer = KryoSerializer().gzip
        )
    }

    /** Virtual topic used for mqtt clients to post to queue */
    val testQueueForwarder by lazy {
        MqEndpoint(
                destinationName = "test.queue.topic",
                destinationType = DestinationType.Topic,
                persistent = true,
                serializer = KryoSerializer().gzip
        )
    }
}