package org.deku.leoz.mobile.mq

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import org.deku.leoz.identity.Identity
import org.deku.leoz.service.internal.toFileFragmentMessages
import sx.mq.mqtt.MqttChannel
import java.util.*

/**
 * Some mime types, as adding guava just for this is pretty heavy-weight
 */
enum class MimeType(val value: String) {
    JPEG("image/jpeg")
}

/**
 * Extensions for sending file messages
 * Created by masc on 26.08.17.
 */

private val identity: Identity by Kodein.global.lazy.instance()
private val mqttEndPoints: MqttEndpoints by Kodein.global.lazy.instance()

/**
 * Send a file via mq
 */
fun MqttChannel.sendFile(data: ByteArray, mimeType: String): UUID {
    return UUID.randomUUID().also {
        data.toFileFragmentMessages(
                nodeUid = identity.uid.value,
                fileUid = it,
                mimeType = mimeType,
                maxChunkSize = 100 * 1024
        ).forEach {
            this.send(it)
        }
    }
}