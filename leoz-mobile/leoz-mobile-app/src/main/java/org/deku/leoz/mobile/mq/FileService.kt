package org.deku.leoz.mobile.mq

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import org.deku.leoz.identity.Identity
import org.deku.leoz.service.internal.toFileFragmentMessages
import sx.mq.mqtt.MqttChannel
import java.io.InputStream
import java.util.*

/**
 * Extensions for sending file messages
 * Created by masc on 26.08.17.
 */

private val identity: Identity by Kodein.global.lazy.instance()

private val DEFAULT_CHUNK_SIZE = 100 * 1024

/**
 * Send in memory buffer as a file via mq
 */
fun MqttChannel.sendFile(content: ByteArray, mimeType: String, chunkSize: Int = DEFAULT_CHUNK_SIZE): UUID {
    return UUID.randomUUID().also {
        content.toFileFragmentMessages(
                nodeUid = identity.uid.value,
                fileUid = it,
                mimeType = mimeType,
                maxChunkSize = chunkSize
        ).forEach {
            this.send(it)
        }
    }
}

/**
 * Send input stream as a file via mq
 */
fun MqttChannel.sendFile(content: InputStream, mimeType: String, chunkSize: Int = DEFAULT_CHUNK_SIZE, totalSize: Int): UUID {
    return UUID.randomUUID().also {
        content.toFileFragmentMessages(
                nodeUid = identity.uid.value,
                fileUid = it,
                mimeType = mimeType,
                maxChunkSize = chunkSize,
                totalSize = totalSize
        ).forEach {
            this.send(it)
        }
    }
}