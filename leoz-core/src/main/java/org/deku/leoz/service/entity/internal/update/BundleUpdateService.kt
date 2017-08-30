package org.deku.leoz.service.entity.internal.update

import sx.packager.Bundle
import sx.packager.BundleInstaller
import sx.packager.BundleRepository
import org.deku.leoz.bundle.boot
import org.deku.leoz.service.internal.BundleServiceV2
import org.slf4j.LoggerFactory
import io.reactivex.subjects.PublishSubject
import org.deku.leoz.identity.Identity
import org.deku.leoz.service.internal.entity.update.UpdateInfo
import sx.Lifecycle
import sx.concurrent.Service
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.platform.PlatformId
import sx.time.Duration
import java.util.*
import java.util.concurrent.ScheduledExecutorService
import kotlin.NoSuchElementException

/**
 * Updater suoporting async/background updates of bundles.
 * Can be added as a message handler to a notification topic message listener for push update notifications.
 * @property executorService Executor service
 * @property bundleServiceV1 Bundle service for requesting bundle/version information
 * @property identity Id of this leoz node. If this parameter is ommitted, the version alias must be provided per update  preset.
 * @property installer Bundle installer for installing bundles locally
 * @property remoteRepository Remote bundle repository. The bundle name of this repository has to match the installer name
 * @property localRepository Optional local repository
 * @property presets Bundke update presets
 * @property cleanup Automatically clean out outdated and non-relevant bundles
 * @property alwaysQueryRepository Useful for setup/testing where the bundle repository host is different from the one
 * hosting the BundleService. Defaults to false, as in productive setups both are the same.
 * Created by masc on 12.10.15.
 */
