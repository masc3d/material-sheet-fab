package org.deku.leoz.bundle.update

import org.apache.commons.logging.LogFactory
import org.deku.leoz.Identity
import org.deku.leoz.bundle.BundleInstaller
import org.deku.leoz.bundle.BundleRepository
import org.deku.leoz.bundle.boot
import org.deku.leoz.bundle.update.entities.UpdateInfo
import org.deku.leoz.bundle.update.entities.UpdateInfoRequest
import sx.Disposable
import sx.jms.Channel
import sx.jms.Converter
import sx.jms.Handler
import sx.jms.converters.DefaultConverter
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.jms.ConnectionFactory
import javax.jms.Destination
import javax.jms.Message
import javax.jms.Session

/**
 * Updater suoporting async/background updates of bundles.
 * Can be added as a message handler to a notification topic message listener for push update notifications.
 * @property identity Id of this leoz node
 * @property installer Bundle installer for installing bundles locally
 * @property remoteRepository Remote bundle repository. The bundle name of this repository has to match the installer name
 * @property localRepository Optional local repository
 * @property presets Bundke update presets
 * @property jmsConnectionFactory JMS connection factory
 * @property jmsUpdateRequestQueue JMS queue to use for update requests
 * Created by masc on 12.10.15.
 */
class BundleUpdater(
        public val identity: Identity,
        public val installer: BundleInstaller,
        public val remoteRepository: BundleRepository,
        public val localRepository: BundleRepository? = null,
        public val presets: List<BundleUpdater.Preset>,
        private val jmsConnectionFactory: ConnectionFactory,
        private val jmsUpdateRequestQueue: Destination)
:
        Handler<UpdateInfo>,
        Disposable {

    /**
     * Bundle update preset
     * @param bundleName Bundle to update
     * @param install Bundle should be installed
     * @param storeInLocalRepository Bundle should be stored in local repository
     * @param requiresBoot Bundle requires (re)boot of module/process
     */
    data class Preset(
            val bundleName: String,
            val install: Boolean = false,
            val storeInLocalRepository: Boolean = false,
            val requiresBoot: Boolean = false
    ) { }

    private val log = LogFactory.getLog(this.javaClass)

    private val executor = Executors.newScheduledThreadPool(2)
    private val updateInfoRequestChannel: Channel

    init {
        this.updateInfoRequestChannel = Channel(
                connectionFactory = jmsConnectionFactory,
                destination = jmsUpdateRequestQueue,
                jmsSessionTransacted = false,
                converter = DefaultConverter(
                        DefaultConverter.SerializationType.KRYO,
                        DefaultConverter.CompressionType.GZIP),
                jmsDeliveryMode = Channel.DeliveryMode.NonPersistent,
                jmsTtl = Duration.ofSeconds(10))
    }

    /**
     * Update notification message handler
     */
    override fun onMessage(message: UpdateInfo, converter: Converter, jmsMessage: Message, session: Session) {
        val updateInfo = message
        log.info("Received update notification [${updateInfo}]")

        val preset = this.presets.firstOrNull { s -> s.bundleName.compareTo(updateInfo.bundleName, ignoreCase = true) == 0 }
        if (preset != null) {
            this.startUpdate(preset)
        }
    }

    /**
     * Start update for specific preset
     * @param preset Update preset
     */
    private fun startUpdate(preset: Preset) {
        // Schedule update
        this.executor.submit({
            this.update(preset)
        })
    }

    /**
     * Start update for all bundle presets
     */
    public fun startUpdate() {
        this.presets.forEach { p -> this.startUpdate(p) }
    }

    public fun stop() {
        this.executor.shutdownNow()
        this.executor.awaitTermination(java.lang.Long.MAX_VALUE, TimeUnit.SECONDS)
    }

    /**
     * Run the update process
     */
    @Synchronized private fun update(preset: Preset) {
        try {
            val bundleName = preset.bundleName

            log.info("Starting update sequence for bundle [${bundleName}]")
            val nodeId = this.identity.id
            if (nodeId == null) {
                log.warn("Identity not available, aborting update for bundle [${bundleName}]")
                return
            }

            log.info("Requesting version info for [${bundleName}]")

            // Request currently assigned version for this bundle and node
            val updateInfo = this.updateInfoRequestChannel.sendReceive(
                    UpdateInfoRequest(nodeId, bundleName),
                    UpdateInfo::class.java,
                    useTemporaryResponseQueue = true)

            log.info("Update info [${updateInfo}]")

            // Determine remote version matching version pattern
            val version = this.remoteRepository.queryLatestMatchingVersion(bundleName, updateInfo.bundleVersionPattern)

            log.info("Matching remote version is [${bundleName}-${version}]")

            val repositoryToInstallFrom: BundleRepository

            if (preset.storeInLocalRepository) {
                // Synchronize to local repository
                if (this.localRepository == null)
                    throw IllegalStateException("Cannot store bundle [${preset.bundleName}] as local repository is not set")

                repositoryToInstallFrom = this.localRepository

                this.remoteRepository.download(
                        bundleName = bundleName,
                        version = version,
                        localRepository = this.localRepository)
            } else {
                repositoryToInstallFrom = this.remoteRepository
            }

            if (preset.install) {
                this.installer.download(
                        bundleRepository = repositoryToInstallFrom,
                        bundleName = bundleName,
                        version = version)

                if (preset.requiresBoot) {
                    this.installer.boot(bundleName)
                } else {
                    this.installer.install(bundleName)
                }
            }

            log.info("Update equence for bundle [${bundleName}] complete")

        } catch(e: Exception) {
            log.error(e.message, e)
        }
    }

    override fun dispose() {
        this.stop()
    }
}