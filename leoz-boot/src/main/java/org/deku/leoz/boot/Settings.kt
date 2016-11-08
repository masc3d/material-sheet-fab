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

    @Parameter(names = arrayOf("--repository"), description = "Repository URI")
    var repositoryUriString: String? = null

    @Parameter(names = arrayOf("--no-ui"), description = "Don't show user interface")
    var hideUi: Boolean = false

    @Parameter(names = arrayOf("--force-download"), description = "Force download")
    var forceDownload: Boolean = false

    @Parameter(names = arrayOf("--version"), description = "Version pattern override")
    var versionPattern: String = "+RELEASE"

    @Parameter(names = arrayOf("--uninstall"), description = "Uninstall bundle")
    var uninstall: Boolean = false
}