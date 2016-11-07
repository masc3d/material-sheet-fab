package org.deku.leoz.service.update

import org.deku.leoz.Identity
import org.deku.leoz.bundle.Bundle
import org.deku.leoz.bundle.BundleInstaller
import org.deku.leoz.bundle.BundleRepository
import org.deku.leoz.bundle.boot
import org.deku.leoz.rest.service.internal.v1.BundleService
import org.slf4j.LoggerFactory
import rx.lang.kotlin.PublishSubject
import sx.Lifecycle
import sx.concurrent.Service
import sx.jms.Channel
import sx.jms.Handler
import sx.platform.PlatformId
import java.time.Duration
import java.util.concurrent.ScheduledExecutorService

/**
 * Updater suoporting async/background updates of bundles.
 * Can be added as a message handler to a notification topic message listener for push update notifications.
 * @property executorService Executor service
 * @property bundleService Bundle service for requesting bundle/version information
 * @property identity Id of this leoz node
 * @property installer Bundle installer for installing bundles locally
 * @property remoteRepository Remote bundle repository. The bundle name of this repository has to match the installer name
 * @property localRepository Optional local repository
 * @property presets Bundke update presets
 * @property cleanup Automatically clean out outdated and non-relevant bundles
 * Created by masc on 12.10.15.
 */
class BundleUpdateService(
        private val executorService: ScheduledExecutorService,
        private val bundleService: () -> BundleService,
        val identity: Identity,
        val installer: BundleInstaller,
        val remoteRepository: BundleRepository,
        val localRepository: BundleRepository? = null,
        presets: List<Preset>,
        val cleanup: Boolean = true)
:
        Handler<UpdateInfo>,
        Lifecycle {

    private val log = LoggerFactory.getLogger(this.javaClass)

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
    ) {}

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

            presets.forEach { p ->
                try {
                    this@BundleUpdateService.update(p)
                } catch(e: Exception) {
                    log.error(e.message, e)
                }
            }

            log.info("Update sequence complete")
        }
    }

    init {
        // Bundle(s) requiring (re)boot go last
        this.presets = presets.sortedBy { p -> p.requiresBoot }
    }

    private val ovsInfoReceived by lazy { PublishSubject<UpdateInfo>() }
    /**
     * Update info received event
     */
    val ovInfoReceived = ovsInfoReceived.asObservable()

    /**
     * Update notification message handler
     */
    override fun onMessage(message: UpdateInfo, replyChannel: Channel?) {
        val updateInfo = message
        log.info("Received update notification [${updateInfo}]")

        ovsInfoReceived.onNext(updateInfo)

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
            this.localRepository.clean(this.presets.map { it.bundleName })
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
        val updateInfo = this.bundleService().info(bundleName = bundleName, nodeKey = this.identity.key)

        log.info("Update info [${updateInfo}]")
        ovsInfoReceived.onNext(updateInfo)

        if (updateInfo.latestDesignatedVersion != null) {
            val latestDesignatedVersion = Bundle.Version.parse(updateInfo.latestDesignatedVersion)
            val latestDesignatedPlatforms = updateInfo.latestDesignatedVersionPlatforms.map { PlatformId.parse(it) }
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
                    this.remoteRepository.download(
                            bundleName = bundleName,
                            version = latestDesignatedVersion,
                            localRepository = this.localRepository)
                } else {
                    log.info("Bundle [${fullBundleName}] already exists in local repository")
                }
            } else {
                repositoryToInstallFrom = this.remoteRepository
            }

            // Clean older bundles
            if (this.localRepository != null && this.cleanup) {
                this.localRepository.clean(bundleName, listOf(latestDesignatedVersion))
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
        } else {
            log.warn("No appropriate version available for bundle [${bundleName}]")
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