package org.deku.leoz.node.config

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.deku.leoz.config.ActiveMQConfiguration
import org.deku.leoz.node.App
import org.deku.leoz.node.config.StorageConfiguration
import org.deku.leoz.SystemInformation
import org.deku.leoz.node.auth.Authorizer
import org.deku.leoz.Identity
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.LazyInstance

import javax.annotation.PostConstruct
import java.io.File
import kotlin.properties.Delegates

/**
 * Leoz-node identity configuration
 * Responsible for setting up identity of the node and initiating remote authorization task(s)
 * Created by masc on 30.06.15.
 */
@Configuration
@Lazy(false)
open class IdentityConfiguration {
    private val log = LogFactory.getLog(this.javaClass)

    /** Authorizer  */
    private var authorizer: Authorizer by Delegates.notNull()
    /**
     * Application wide Node identity
     * @retur
     */
    var identity: Identity by Delegates.notNull()
        private set

    /**
     * Application wide system information
     * @return
     */
    var systemInformation: SystemInformation by Delegates.notNull()
        private set

    /**
     * Initialize identity
     */
    @PostConstruct
    fun initialize() {
        var identity: Identity? = null

        // Collect system information
        val systemInformation = SystemInformation.create()
        log.info(systemInformation)

        // Verify and read existing identity file
        val identityFile = StorageConfiguration.instance.identityConfigurationFile
        if (identityFile.exists()) {
            try {
                identity = Identity.createFromFile(systemInformation, identityFile)
            } catch (e: Exception) {
                log.error(e.getMessage(), e)
            }

        }
        // Create identity if it doesn't exist or could not be read/parsed
        if (identity == null) {
            identity = Identity.create(systemInformation)

            // Store updates/created identity
            try {
                identity!!.store(identityFile)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

        }
        log.info(identity)

        // Start authorizer (on client nodes only)
        if (App.instance().profile == App.PROFILE_CLIENT_NODE) {
            authorizer = Authorizer(ActiveMQConfiguration.instance)
            authorizer.start(identity)
        }

        this.systemInformation = systemInformation
        this.identity = identity!!
    }
}
/** c'tor  */
