package org.deku.leoz.node.config

import org.apache.commons.logging.LogFactory
import org.deku.leoz.bundle.BundleInstaller
import org.deku.leoz.bundle.BundleRepositoryFactory
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.deku.leoz.node.App
import org.deku.leoz.update.Updater
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

    /** Updater instance */
    lateinit var updater: Updater

    /** Bundle installer instance */
    var bundleInstaller: BundleInstaller

    /** Broker listener  */
    private val brokerEventListener = object : Broker.EventListener {
        override fun onStart() {
            updater.startUpdate(App.instance().name)
        }

        override fun onStop() {
            updater.stop()
        }
    }

    init {
        bundleInstaller = BundleInstaller(
                StorageConfiguration.instance.bundlesDirectory,
                BundleRepositoryFactory.stagingRepository()
        )
    }

    @Inject
    lateinit var identityConfiguration: IdentityConfiguration

    @PostConstruct
    fun onInitialize() {
        updater = Updater(
                identityConfiguration.identity,
                bundleInstaller,
                ActiveMQConfiguration.instance.broker.connectionFactory,
                ActiveMQConfiguration.instance.centralQueue
        )

        ActiveMQConfiguration.instance.broker.delegate.add(brokerEventListener)
        if (ActiveMQConfiguration.instance.broker.isStarted)
            brokerEventListener.onStart()
    }
}