class BundleUpdateService(
        private val executorService: ScheduledExecutorService,
        private val bundleService: () -> BundleServiceV2,
        val identity: Identity? = null,
        val installer: BundleInstaller,
        val remoteRepository: () -> BundleRepository,
        val localRepository: BundleRepository? = null,
        presets: List<Preset>,
        val cleanup: Boolean = true,
        val alwaysQueryRepository: Boolean = false)
    :
        MqHandler<UpdateInfo>,
        Lifecycle {

    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Bundle update preset
     * @param bundleName Bundle to update
     * @param versionAlias Bundle version alias to look for. This parameter can be omitted if a (node) identity is provided to the service,
     * @param install Bundle should be installed
     * @param storeInLocalRepository Bundle should be stored in local repository
     * @param requiresBoot Bundle requires (re)boot of module/process
     */
    data class Preset(
            val bundleName: String,
            val versionAlias: String? = null,
            val install: Boolean = false,
            val storeInLocalRepository: Boolean = false,
            val requiresBoot: Boolean = false
    )

    /**
     * Update presets
     */
    val presets: List<Preset>

    /**
     * Background service
     */
    private val service = object : Service(
            executorService = this.executorService,
            initialDelay = Duration.ZERO) {

        override fun run() {
            // Clean bundles before update
            try {
                if (this@BundleUpdateService.cleanup)
                    this@BundleUpdateService.clean()
            } catch(e: Exception) {
                log.error(e.message, e)
            }

            presets.sortedBy { it.requiresBoot }.forEach { p ->
                try {
                    this@BundleUpdateService.update(p)
                } catch(e: Exception) {
                    log.error(e.message, e)
                }
            }

            log.info("Update sequence complete")
        }

        fun submit(command: () -> Unit) {
            this.submitSupplementalTask(command)
        }
    }

    init {
        // Bundle(s) requiring (re)boot go last
        this.presets = presets.sortedBy { p -> p.requiresBoot }
    }

    private val infoReceivedEventSubject by lazy { PublishSubject.create<UpdateInfo>() }
    /**
     * Update info received event
     */
    val infoReceived = infoReceivedEventSubject.hide()

    /**
     * Update notification message handler
     */
    override fun onMessage(message: UpdateInfo, replyChannel: MqChannel?) {
        val updateInfo = message
        log.info("Received update notification [${updateInfo}]")

        infoReceivedEventSubject.onNext(updateInfo)

        val preset = this.presets.firstOrNull { s -> s.bundleName.compareTo(updateInfo.bundleName, ignoreCase = true) == 0 }
        if (preset != null) {
            this.service.trigger()
        }
    }

    /**
     * Updater enabled/disabled
     */
    var enabled: Boolean = true

    /**
     * Clean any local bundles that don't have a preset set up
     */
    private fun clean() {
        if (this.localRepository != null) {
            this.localRepository.cleanBundles(this.presets.map { it.bundleName })
        }
    }

    /**
     * Schedule cleanup
     * @param preserve Preserve spec
     */
    fun scheduleCleanup(preserve: List<BundleRepository.PreserveSpec>) {
        if (this.localRepository != null) {
            this.service.submit {
                this.localRepository.cleanVersions(preserve)
            }
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

        log.info("Requesting version info for [${bundleName}] alias [${preset.versionAlias}] node [${this.identity?.uid?.short}]")

        // Request currently assigned version for this bundle and node
        val updateInfo: UpdateInfo = this.bundleService().info(
                bundleName = bundleName,
                versionAlias = preset.versionAlias,
                nodeKey = this.identity?.uid?.value)

        // Log info and emit event
        log.info("${updateInfo}")
        this.infoReceivedEventSubject.onNext(updateInfo)

        // Actual download & Installation
        val remoteRepository = this.remoteRepository()
        val latestDesignatedVersion: Bundle.Version
        val latestDesignatedPlatforms: List<PlatformId>

        if (this.alwaysQueryRepository) {
            try {
                // Query remote repository directly
                latestDesignatedVersion = remoteRepository.queryLatestMatchingVersion(
                        bundleName,
                        updateInfo.bundleVersionPattern)
            } catch(e: NoSuchElementException) {
                log.warn(e.message)
                return
            }

            latestDesignatedPlatforms = remoteRepository.listPlatforms(bundleName, latestDesignatedVersion)
        } else {
            // Rely on info delivered by BundleService
            if (updateInfo.latestDesignatedVersion == null)
                throw NoSuchElementException("No appropriate version available for bundle [${bundleName}]")

            latestDesignatedVersion = Bundle.Version.parse(updateInfo.latestDesignatedVersion!!)
            latestDesignatedPlatforms = updateInfo.latestDesignatedVersionPlatforms.map { PlatformId.parse(it) }
        }

        val fullBundleName = "${bundleName}-${latestDesignatedVersion}"

        // The repository to actually install from.
        // This may be the local repository if this bundle is supposed to be downloaded to local repository anyway
        // or the remote repository if it's installed directly.
        val repositoryToInstallFrom: BundleRepository

        if (preset.storeInLocalRepository) {
            // Synchronize to local repository
            if (this.localRepository == null)
                throw IllegalStateException("Cannot store bundle [${preset.bundleName}] as local repository is not set")

            repositoryToInstallFrom = this.localRepository

            val existsLocally = this.localRepository
                    .listVersions(bundleName)
                    .contains(latestDesignatedVersion)

            val allPlatformsExistLocally = latestDesignatedPlatforms
                    .subtract(this.localRepository.listPlatforms(bundleName, latestDesignatedVersion))
                    .count() == 0

            if (!existsLocally || !allPlatformsExistLocally) {
                remoteRepository.download(
                        bundleName = bundleName,
                        version = latestDesignatedVersion,
                        localRepository = this.localRepository)
            } else {
                log.info("Bundle [${fullBundleName}] already exists in local repository")
            }
        } else {
            repositoryToInstallFrom = this.remoteRepository()
        }

        // Clean older bundles
        if (this.localRepository != null && this.cleanup) {
            this.localRepository.cleanVersions(bundleName, listOf(latestDesignatedVersion))
        }

        if (preset.install) {
            val currentPlatform = PlatformId.current()
            if (latestDesignatedPlatforms.contains(currentPlatform)) {
                val readyToInstall = this.installer.download(
                        bundleRepository = repositoryToInstallFrom,
                        bundleName = bundleName,
                        version = latestDesignatedVersion)

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
            } else {
                log.warn("Bundle [${fullBundleName}] is not available for this platform [${currentPlatform}]")
            }
        }

        log.info("Update sequence for bundle [${bundleName}] complete")
    }

    override fun start() {
        this.service.start()
    }

    override fun stop() {
        this.service.stop()
    }

    override fun restart() {
        this.service.restart()
    }

    override fun isRunning(): Boolean {
        return this.service.isRunning()
    }

    fun trigger() {
        this.service.trigger()
    }

    override fun close() {
        this.service.stop()
    }
}