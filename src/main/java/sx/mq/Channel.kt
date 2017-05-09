package sx.mq

import sx.io.serialization.Serializer

/**
 * Abstract messaging channel
 *
 * This class is not base class of specific channels on purpose, as semantics for each messaging technology/protocol
 * are too specific to have a clean abstraction.
 *
 * This class can be used as a common base configuration for mq technologies, eg using jms backend and mqtt clients.
 * Instances shouls be explicitly transformed into specific api/protocol related channels (eg., JmsChannel)
 *
 * Created by masc on 07.05.17.
 */
class Channel(
        /** Destination name, following JMS specifications */
        val destinationName: String,
        val destinationType: DestinationType,
        val persistent: Boolean = false,
        val serializer: Serializer
)