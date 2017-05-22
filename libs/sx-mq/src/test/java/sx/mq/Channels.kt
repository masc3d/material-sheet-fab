package sx.mq

import sx.io.serialization.KryoSerializer
import sx.io.serialization.gzip

/**
 * Created by masc on 22.05.17.
 */
object Channels {
    /** Topic channel for testing notifications */
    val testTopic by lazy {
        MqChannel(
                destinationName = "test.topic",
                destinationType = DestinationType.Topic,
                persistent = true,
                serializer = KryoSerializer().gzip
        )
    }

    /** Queue channel for testing queues with topic forwarding via mqtt */
    val testQueue by lazy {
        MqChannel(
                destinationName = "test.queue",
                destinationType = DestinationType.Queue,
                persistent = true,
                serializer = KryoSerializer().gzip
        )
    }

    /** Virtual topic used for mqtt clients to post to queue */
    val testQueueForwarder by lazy {
        MqChannel(
                destinationName = "test.queue.topic",
                destinationType = DestinationType.Topic,
                persistent = true,
                serializer = KryoSerializer().gzip
        )
    }
}