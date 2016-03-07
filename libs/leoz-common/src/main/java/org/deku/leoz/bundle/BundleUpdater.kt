package org.deku.leoz.bundle

import org.apache.commons.logging.LogFactory
import org.deku.leoz.Identity
import org.deku.leoz.bundle.entities.UpdateInfo
import org.deku.leoz.bundle.entities.UpdateInfoRequest
import sx.Disposable
import sx.jms.Channel
import sx.jms.Converter
import sx.jms.Handler
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.jms.ConnectionFactory
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
        presets: List<BundleUpdater.Preset>,
        private val updateInfoRequestChannel: Channel)
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

    val presets: List<Preset>

    init {
        // Bundle(s) requiring (re)boot go last
        this.presets = presets.sortedBy { p -> p.requiresBoot }
    }

    /**
     * Update notification message handler
     */
    override fun onMessage(message: UpdateInfo, converter: Converter, jmsMessage: Message, session: Session, connectionFactory: ConnectionFactory) {
        val updateInfo = message
        log.info("Received update notification [${updateInfo}]")

        val preset = this.presets.firstOrNull { s -> s.bundleName.compareTo(updateInfo.bundleName, ignoreCase = true) == 0 }
        if (preset != null) {
            this.startUpdate(preset)
        }
    }

    /**
     * Updater enabled/disabled
     */
    var enabled: Boolean = true

    /**
     * Start update for all bundle presets
     * @param presets Presets to update, defaults to internal list of presets
     */
    public fun startUpdate(vararg presets: Preset = this.presets.toTypedArray()) {
        this.executor.submit({
            // Clean bundles before update
            try {
                this.clean()
            } catch(e: Exception) {
                log.error(e.message, e)
            }

            presets.forEach { p ->
                try {
                    this.update(p)
                } catch(e: Exception) {
                    log.error(e.message, e)
                }

                // Clean bundle version after update
                try {
                    this.clean(p.bundleName)
                } catch(e: Exception) {
                    log.error(e.message, e)
                }
            }

            log.info("Update sequence complete")
        })
    }

    /**
     * Stop all bundle tasks
     */
    public fun stop() {
        log.info("Stopping bundle updater")
        this.executor.shutdownNow()
        this.executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)
    }

    /**
     * Clean any local bundles that are not contained within remote repository.
     * In case this updater has been created without local repository, this method doesn't do anything.
     */
    private fun clean() {
        if (this.localRepository != null) {
            this.localRepository.clean(this.remoteRepository.listBundles())
        }
    }

    /**
     * Clean any local versions for a bundle that are not contained within remote repository.
     * In case this updater has been created without local repository, this method doesn't do anything.
     * @param bundleName Bundle to clean
     */
    private fun clean(bundleName: String) {
        if (this.localRepository != null) {
            this.localRepository.clean(bundleName, this.remoteRepository.listVersions(bundleName))
        }
    }

    /**
     * Run the update process
     */
    @Synchronized private fun update(preset: Preset) {
        val bundleName = preset.bundleName

        log.info("Starting update sequence for bundle [${bundleName}]")

        if (!this.enabled) {
            log.warn("Updates have been disabled")
            return
        }

        val nodeId = this.identity.id
        if (nodeId == null) {
            log.warn("Identity not available, aborting update for bundle [${bundleName}]")
            return
        }

        log.info("Requesting version info for [${bundleName}]")

        // Request currently assigned version for this bundle and node
        val updateInfo = this.updateInfoRequestChannel.sendRequest(UpdateInfoRequest(nodeId, bundleName)).use {
            it.receive(UpdateInfo::class.java)
        }

        log.info("Update info [${updateInfo}]")

        // Determine remote version matching version pattern
        val version = this.remoteRepository.queryLatestMatchingVersion(bundleName, updateInfo.bundleVersionPattern)

        log.info("Matching remote version is [${bundleName}-${version}]")

        // The repository to actually install from.
        // This may be the local repository if this bundle is supposed to be downloaded to local repository anyway
        // or the remote repository if it's installed directly.
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
            val readyToInstall = this.installer.download(
                    bundleRepository = repositoryToInstallFrom,
                    bundleName = bundleName,
                    version = version)

            if (readyToInstall) {
                if (preset.requiresBoot) {
                    // TODO: add support for updateInfo.desiredStartTime
                    this.installer.boot(bundleName)
                } else {
                    this.installer.install(bundleName)
                }
            } else {
                log.info("Bundle [${bundleName}] is already uptodate.")
            }
        }

        log.info("Update sequence for bundle [${bundleName}] complete")
    }

    override fun close() {
        this.stop()
    }
}