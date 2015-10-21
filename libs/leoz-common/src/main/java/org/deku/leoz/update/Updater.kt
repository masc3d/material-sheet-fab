package org.deku.leoz.update

import org.apache.commons.logging.LogFactory
import org.deku.leoz.Identity
import org.deku.leoz.bundle.Bundle
import org.deku.leoz.bundle.BundleInstaller
import org.deku.leoz.bundle.BundleRepository
import sx.jms.Channel
import sx.jms.Converter
import sx.jms.Handler
import sx.jms.converters.DefaultConverter
import sx.jms.listeners.SpringJmsListener
import java.io.File
import java.io.Serializable
import java.time.Duration
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.jms.*

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

    private val executor = Executors.newFixedThreadPool(2)
    private val updateRequestChannel: Channel

    init {
        // Use all available bundles if names are not explicitly provided
        this.bundleNames = BundleInstaller.listBundleNames(this.bundleContainerPath)
        this.bundlePaths = BundleInstaller.listBundlePaths(this.bundleContainerPath)

        this.updateRequestChannel = Channel(
                connectionFactory = jmsConnectionFactory,
                destination = jmsUpdateRequestQueue,
                converter = DefaultConverter(
                        DefaultConverter.SerializationType.KRYO,
                        DefaultConverter.CompressionType.NONE),
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
            this.executor.submit(Runnable {
                this.update(updateInfo.bundleName)
            })
        }
    }

    /**
     * Run the update process
     */
    @Synchronized private fun update(bundleName: String) {
        try {
            val nodeId = identity.id
            if (nodeId == null)
                return

            // Request currently assigned version for this bundle and node
            val updateInfo = this.updateRequestChannel.sendReceive(
                    UpdateInfoRequest(nodeId, bundleName),
                    UpdateInfo::class.java)

            // Version check
            val updateBundleVersion = Bundle.Version.parse(updateInfo.bundleVersion)
            val bundle = Bundle.load(
                    BundleInstaller.getNativeBundlePath(
                            File(bundleContainerPath, bundleName)))

            if (!updateBundleVersion.equals(bundle.version)) {
                // Version differs -> update
                val installer = BundleInstaller(this.bundleContainerPath, bundleName, this.bundleRepository)

                installer.download(updateBundleVersion, prepareAsUpdate = true)
            }
        } catch(e: Exception) {
            log.error(e.getMessage(), e)
        }
    }
}