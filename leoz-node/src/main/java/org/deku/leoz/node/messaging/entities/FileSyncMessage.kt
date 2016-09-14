package org.deku.leoz.node.messaging.entities

import sx.io.serialization.Serializable

/**
 * Notifies another node that files are available to be synchronized
 * Created by masc on 18/03/16.
 */
@Serializable(0x360b49d4873922)
class FileSyncMessage(
        var key: String = ""
)