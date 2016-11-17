package org.deku.leoz.boot

import com.beust.jcommander.Parameter
import java.util.*

/**
 * Created by masc on 08/11/2016.
 */
class Settings {
    @Parameter(description = "Command args")
    var args: List<String> = ArrayList()

    @Parameter(names = arrayOf("--bundle"), description = "Bundle to boot")
    var bundle: String = ""

    @Parameter(names = arrayOf("--no-ui"), description = "Don't show user interface")
    var hideUi: Boolean = false

    @Parameter(names = arrayOf("--discover-local-node"), description = "Attempts to discover a local node instead of directly connecting to central.")
    var discoverLocalNode: Boolean = false

    @Parameter(names = arrayOf("--force-download"), description = "Force download")
    var forceDownload: Boolean = false

    @Parameter(names = arrayOf("--version-alias"), description = "Version alias. Defaults to 'release'")
    var versionAlias: String = "release"

    @Parameter(names = arrayOf("--uninstall"), description = "Uninstall bundle")
    var uninstall: Boolean = false

    // Overrides, mainly used for testing
    @Parameter(names = arrayOf("--version-pattern"), description = "Version pattern override")
    var versionPattern: String? = null

    @Parameter(names = arrayOf("--http-host"), description = "Http(s) host to connect to for service calls")
    var httpHost: String? = null

    @Parameter(names = arrayOf("--https"), description = "Use https for web/rest connections")
    var https: Boolean? = null

    @Parameter(names = arrayOf("--rsync-host"), description = "Rsync host to connect to for retrieving bundles")
    var rsyncHost: String? = null
}