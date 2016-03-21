package org.deku.leoz.node.messaging.entities

import java.io.Serializable

/**
 * Notifies another node that files are available to be synchronized
 * Created by masc on 18/03/16.
 */
class FileSyncMessage(
        var key: String = ""
) : Serializable {
    private val serialVersionUID = 2805490723264764563L

}