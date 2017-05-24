package org.deku.leoz.config

import org.deku.leoz.identity.Identity
import sx.Disposable
import sx.io.serialization.JacksonSerializer
import sx.io.serialization.KryoSerializer
import sx.io.serialization.gzip
import sx.mq.MqChannel
import sx.mq.DestinationType

/**
 * Generic MQ configuration
 * Created by masc on 08.05.17.
 */
object MqConfiguration {
    // Leoz broker configuration only has a single user which is defined here
    val USERNAME = "leoz"
    val PASSWORD = "iUbmQRejRI1P3SNtzwIM7wAgNazURPcVcBU7SftyZ0oha9FlnAdGAmXdEQwYlKFC"
    val GROUPNAME = "leoz"
}