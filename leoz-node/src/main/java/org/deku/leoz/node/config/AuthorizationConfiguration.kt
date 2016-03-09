package org.deku.leoz.node.config

import org.apache.commons.logging.LogFactory
import org.deku.leoz.Identity
import org.deku.leoz.SystemInformation
import org.deku.leoz.bundle.boot
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.deku.leoz.node.App
import org.deku.leoz.node.auth.Authorizer

/**
 * Leoz-node authorization configuration
 * Responsible for setting up identity of the node and initiating remote authorization task(s)
 * Created by masc on 30.06.15.
 */
class AuthorizationConfiguration {
    private val log = LogFactory.getLog(this.javaClass)

    /** Authorizer */
    private var authorizer: Authorizer

    /**
     * Application wide Node identity
     * @retur
     */
    var identity: Identity
        @Synchronized private set


    /**
     * Application wide system information
     * @return
     */
    val systemInformation: SystemInformation by lazy {
        SystemInformation.create()
    }

    companion object Singleton {
        @JvmStatic val instance by lazy {
            AuthorizationConfiguration()
        }
    }

    /**
     * Initialize
     */
    fun initialize() {
        // Actual initialization done by c'tor
    }

    /**
     * Creates and stores new identity
     */
    private fun createIdentity(): Identity {
        val identity = Identity.create(
                App.instance.name,
                this.systemInformation)

        // Store updates/created identity
        try {
            identity.save(StorageConfiguration.instance.identityConfigurationFile)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

        return identity
    }

    /**
     * Starts authorization
     */
    private fun startAuthorization() {
        this.authorizer.start(this.identity, onRejected = { identity ->
            log.warn("Authorization rejected for identity [${identity}]")
            // If rejected, create new identity and retry authorization
            this.identity = this.createIdentity()

            log.warn("Rebooting due to identity change")
            val installer = BundleConfiguration.bundleInstaller()
            installer.boot(App.instance.name)
            App.instance.shutdown()
        })
    }

    /**
     * c'tor
     */
    init {
        var identity: Identity? = null

        // Collect system information
        log.info(this.systemInformation)

        // Verify and read existing identity file
        val identityFile = StorageConfiguration.instance.identityConfigurationFile
        if (identityFile.exists()) {
            try {
                identity = Identity.load(this.systemInformation, identityFile)
            } catch (e: Exception) {
                log.error(e.message, e)
            }
        }

        // Create identity if it doesn't exist or could not be read/parsed
        if (identity == null) {
            identity = this.createIdentity()
        }

        this.identity = identity
        log.info(identity)

        // Start authorizer
        this.authorizer = Authorizer(ActiveMQConfiguration.instance)
        this.startAuthorization()
    }
}
/** c'tor  */
