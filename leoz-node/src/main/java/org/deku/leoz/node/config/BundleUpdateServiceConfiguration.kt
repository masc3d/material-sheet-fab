package org.deku.leoz.node.config

import org.apache.commons.logging.LogFactory
import org.deku.leoz.bundle.BundleRepository
import org.deku.leoz.bundle.BundleUpdateService
import org.deku.leoz.bundle.Bundles
import org.deku.leoz.bundle.entities.UpdateInfo
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
import java.util.concurrent.ScheduledExecutorService
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
open class BundleUpdateServiceConfiguration {
    private val log = LogFactory.getLog(this.javaClass)

    @Named
    @ConfigurationProperties(prefix = "update")
    class Settings {
        var enabled: Boolean = false
        var automatic: Boolean = true
        var rsyncUri: String by Delegates.notNull()
        var rsyncPassword: String by Delegates.notNull()
    }

    @Inject
    private lateinit var settings: Settings

    @Inject
    private lateinit var executorService: ScheduledExecutorService

    @Inject
    private lateinit var messageListenerConfiguration: MessageListenerConfiguration

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

    /**
     * Updater instance
     */
    @Bean
    open fun bundleUpdater(): BundleUpdateService {
        val installer = BundleConfiguration.bundleInstaller()

        val updater = BundleUpdateService(
                executorService = this.executorService,
                identity = App.instance.identity,
                installer = installer,
                remoteRepository = this.updateRepository,
                localRepository = this.localRepository,
                presets = listOf(
                        BundleUpdateService.Preset(
                                bundleName = App.instance.name,
                                install = true,
                                storeInLocalRepository = false,
                                requiresBoot = true),
                        BundleUpdateService.Preset(
                                bundleName = Bundles.LEOZ_BOOT.value,
                                install = true,
                                storeInLocalRepository = true)
                ),
                requestChannel = Channel(ActiveMQConfiguration.instance.centralQueue)
        )
        updater.enabled = this.settings.enabled
        return updater
    }

    private val bundleUpdater by lazy { bundleUpdater() }

    /** Broker listener  */
    private val brokerEventListener = object : Broker.DefaultEventListener() {
        val firstStart: Boolean = true

        override fun onConnectedToBrokerNetwork() {
            // Run bundle updater initially/on startup
            if (this@BundleUpdateServiceConfiguration.settings.automatic)
                this@BundleUpdateServiceConfiguration.bundleUpdater.trigger()
        }

        override fun onStop() {
            this@BundleUpdateServiceConfiguration.bundleUpdater.close()
        }
    }

    @PostConstruct
    fun onInitialize() {
        ActiveMQConfiguration.instance.broker.delegate.add(brokerEventListener)
        if (ActiveMQConfiguration.instance.broker.isStarted)
            brokerEventListener.onStart()

        // Register for update notifications (as long as automatic updates are enabled)
        if (this@BundleUpdateServiceConfiguration.settings.automatic) {
            this.messageListenerConfiguration.nodeNotificationListener.addDelegate(
                    UpdateInfo::class.java,
                    this.bundleUpdater)
        }
    }
}