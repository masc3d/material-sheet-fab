package org.deku.leoz.node.config

import org.apache.commons.logging.LogFactory
import org.deku.leoz.Identity
import org.deku.leoz.SystemInformation
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.deku.leoz.node.App
import org.deku.leoz.node.auth.Authorizer

/**
 * Leoz-node identity configuration
 * Responsible for setting up identity of the node and initiating remote authorization task(s)
 * Created by masc on 30.06.15.
 */
class IdentityConfiguration {
    private val log = LogFactory.getLog(this.javaClass)

    /** Authorizer */
    private var authorizer: Authorizer

    /**
     * Application wide Node identity
     * @retur
     */
    var identity: Identity
        private set

    /**
     * Application wide system information
     * @return
     */
    var systemInformation: SystemInformation
        private set

    companion object Singleton {
        @JvmStatic val instance by lazy {
            IdentityConfiguration()
        }
    }

    /**
     * Initialize
     */
    fun initialize() {
        // Actual initialization done by c'tor
    }

    /**
     * c'tor
     */
    init {
        var identity: Identity? = null

        // Collect system information
        val systemInformation = SystemInformation.create()
        log.info(systemInformation)

        // Verify and read existing identity file
        val identityFile = StorageConfiguration.instance.identityConfigurationFile
        if (identityFile.exists()) {
            try {
                identity = Identity.load(systemInformation, identityFile)
            } catch (e: Exception) {
                log.error(e.message, e)
            }

        }
        // Create identity if it doesn't exist or could not be read/parsed
        if (identity == null) {
            identity = Identity.create(App.instance.name, systemInformation)

            // Store updates/created identity
            try {
                identity.save(identityFile)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

        }
        log.info(identity)

        // Start authorizer
        authorizer = Authorizer(ActiveMQConfiguration.instance)
        authorizer.start(identity)

        this.systemInformation = systemInformation
        this.identity = identity
    }
}
/** c'tor  */
