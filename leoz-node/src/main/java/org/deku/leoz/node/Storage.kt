package org.deku.leoz.node

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.eagerSingleton
import com.github.salomonbrys.kodein.instance
import org.deku.leoz.node.Application
import org.slf4j.LoggerFactory
import sx.LazyInstance
import java.io.File

/**
 * Local Storage
 * Created by masc on 26.06.15.
 */
open class Storage(appName: String)
:
        org.deku.leoz.Storage(appName = appName)
{
    private val log = LoggerFactory.getLogger(this.javaClass)

    // Directories
    /** Local embedded activemq data directory */
    val activeMqDataDirectory by lazy {
        File(this.dataDirectory, "activemq")
    }

    /** Bundle repository directory */
    val bundleRepositoryDirectory by lazy {
        val d = File(this.bundlesDirectory, "repository")
        d.mkdirs()
        d
    }

    val sshDataDirectory by lazy {
        val d = File(this.dataDirectory, "ssh")
        d.mkdirs()
        d
    }

    val transferDirectory by lazy {
        val d = File(this.publicDirectory, "transfer")
        d.mkdirs()
        d
    }

    val mobileDataDirectory by lazy {
        val d = File(this.publicDirectory, "mobile")
        d.mkdirs()
        d
    }

    val workTmpDataDirectory by lazy {
        val d = File(this.publicDirectory, "workTmp")
        d.mkdirs()
        d
    }

    // Files
    /** Local application configuration file */
    val applicationConfigurationFile by lazy {
        File(this.etcDirectory, "${this.appName}.yml")
    }

    /** Local identity configuration file */
    val identityConfigurationFile: File by lazy {
        File(this.dataDirectory, "identity.yml")
    }

    /** Local h2 database file */
    val h2DatabaseFile: File by lazy {
        this.dataDirectory.toPath().resolve("h2").resolve("leoz").toFile()
    }
}
