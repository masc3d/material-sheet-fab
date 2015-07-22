package org.deku.leo2.node

import com.google.common.base.Strings
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import sx.LazyInstance

import java.io.File
import java.util.*
import kotlin.platform.platformStatic

/**
 * Local Storage
 * Created by masc on 26.06.15.
 */
class LocalStorage {
    private var log: Log = LogFactory.getLog(this.javaClass)

    companion object Singleton {
        private val instance: LocalStorage = LocalStorage()
        @platformStatic fun instance() : LocalStorage {
            return this.instance;
        }
    }

    /** c'tor */
    private constructor () {
        // Initialize directories
        var basePath: String
        var baseDirectory: String
        if (SystemUtils.IS_OS_WINDOWS) {
            basePath = System.getenv("ALLUSERSPROFILE")
            baseDirectory = "LeoZ"
        } else {
            basePath = System.getProperty("user.home")
            baseDirectory = ".leoZ"
        }
        if (Strings.isNullOrEmpty(basePath))
            throw UnsupportedOperationException("Basepath is empty");

        this.homeDirectory = File(basePath, baseDirectory)
        this.log.info("Home directory [${homeDirectory}]")
        this.logDirectory = File(this.homeDirectory, "log");
        this.dataDirectory = File(this.homeDirectory, "data")

        this.applicationConfigurationFile = File(this.homeDirectory, "leo2.properties")
        this.identityConfigurationFile = File(this.dataDirectory, "identity.properties")
        this.logFile = File(this.logDirectory, "leo2.log")
        this.activeMqDataDirectory = File(this.dataDirectory, "activemq")

        this.homeDirectory.mkdirs()
        this.dataDirectory.mkdirs()
        this.logDirectory.mkdirs()
    }

    // Directories
    /** Local home directory */
    val homeDirectory: File;
    /** Local data directory */
    val dataDirectory: File
    /** Local log directory */
    val logDirectory: File
    /** Local embedded activemq data directory */
    val activeMqDataDirectory: File

    // Files
    /** Local application configuration file */
    val applicationConfigurationFile: File
    /** Local identity configuration file */
    val identityConfigurationFile: File
    /** Local log file */
    val logFile: File
}
