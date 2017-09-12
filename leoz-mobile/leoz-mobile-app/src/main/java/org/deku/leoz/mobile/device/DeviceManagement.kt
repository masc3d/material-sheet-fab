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

    private val deviceFile = File(path, "device.xml")

    /**
     * Save device information file for mobile device management
     */
    fun saveDeviceFile() {
        val TAG_ROOT = "device"
        val TAG_IDENTITY = "identity"
        val TAG_DEVICE_UID = "uid"
        val TAG_DEVICE_UID_SHORT = "short-uid"

        log.trace("Writing device information file [${deviceFile}]")

        FileWriter(deviceFile).use { writer ->
            Xml.newSerializer().also {
                it.setOutput(writer)
                it.startDocument(Charsets.UTF_8.name(), null)
                it.startTag(null, TAG_ROOT)
                it.startTag(null, TAG_IDENTITY)
                it.attribute(null, TAG_DEVICE_UID, identity.uid.value)
                it.attribute(null, TAG_DEVICE_UID_SHORT, identity.shortUid)
                it.endTag(null, TAG_IDENTITY)
                it.endTag(null, TAG_ROOT)
                it.endDocument()
            }
        }
    }
}