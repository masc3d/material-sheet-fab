package org.deku.leoz.node.config

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.deku.leoz.node.App
import java.io.File

/**
 * Local Storage
 * Created by masc on 26.06.15.
 */
class StorageConfiguration(appName: String) : org.deku.leoz.config.StorageConfiguration(appName) {
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

    /** Bundle repository directory */
    val bundleRepositoryDirectory: File by lazy({
        File(this.dataDirectory, "bundle-repository")
    })

    /** c'tor */
    init {
        this.applicationConfigurationFile = File(this.baseDirectory, "leoz.properties")
    }

    companion object {
        /** Java instance access. $INSTANCE doesn't seem to work with JPA annotation processor */
        @JvmStatic val instance = StorageConfiguration(App.instance().name)
    }
}
