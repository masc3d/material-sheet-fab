package org.deku.leoz.node.config

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.deku.leoz.node.App
import sx.LazyInstance
import java.io.File

/**
 * Local Storage
 * Created by masc on 26.06.15.
 */
open class StorageConfiguration protected constructor(appName: String)
:
        org.deku.leoz.config.StorageConfiguration(appName)
{
    companion object {
        val injectableInstance = LazyInstance({ StorageConfiguration(App.instance.name) })
        val instance by lazy({ injectableInstance.get() })
    }

    private var log: Log = LogFactory.getLog(this.javaClass)

    // Directories
    /** Local embedded activemq data directory */
    val activeMqDataDirectory: File by lazy({
        File(this.dataDirectory, "activemq")
    })

    /** Bundle repository directory */
    val bundleRepositoryDirectory: File by lazy({
        val d = File(this.bundlesDirectory, "repository")
        d.mkdirs()
        d
    })

    val sshDataDirectory: File by lazy({
        val d = File(this.dataDirectory, "ssh")
        d.mkdirs()
        d
    })

    // Files
    /** Local application configuration file */
    val applicationConfigurationFile: File by lazy({
        File(this.etcDirectory, "${this.appName}.yml")
    })

    /** Local identity configuration file */
    val identityConfigurationFile: File by lazy({
        File(this.dataDirectory, "identity.properties")
    })

    /** Local h2 database file */
    val h2DatabaseFile: File by lazy({
        this.dataDirectory.toPath().resolve("h2").resolve("leoz").toFile()
    })
}
