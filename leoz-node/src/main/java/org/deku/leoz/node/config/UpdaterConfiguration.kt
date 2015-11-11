package org.deku.leoz.node.config

import org.apache.commons.logging.LogFactory
import org.deku.leoz.bundle.BundleInstaller
import org.deku.leoz.bundle.BundleUpdater
import org.deku.leoz.bundle.Bundles
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.deku.leoz.node.App
import org.springframework.context.annotation.Bean
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
    @Bean
    open fun bundleUpdater(): BundleUpdater {
        val installer = BundleInstaller(
                StorageConfiguration.instance.get().bundlesDirectory)

        return BundleUpdater(
                identity = this.identityConfiguration.identity,
                installer = installer,
                remoteRepository = BundleRepositoryConfiguration.stagingRepository,
                localRepository = BundleRepositoryConfiguration.localRepository,
                presets = listOf(
                        BundleUpdater.Preset(
                                bundleName = App.instance.get().name,
                                install = true,
                                storeInLocalRepository = false,
                                requiresBoot = true),
                        BundleUpdater.Preset(
                                bundleName = Bundles.LEOZ_BOOT,
                                install = true,
                                storeInLocalRepository = true)
                ),
                jmsConnectionFactory = ActiveMQConfiguration.instance.broker.connectionFactory,
                jmsUpdateRequestQueue = ActiveMQConfiguration.instance.centralQueue
        )
    }

    /** Broker listener  */
    private val brokerEventListener = object : Broker.EventListener {
        override fun onStart() {
            bundleUpdater().startUpdate()
        }

        override fun onStop() {
            bundleUpdater().stop()
        }
    }

    @PostConstruct
    fun onInitialize() {
        ActiveMQConfiguration.instance.broker.delegate.add(brokerEventListener)
        if (ActiveMQConfiguration.instance.broker.isStarted)
            brokerEventListener.onStart()
    }
}