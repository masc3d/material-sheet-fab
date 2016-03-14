package org.deku.leoz.node.config

import org.apache.commons.logging.LogFactory
import org.deku.leoz.bundle.BundleRepository
import org.deku.leoz.bundle.BundleUpdater
import org.deku.leoz.bundle.Bundles
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.deku.leoz.node.App
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.jms.Channel
import sx.jms.embedded.Broker
import sx.rsync.Rsync
import sx.ssh.SshTunnelProvider
import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.inject.Named
import kotlin.properties.Delegates

/**
 * Leoz updater configuration
 * Created by masc on 30-Oct-15.
 */
@Configuration
@Lazy(false)
open class UpdaterConfiguration {
    private val log = LogFactory.getLog(this.javaClass)

    @Named
    @ConfigurationProperties(prefix = "update")
    class Settings {
        var enabled: Boolean = false
        var onStartup: Boolean = true
        var rsyncUri: String by Delegates.notNull()
        var rsyncPassword: String by Delegates.notNull()
    }

    @Inject
    private lateinit var settings: Settings

    /**
     * Local bundle repository
     * */
    val localRepository: BundleRepository by lazy({
        BundleRepository(
                Rsync.URI(StorageConfiguration.instance.bundleRepositoryDirectory))
    })

    /**
     * Remote bundle update repository
     * */
    val updateRepository: BundleRepository by lazy({
        BundleRepository(rsyncModuleUri = Rsync.URI(this.settings.rsyncUri),
                rsyncPassword = this.settings.rsyncPassword,
                sshTunnelProvider = this.sshTunnelProvider)

    })

    /**
     * SSH tunnel provider
     */
    @Inject
    lateinit var sshTunnelProvider: SshTunnelProvider

    @Inject
    lateinit var bundleUpdater: BundleUpdater

    /**
     * Updater instance
     */
    @Bean
    open fun bundleUpdater(): BundleUpdater {
        val installer = BundleConfiguration.bundleInstaller()

        val updater = BundleUpdater(
                identity = App.instance.identity,
                installer = installer,
                remoteRepository = this.updateRepository,
                localRepository = this.localRepository,
                presets = listOf(
                        BundleUpdater.Preset(
                                bundleName = App.instance.name,
                                install = true,
                                storeInLocalRepository = false,
                                requiresBoot = true),
                        BundleUpdater.Preset(
                                bundleName = Bundles.LEOZ_BOOT.value,
                                install = true,
                                storeInLocalRepository = true)
                ),
                updateInfoRequestChannel = Channel(ActiveMQConfiguration.instance.centralQueue)
        )
        updater.enabled = this.settings.enabled
        return updater
    }

    /** Broker listener  */
    private val brokerEventListener = object : Broker.DefaultEventListener() {
        override fun onStart() {
            // Run bundle updater initially/on startup
            if (this@UpdaterConfiguration.settings.onStartup)
                this@UpdaterConfiguration.bundleUpdater.startUpdate()
        }

        override fun onStop() {
            this@UpdaterConfiguration.bundleUpdater.stop()
        }
    }

    @PostConstruct
    fun onInitialize() {
        ActiveMQConfiguration.instance.broker.delegate.add(brokerEventListener)
        if (ActiveMQConfiguration.instance.broker.isStarted)
            brokerEventListener.onStart()
    }
}