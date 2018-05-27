package org.deku.leoz.service.entity

import sx.io.serialization.Serializable

/**
 * Base mq message class
 * Created by masc on 21.04.18.
 */
@Serializable(0x64f0666c30fde3)
abstract class Message {
    /** Sender node uid */
    var nodeUid: String? = null
}
