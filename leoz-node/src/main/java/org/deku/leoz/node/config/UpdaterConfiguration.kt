package org.deku.leoz.node.config

import org.apache.commons.logging.LogFactory
import org.deku.leoz.bundle.BundleInstaller
import org.deku.leoz.bundle.update.Updater
import org.deku.leoz.config.BundleRepositoryConfiguration
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.deku.leoz.node.App
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.jms.embedded.Broker
import javax.annotation.PostConstruct
import javax.inject.Inject

/**
 * Updater configuration
 * Created by n3 on 30-Oct-15.
 */
@Configuration
@Lazy(false)
open class UpdaterConfiguration {
    private val log = LogFactory.getLog(this.javaClass)

    @Inject
    lateinit var identityConfiguration: IdentityConfiguration

    /** Updater instance */
    lateinit var bundleUpdater: Updater

    /** Bundle installer instance */
    val bundleInstaller: BundleInstaller

    /** Broker listener  */
    private val brokerEventListener = object : Broker.EventListener {
        override fun onStart() {
            bundleUpdater.startUpdate(App.instance().name)
        }

        override fun onStop() {
            bundleUpdater.stop()
        }
    }

    init {
        this.bundleInstaller = BundleInstaller(StorageConfiguration.instance.bundlesDirectory)
    }

    @PostConstruct
    fun onInitialize() {
        bundleUpdater = Updater(
                identity = this.identityConfiguration.identity,
                installer = this.bundleInstaller,
                remoteRepository = BundleRepositoryConfiguration.stagingRepository(),
                jmsConnectionFactory = ActiveMQConfiguration.instance.broker.connectionFactory,
                jmsUpdateRequestQueue = ActiveMQConfiguration.instance.centralQueue
        )

        ActiveMQConfiguration.instance.broker.delegate.add(brokerEventListener)
        if (ActiveMQConfiguration.instance.broker.isStarted)
            brokerEventListener.onStart()
    }
}