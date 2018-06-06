package org.deku.leoz.config

import org.slf4j.LoggerFactory
import sx.platform.OperatingSystem
import sx.platform.PlatformId
import sx.rsync.Rsync
import java.io.File
import java.net.URI

/**
 * Rsync configuration (like usernames and passwords)
 * Created by masc on 15.09.15.
 */
open class RsyncConfiguration {
    companion object {
        private val log = LoggerFactory.getLogger(RsyncConfiguration::class.java)

        init {
            Rsync.executable.baseFilename = "leoz-rsync"
        }

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

        fun createRsyncUri(hostName: String, port: Int = DEFAULT_PORT, moduleName: String): Rsync.URI {
            val uri = URI("rsync", USERNAME, hostName, port, "/${moduleName}", null, null)
            return Rsync.URI(uri)
        }
    }

    /**
     * Rsync module names
     */
    object ModuleNames {
        val BUNDLES = "bundles"
        val TRANSFER = "transfer"
    }
}