package org.deku.leoz.config

import sx.platform.OperatingSystem
import sx.platform.PlatformId
import sx.rsync.Rsync
import java.io.File
import java.net.URI

/**
 * Rsync configuration (like usernames and passwords)
 * Created by masc on 15.09.15.
 */
object RsyncConfiguration {
    /**
     * Rsync default port
     */
    val DEFAULT_PORT = 13002

    /**
     * Default leoz rsync user
     */
    val USERNAME: String = "leoz"
    /**
     * Rsync password used by all leoz instances
     */
    val PASSWORD: String = "2FBVQsfQqZOgpbSSipdZuatQCuaogyfYc9noFYRZO6gz3TwGRDLDiGXkRJ70yw5x"

    object ModuleNames {
        val BUNDLES = "bundles"
        val TRANSFER = "transfer"
    }

    fun createRsyncUri(hostName: String, port: Int? = null, moduleName: String): Rsync.URI {
        val uri = URI("rsync", USERNAME, hostName, port ?: -1, "/${moduleName}", null, null)
        return Rsync.URI(uri)
    }

    fun initialize() {
        Rsync.executable.baseFilename = "leoz-rsync"

        try {
            Rsync.executable.file
        } catch(e: IllegalStateException) {
            if (PlatformId.current().operatingSystem == OperatingSystem.LINUX) {
                val nixRsyncFile = File("/usr/bin/rsync")
                if (!nixRsyncFile.exists())
                    throw IllegalStateException("Could not fallback to system `rsync` executable", e)

                Rsync.executable.file = nixRsyncFile
            }
        }
    }
}