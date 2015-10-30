package org.deku.leoz.node.config

import org.deku.leoz.bundle.BundleInstaller
import org.deku.leoz.bundle.BundleRepositoryFactory
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.deku.leoz.update.Updater
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import javax.annotation.PostConstruct
import javax.inject.Inject

/**
 * Updater configuration
 * Created by n3 on 30-Oct-15.
 */
@Configuration
@Lazy(false)
open class UpdaterConfiguration {

    /** Updater instance */
    lateinit var updater: Updater

    /** Bundle installer instance */
    var bundleInstaller: BundleInstaller

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
    }
}