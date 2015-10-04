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
class LocalStorage(appName: String) : org.deku.leoz.LocalStorage(appName) {
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
    /** Local h2 database file */
    val h2DatabaseFile: File by lazy({
        this.dataDirectory.toPath().resolve("h2").resolve("leoz").toFile()
    })

    /** c'tor */
    init {
        this.applicationConfigurationFile = File(this.baseDirectory, "leoz.properties")
    }

    companion object {
        /**
         * Java instance access. $INSTANCE doesn't seem to work with JPA annotation processor
         */
        @JvmStatic val instance = LocalStorage(App.instance().name)
    }
}
