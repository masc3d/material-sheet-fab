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
class LocalStorage : org.deku.leoz.LocalStorage() {
    private var log: Log = LogFactory.getLog(this.javaClass)

    // Directories
    /** Local embedded activemq data directory */
    val activeMqDataDirectory: File

    // Files
    /** Local application configuration file */
    val applicationConfigurationFile: File
    /** Local identity configuration file */
    val identityConfigurationFile: File

    companion object Singleton {
        private val instance: LocalStorage = LocalStorage()
        @JvmStatic fun instance(): LocalStorage {
            return this.instance;
        }
    }

    /** c'tor */
    init {
        this.applicationConfigurationFile = File(this.homeDirectory, "leoz.properties")
        this.identityConfigurationFile = File(this.dataDirectory, "identity.properties")
        this.activeMqDataDirectory = File(this.dataDirectory, "activemq")
    }
}
