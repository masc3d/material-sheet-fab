package org.deku.leoz.config

import sx.rsync.Rsync
import java.net.URI

/**
 * Rsync configuration (like usernames and passwords)
 * Created by masc on 15.09.15.
 */
object RsyncConfiguration {
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
    }
}