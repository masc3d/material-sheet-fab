package org.deku.leoz.update

import org.apache.commons.logging.LogFactory
import org.deku.leoz.Identity
import org.deku.leoz.bundle.Bundle
import org.deku.leoz.bundle.BundleInstaller
import org.deku.leoz.bundle.BundleRepository
import org.deku.leoz.bundle.filter
import sx.event.EventDispatcher
import sx.jms.Channel
import sx.jms.Handler
import sx.jms.converters.DefaultConverter
import java.io.File
import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.Executors
import javax.jms.ConnectionFactory
import javax.jms.Destination
import javax.jms.Message
import javax.jms.Session

/**
 * Updater suoporting async/background updates of bundles.
 * Can be added as a message handler to a notification topic message listener for push update notifications.
 * @property nodeId Id of this leoz node
 * @property bundleContainerPath Path containing bundles
 * @property bundleRepository Remote bundle repository. The bundle name of this repository has to match the installer name
 * @property jmsConnectionFactory JMS connection factory
 * @property jmsUpdateRequestQueue JMS queue to use for update requests
 * Created by masc on 12.10.15.
 */
class Updater(
        public val identity: Identity,
        public val bundleContainerPath: File,
        public val bundleRepository: BundleRepository,
        private val jmsConnectionFactory: ConnectionFactory,
        private val jmsUpdateRequestQueue: Destination) : Handler<UpdateInfo> {

    private val log = LogFactory.getLog(this.javaClass)
    private val bundleNames: List<String>
    private val bundlePaths: List<File>

    private val executor = Executors.newScheduledThreadPool(2)
    private val updateInfoRequestChannel: Channel

    //region Events
    interface Listener : sx.event.EventListener {
        /**
         * Emitted when update was successfully prepared
         * @param desiredRestartTime Desired time for restarting if a bundle is self updating. If omitted the update is supposed to become active asap.
         */
        fun onUpdatePrepared(bundleName: String, bundleVersion: Bundle.Version, desiredRestartTime: LocalTime?)
    }
    private val eventDispatcher = EventDispatcher.createThreadSafe<Listener>()
    public val eventDelegate = eventDispatcher
    //endregion

    init {
        // Use all available bundles if names are not explicitly provided
        this.bundleNames = BundleInstaller.listBundleNames(this.bundleContainerPath)
        this.bundlePaths = BundleInstaller.listBundlePaths(this.bundleContainerPath)

        this.updateInfoRequestChannel = Channel(
                connectionFactory = jmsConnectionFactory,
                destination = jmsUpdateRequestQueue,
                converter = DefaultConverter(
                        DefaultConverter.SerializationType.KRYO,
                        DefaultConverter.CompressionType.GZIP),
                jmsSessionTransacted = false,
                jmsDeliveryMode = Channel.DeliveryMode.NonPersistent,
                jmsTtl = Duration.ofSeconds(10))
    }

    /**
     * Update notification message handler
     */
    override fun onMessage(updateInfo: UpdateInfo, jmsMessage: Message, session: Session) {
        log.info("Received update notification [${updateInfo}]")
        if (this.bundleNames.contains(updateInfo.bundleName)) {
            // Schedule update
            this.executor.submit({
                this.update(updateInfo.bundleName)
            })
        }
    }

    /**
     * Run the update process
     */
    @Synchronized private fun update(bundleName: String) {
        try {
            log.info("Starting update sequence for bundle [${bundleName}]")
            val nodeId = this.identity.id
            if (nodeId == null) {
                log.warn("Identity not available, aborting update for bundle [${bundleName}]")
                return
            }

            // Request currently assigned version for this bundle and node
            val updateInfo = this.updateInfoRequestChannel.sendReceive(
                    UpdateInfoRequest(nodeId, bundleName),
                    UpdateInfo::class.java)

            // Version check
            val bundle = Bundle.load(
                    BundleInstaller.getNativeBundlePath(
                            File(this.bundleContainerPath, bundleName)))

            log.info("Bundle [${bundleName}] current version [${bundle.version}]")

            val availableVersions = this.bundleRepository
                    .listVersions(bundleName)

            log.info("Repository [${this.bundleRepository} has following versions of [${bundleName}]: ${availableVersions.map { it -> it.toString() }.joinToString(", ")}")

            val latestMatchingVersion = availableVersions.filter(updateInfo.bundleVersionPattern)
                    .sortedDescending()
                    .firstOrNull()

            if (latestMatchingVersion == null) {
                log.warn("No version matching [${updateInfo.bundleVersionPattern}] ")
                return
            }

            if (latestMatchingVersion.equals(bundle.version)) {
                log.info("Version [${latestMatchingVersion}] already installed")
                return
            }

            // Install as prepared update
            log.info("Updating [${bundleName}] to [${latestMatchingVersion}]")
            val installer = BundleInstaller(this.bundleContainerPath, bundleName, this.bundleRepository)
            installer.download(latestMatchingVersion, prepareAsUpdate = true)

            this.eventDispatcher.emit { l -> l.onUpdatePrepared(bundleName, latestMatchingVersion, updateInfo.desiredRestartTime) }

        } catch(e: Exception) {
            log.error(e.message, e)
        }
    }
}