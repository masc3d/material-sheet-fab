package org.deku.leoz.boot

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.google.common.base.Strings
import org.deku.leoz.boot.config.RestConfiguration
import org.deku.leoz.boot.config.StorageConfiguration
import org.deku.leoz.bundle.Bundle
import org.deku.leoz.bundle.BundleInstaller
import org.deku.leoz.bundle.BundleRepository
import org.deku.leoz.bundle.BundleType
import org.deku.leoz.boot.config.BundleConfiguration
import org.deku.leoz.rest.RestClient
import org.deku.leoz.rest.service.internal.v1.BundleService
import org.deku.leoz.service.discovery.DiscoveryService
import org.slf4j.LoggerFactory
import rx.Observable
import rx.lang.kotlin.cast
import rx.lang.kotlin.subscribeWith
import rx.util.async.Async
import sx.rsync.Rsync
import sx.rsync.RsyncClient
import sx.rx.*
import java.io.File
import java.time.Duration

/**
 * Boot model
 * Created by masc on 08/11/2016.
 */
class Boot {
    private val log = LoggerFactory.getLogger(this.javaClass)

    // Injections
    private val storageConfiguration: StorageConfiguration by Kodein.global.lazy.instance()
    private val restConfiguration: RestConfiguration by Kodein.global.lazy.instance()
    private val bundleConfiguration: BundleConfiguration by Kodein.global.lazy.instance()
    private val installer: BundleInstaller by Kodein.global.lazy.instance()
    private val discoveryService: DiscoveryService by Kodein.global.lazy.instance()

    /**
     * Event
     */
    data class Event(
            val progress: Double
    )

    class DiscoveryException(e: Throwable) : Exception(e) {}
    class InstallationException(e: Throwable) : Exception(e) {}

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
    fun discover(): Observable<Any> {
        return discoveryService.discoverFirst(
                predicate = {
                    it.bundleType == BundleType.LEOZ_NODE
                },
                timeout = Duration.ofSeconds(2))
                .doOnNext {
                    val host = it.address.hostAddress.toString()

                    // Update REST configuration. This will affect all subsequent REST client invocations
                    log.info("Setting REST host based on discovery to ${host}")
                    restConfiguration.httpHost = host
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
            if (storageConfiguration.nativeBundleBasePath == null) {
                log.warn("Skipping self-installation as native bundle base path could not be determined (not running from native bundle)")
                return@task
            }

            val nativeBundlePath = storageConfiguration.nativeBundleBasePath!!
            log.info("Native bundle path [${nativeBundlePath}")

            if (nativeBundlePath.parentFile == storageConfiguration.bundleInstallationDirectory)
                return@task

            log.info("Performing self verification")
            Bundle.load(nativeBundlePath).verify()

            val srcPath = nativeBundlePath
            val destPath = File(storageConfiguration.bundleInstallationDirectory, BundleType.LEOZ_BOOT.value)

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
                versionPattern: String? = null): Observable<Event> {
        return task<Event> { onNext ->
            if (Strings.isNullOrEmpty(bundleName))
                throw IllegalArgumentException("Missing or empty bundle parameter. Nothing to do, exiting")

            if (versionAlias == null && versionPattern == null)
                throw IllegalArgumentException("Either version alias or pattern must be provided")

            val finalVersionPattern: String
            if (versionPattern != null) {
                finalVersionPattern = versionPattern
            } else {
                val restClient: RestClient = Kodein.global.instance()
                val bundleService = restClient.proxy(BundleService::class.java)

                val updateInfo = bundleService.info(bundleName = bundleName, versionAlias = versionAlias)
                log.info("${updateInfo}")

                finalVersionPattern = updateInfo.bundleVersionPattern
            }

            if (!this.installer.hasBundle(bundleName) || forceDownload) {
                val repository: BundleRepository = Kodein.global.instance()

                // Query for version matching pattern
                val version = repository.queryLatestMatchingVersion(
                        bundleName,
                        finalVersionPattern)

                // Download bundle
                this.installer.download(
                        bundleRepository = repository,
                        bundleName = bundleName,
                        version = version,
                        forceDownload = forceDownload,
                        onProgress = { f, p ->
                            onNext(Event(p))
                        }
                )
            }

            onNext(Event(-1.0))

            this.installer.install(bundleName)

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
                this.discover()
            else
                Observable.empty())
                    .concatWith(Observable.fromCallable {
                        if (settings.httpHost != null)
                            restConfiguration.httpHost = settings.httpHost!!

                        if (settings.https != null)
                            restConfiguration.https = settings.https!!

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
                    versionPattern = settings.versionPattern)

        // Concat tasks and skew progress events
        return Observable.concat(
                Observable.mergeDelayError(
                        discoveryTask.ignoreElements().cast(),
                        this.selfInstall()
                                .map {
                                    Boot.Event(this.skewProgress(0.0, 0.3, it.progress))
                                }
                ),
                mainTask
                        .map {
                            Boot.Event(this.skewProgress(0.3, 1.0, it.progress))
                        })
    }
}