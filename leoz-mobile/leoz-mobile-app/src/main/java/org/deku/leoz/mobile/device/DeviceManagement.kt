package org.deku.leoz.mobile.device

import android.util.Xml
import org.deku.leoz.identity.Identity
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileWriter

/**
 * Identity information for mobile device management
 * Created by masc on 12.09.17.
 * @property path Base directory for device management related files
 * @property identity Device identity
 */
class DeviceManagement(
        val path: File,
        val identity: Identity
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val identityFile = File(path, "data.xml")

    fun saveIdentityFile() {
        val TAG_ROOT = "mobilex"
        val TAG_IDENTITY = "identity"
        val TAG_DEVICE_ID_SHORT = "device-id-short"
        val TAG_DEVICE_ID = "device-id"

        log.trace("Writing identity file [${identityFile}]")

        FileWriter(identityFile).use { writer ->
            Xml.newSerializer().also {
                it.setOutput(writer)
                it.startDocument(Charsets.UTF_8.name(), null)
                it.startTag(null, TAG_ROOT)
                it.startTag(null, TAG_IDENTITY)
                it.startTag(null, TAG_DEVICE_ID_SHORT)
                it.text(identity.shortUid)
                it.endTag(null, TAG_DEVICE_ID_SHORT)
                it.startTag(null, TAG_DEVICE_ID)
                it.text(identity.uid.value)
                it.endTag(null, TAG_DEVICE_ID)
                it.endTag(null, TAG_IDENTITY)
                it.endTag(null, TAG_ROOT)
                it.endDocument()
            }
        }
    }
}