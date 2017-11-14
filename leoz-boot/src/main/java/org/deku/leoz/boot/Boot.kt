package org.deku.leoz.boot

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.google.common.base.Strings
import org.deku.leoz.Storage
import org.deku.leoz.boot.config.BundleConfiguration
import org.deku.leoz.bundle.*
import sx.rs.proxy.RestClient
import org.deku.leoz.service.internal.BundleServiceV2
import org.deku.leoz.service.internal.DiscoveryService
import org.slf4j.LoggerFactory
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.cast
import org.threeten.bp.Duration
import sx.packager.Bundle
import sx.packager.BundleInstaller
import sx.packager.BundleRepository
import sx.platform.PlatformId
import sx.rsync.Rsync
import sx.rsync.RsyncClient
import sx.rx.task
import sx.ssh.SshTunnelProvider
import java.io.File
import java.util.*

/**
 * Boot model
 * Created by masc on 08/11/2016.
 */
class Boot {
    private val log = LoggerFactory.getLogger(this.javaClass)

    // Injections
    private val storage: Storage by Kodein.global.lazy.instance()
    private val restConfiguration: RestClientFactory by Kodein.global.lazy.instance()
    private val bundleConfiguration: BundleConfiguration by Kodein.global.lazy.instance()
    private val installer: BundleInstaller by Kodein.global.lazy.instance()
    private val discoveryService: DiscoveryService by Kodein.global.lazy.instance()
    private val sshTunnelProvider: SshTunnelProvider by Kodein.global.lazy.instance()

    /**
     * Event
     */
    data class Event(
            val progress: Double
    )

    class DiscoveryException(e: Throwable) : Exception(e)
    class InstallationException(e: Throwable) : Exception(e)

    /**
     * Calculate progress of intermediate steps
     * @param startProgress Start of progress for intermediate step
     * @param endProgress End of progress for intermediate step
     * @param progress Current progress (from 0.0 to 1.0)
     */
    private fun skewProgress(startProgress: Double, endProgress: Double, progress: Double): Double {
        if (progress < 0)
            return progress

        val range = endProgress - startProgress
        return startProgress + (range * progress)
    }

    /**
     * Discovers leoz-node in local network and amends configurations to connect to this host
     */
    fun discover(): Single<Any> {
        return discoveryService.discoverFirst(
                predicate = {
                    it.bundleType == BundleType.LEOZ_NODE
                },
                timeout = Duration.ofSeconds(2))
                .doOnSuccess {
                    val host = it.address.hostAddress.toString()

                    // Update REST configuration. This will affect all subsequent REST client invocations
                    log.info("Setting REST host based on discovery to ${host}")
                    restConfiguration.host = host
                    restConfiguration.https = false
                    bundleConfiguration.rsyncHost = host
                }
                .onErrorReturn {
                    throw DiscoveryException(it)
                }
                .cast()
    }

    /**
     * Performs self-instllation of leoz-boot (into leoz bundles directory)
     * @param onProgress Progress callback
     */
    fun selfInstall(): Observable<Event> {
        return task<Event> { onNext ->
            if(File(this.javaClass.protectionDomain.codeSource.location.toURI()).isDirectory) {
                log.warn("Not running from jar file, skipping self-installation")
                return@task
            }

            val runtimeBundle = Bundle.load(this.javaClass)
            val bundleInstallationDirectory = storage.bundleInstallationDirectory.toPath()

            val runtimeBundlePath = runtimeBundle.path!!.toPath()
            log.info("Runtime bundle path [${runtimeBundlePath}")

            if (runtimeBundlePath.toAbsolutePath().startsWith(bundleInstallationDirectory.toAbsolutePath())) {
                log.info("Skipping self-installation, already running from bundle installation path")
                return@task
            }

            log.info("Performing self verification")
            runtimeBundle.verify()

            val srcPath = runtimeBundlePath
            val destPath = File(storage.bundleInstallationDirectory, BundleType.LEOZ_BOOT.value)

            val rc = RsyncClient()
            val source = Rsync.URI(srcPath)
            val destination = Rsync.URI(destPath)
            rc.delete = true
            rc.preserveExecutability = true
            rc.preservePermissions = false

            log.info("Synchronizing [${source}] -> [${destination}]")
            rc.sync(source, destination,
                    onFile = { r ->
                        log.info("Updating [${r.flags}] [${r.path}]")
                    },
                    onProgress = { pr ->
                        onNext(Event(pr.percentage.toDouble() / 100))
                    })
            onNext(Event(100.0))
        }
    }

