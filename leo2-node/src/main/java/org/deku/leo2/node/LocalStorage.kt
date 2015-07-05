package org.deku.leo2.node

import sx.LazyInstance

import java.io.File
import java.util.*
import kotlin.platform.platformStatic

/**
 * Created by masc on 26.06.15.
 */
object LocalStorage {

    // Directories
    /** Local home directory */
    val homeDirectory: File = File(System.getProperty("user.home"), ".leo2")
    /** Local data directory */
    val dataDirectory: File = File(this.homeDirectory, "data")
    /** Local application configuration file */
    val mConfigurationDirectory: File = File(this.homeDirectory, "leo2.properties")

    // Files
    val applicationConfigurationFile: File = File(this.homeDirectory, "leo2.properties")
    /** Local identity configuration file */
    val identityConfigurationFile: File = File(this.dataDirectory, "identity.properties")
    /** Local log file */
    val logFile: File = File(this.homeDirectory, "leo2.log")
    /** Local embedded activemq data directory */
    val activeMqDataDirectory: File = File(this.dataDirectory, "activemq")

    /**
     * Initializes local storage, takes care directories exist etc.
     */
    fun initialize() {
        homeDirectory.mkdirs()
        dataDirectory.mkdirs()
    }

    @platformStatic fun instance() : LocalStorage {
        return LocalStorage
    }
}
