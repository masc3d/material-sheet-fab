package org.deku.leoz.mobile

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import org.deku.leoz.MimeType
import org.deku.leoz.bundle.BundleType
import org.deku.leoz.identity.Identity
import org.deku.leoz.mobile.mq.MqttEndpoints
import org.deku.leoz.mobile.mq.sendFile
import org.zeroturnaround.zip.ZipUtil
import sx.ProcessExecutor
import sx.mq.mqtt.channel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 * Application level diagnostics
 * Created by masc on 17.12.17.
 */
class Diagnostics() {
    private val database: Database by Kodein.global.lazy.instance()
    private val identity: Identity by Kodein.global.lazy.instance()
    private val mqttEndpoints: MqttEndpoints by Kodein.global.lazy.instance()
    private val storage: Storage by Kodein.global.lazy.instance()

    private val dateFormat by lazy { SimpleDateFormat("yyyyMMdd-HHmmss") }

    /**
     * Send diagnostics
     */
    fun send() {
        val diagName = "${BundleType.LEOZ_MOBILE.value}-${identity.shortUid}-${dateFormat.format(Date())}"

        val diagDir = this.storage.diagnosticsDir.resolve(diagName)

        try {
            diagDir.mkdirs()

            database.backup(
                    destinationFile = diagDir.resolve(database.name)
            )

            val diagZipFile = diagDir.parentFile.resolve(diagDir.name + MimeType.LEOZ_DIAGNOSTIC_ZIP.extension)

            try {
                ZipUtil.pack(
                        diagDir,
                        diagZipFile
                )

                diagZipFile.inputStream().buffered().use { diagZipStream ->
                    this.mqttEndpoints.central.main.channel().sendFile(
                            content = diagZipStream,
                            mimeType = MimeType.LEOZ_DIAGNOSTIC_ZIP.value,
                            totalSize = diagZipFile.length().toInt()
                    )
                }
            } finally {
                diagZipFile.delete()
            }


        } finally {
            diagDir.deleteRecursively()
        }
    }
}