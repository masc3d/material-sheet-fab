package org.deku.leoz.bundle

import org.apache.commons.logging.LogFactory
import org.deku.leoz.Identity
import org.deku.leoz.bundle.entities.UpdateInfo
import org.deku.leoz.bundle.entities.UpdateInfoRequest
import sx.Disposable
import sx.concurrent.Service
import sx.jms.Channel
import sx.jms.Handler
import java.util.concurrent.ScheduledExecutorService

/**
 * Updater suoporting async/background updates of bundles.
 * Can be added as a message handler to a notification topic message listener for push update notifications.
 * @property executorService Executor service
 * @property requestChannel The JMS channel to use to issue update info requests
 * @property identity Id of this leoz node
 * @property installer Bundle installer for installing bundles locally
 * @property remoteRepository Remote bundle repository. The bundle name of this repository has to match the installer name
 * @property localRepository Optional local repository
 * @property presets Bundke update presets
 * Created by masc on 12.10.15.
 */
class BundleUpdateService(
        private val executorService: ScheduledExecutorService,
        private val requestChannel: Channel,
        public val identity: Identity,
        public val installer: BundleInstaller,
        public val remoteRepository: BundleRepository,
        public val localRepository: BundleRepository? = null,
        presets: List<BundleUpdateService.Preset>)
:
        Handler<UpdateInfo>,
        Disposable {

    private val log = LogFactory.getLog(this.javaClass)

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

    /**
     * Update presets
     */
    val presets: List<Preset>

    /**
     * Background service
     */
    private val service = object : Service(executorService = this.executorService) {
        override fun run() {
            this@BundleUpdateService.startUpdate()
        }
    }

    init {
        // Bundle(s) requiring (re)boot go last
        this.presets = presets.sortedBy { p -> p.requiresBoot }
        this.service.start()
    }

    /**
     * Update notification message handler
     */
    override fun onMessage(message: UpdateInfo, replyChannel: Channel?) {
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
    private fun startUpdate(vararg presets: Preset = this.presets.toTypedArray()) {
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

        log.info("Requesting version info for [${bundleName}]")

        // Request currently assigned version for this bundle and node
        val updateInfo = this.requestChannel.sendRequest(UpdateInfoRequest(this.identity.key, bundleName)).use {
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

    fun trigger() {
        this.service.trigger()
    }

    override fun close() {
        this.service.stop()
    }
}