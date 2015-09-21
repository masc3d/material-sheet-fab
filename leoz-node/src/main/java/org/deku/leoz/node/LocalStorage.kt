package org.deku.leoz.node

import com.google.common.base.Strings
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import java.io.File
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.*
import java.util.*

/**
 * Local Storage
 * Created by masc on 26.06.15.
 */
object LocalStorage : org.deku.leoz.LocalStorage() {
    private var log: Log = LogFactory.getLog(this.javaClass)

    // Directories
    /** Local embedded activemq data directory */
    val activeMqDataDirectory: File by lazy({
        File(this.dataDirectory, "activemq")
    })

    // Files
    /** Local application configuration file */
    val applicationConfigurationFile: File
    /** Local identity configuration file */
    val identityConfigurationFile: File by lazy({
        File(this.dataDirectory, "identity.properties")
    })

    /** c'tor */
    init {
        this.applicationConfigurationFile = File(this.baseDirectory, "leoz.properties")
    }
}