    /**
     * Uninstall bundle
     */
    fun uninstall(bundleName: String): Observable<Event> {
        return task<Event> {
            if (Strings.isNullOrEmpty(bundleName))
                throw IllegalArgumentException("Missing or empty bundle parameter. Nothing to do, exiting")

            this.installer.uninstall(bundleName)
        }
    }

    /**
     * Install bundle
     */
    fun install(bundleName: String,
                forceDownload: Boolean,
                versionAlias: String? = null,
                versionPattern: String? = null,
                productive: Boolean = false): Observable<Event> {
        return task<Event> { onNext ->
            if (Strings.isNullOrEmpty(bundleName))
                throw IllegalArgumentException("Missing or empty bundle parameter. Nothing to do, exiting")

            val requiresDownload = !this.installer.hasBundle(bundleName) || forceDownload

            if (requiresDownload && versionAlias == null && versionPattern == null)
                throw IllegalArgumentException("Either version alias or pattern must be provided")

            if (requiresDownload) {
                val finalVersionPattern: String
                if (versionPattern != null) {
                    finalVersionPattern = versionPattern
                } else {
                    val restClient: RestClient = Kodein.global.instance()
                    val bundleService = restClient.proxy(BundleServiceV2::class.java)

                    val updateInfo = bundleService.info(bundleName = bundleName, versionAlias = versionAlias)
                    log.info("${updateInfo}")

                    finalVersionPattern = updateInfo.bundleVersionPattern
                }

                val repository: BundleRepository = Kodein.global.instance()

                // Query for version matching pattern
                val version = repository.queryLatestMatchingVersion(
                        bundleName,
                        finalVersionPattern)

                val platforms = repository.listPlatforms(bundleName, version)
                if (!platforms.contains(PlatformId.current()))
                    throw NoSuchElementException("Bundle not available for this platform [${PlatformId.current()}]")

                // Download bundle
                this.installer.download(
                        bundleRepository = repository,
                        bundleName = bundleName,
                        version = version,
                        forceDownload = forceDownload,
                        onProgress = { _, p ->
                            onNext(Event(p))
                        }
                )
            }

            onNext(Event(-1.0))

            this.installer.install(
                    bundleName = bundleName,
                    productive = productive)

            onNext(Event(100.0))
        }
                // Eliminate duplicate progress updates
                .distinctUntilChanged()
    }

    /**
     * Compound boot task
     * @param settings Boot task settings
     */
    fun boot(settings: Settings): Observable<Event> {
        val discoveryTask: Observable<Any>
        // Discovery task
        if (!settings.rsyncHost.isNullOrEmpty() && !settings.httpHost.isNullOrEmpty()) {
            log.info("Skipping discovery")
            discoveryTask = Observable.empty()
        } else {
            discoveryTask = (if (settings.discover)
                this.discover().toObservable()
            else
                Observable.empty())
                    .concatWith(Observable.fromCallable {
                        if (settings.httpHost != null) {
                            restConfiguration.host = settings.httpHost!!
                            restConfiguration.https = settings.https
                        }

                        if (settings.rsyncHost != null)
                            bundleConfiguration.rsyncHost = settings.rsyncHost!!
                    })
        }

        // Main task
        val mainTask = if (settings.uninstall)
            this.uninstall(
                    bundleName = settings.bundle)
        else
            this.install(
                    bundleName = settings.bundle,
                    forceDownload = settings.forceDownload,
                    versionAlias = settings.versionAlias,
                    versionPattern = settings.versionPattern,
                    productive = settings.productive)

        // Concat tasks and skew progress events
        return Observable.concat(
                Observable.mergeDelayError(
                        discoveryTask.ignoreElements().toObservable(),
                        this.selfInstall()
                                .map {
                                    Boot.Event(this.skewProgress(0.0, 0.3, it.progress))
                                }
                ),
                mainTask
                        .map {
                            Boot.Event(this.skewProgress(0.3, 1.0, it.progress))
                        })
                .doAfterTerminate {
                    // Cleanup
                    sshTunnelProvider.close()
                }
    }
}