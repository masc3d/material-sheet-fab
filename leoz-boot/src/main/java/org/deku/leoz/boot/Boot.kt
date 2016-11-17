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
import org.deku.leoz.config.BundleConfiguration
import org.deku.leoz.service.discovery.DiscoveryService
import org.slf4j.LoggerFactory
import rx.Observable
import rx.lang.kotlin.cast
import sx.rsync.Rsync
import sx.rsync.RsyncClient
import sx.rx.task
import java.io.File
import java.time.Duration

/**
 * Boot model
 * Created by masc on 08/11/2016.
 */
class Boot {
    private val log = LoggerFactory.getLogger(this.javaClass)

    // Injections
    private val storage: StorageConfiguration by Kodein.global.lazy.instance()
    private val installer: BundleInstaller by Kodein.global.lazy.instance()
    private val discoveryService: DiscoveryService by Kodein.global.lazy.instance()
    private val restConfiguration: RestConfiguration by Kodein.global.lazy.instance()

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
        val range = endProgress - startProgress
        return startProgress + (range * progress)
    }

    /**
     * Discovers leoz-node in local network and amends configurations to connect to this host
     */
    fun discoverTask(): Observable<Any> {
        return discoveryService.discoverFirstTask(
                predicate = {
                    it.bundleType == BundleType.LEOZ_NODE
                },
                timeout = Duration.ofSeconds(2))
                .doOnNext {
                    val httpHost = it.address.toString()
                    log.info("Setting REST host based on discovery to ${httpHost}")
                    restConfiguration.httpHost = httpHost
                    restConfiguration.https = false
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
    fun selfInstallTask(): Observable<Event> {
        return task<Event> { onNext ->
            if (storage.nativeBundleBasePath == null) {
                log.warn("Skipping self-installation as native bundle base path could not be determined (not running from native bundle)")
                return@task
            }

            val nativeBundlePath = storage.nativeBundleBasePath!!
            log.info("Native bundle path [${nativeBundlePath}")

            if (nativeBundlePath.parentFile == storage.bundleInstallationDirectory)
                return@task

            log.info("Performing self verification")
            Bundle.load(nativeBundlePath).verify()

            val srcPath = nativeBundlePath
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
    fun uninstallTask(bundleName: String): Observable<Event> {
        return task<Event> {
            if (Strings.isNullOrEmpty(bundleName))
                throw IllegalArgumentException("Missing or empty bundle parameter. Nothing to do, exiting")

            this.installer.uninstall(bundleName)
        }
    }

    /**
     * Install bundle
     */
    fun installTask(bundleName: String,
                    forceDownload: Boolean,
                    discover: Boolean = false,
                    versionAlias: String,
                    versionPattern: String = "",
                    bundleRepositoryUri: String? = null): Observable<Event> {
        return task<Event> { onNext ->
            if (Strings.isNullOrEmpty(bundleName))
                throw IllegalArgumentException("Missing or empty bundle parameter. Nothing to do, exiting")

            if (!this.installer.hasBundle(bundleName) || forceDownload) {
                val repository = if (bundleRepositoryUri != null)
                    BundleRepository(Rsync.URI(bundleRepositoryUri)) else
                    BundleConfiguration.stagingRepository

                // Query for version matching pattern
                val version = repository.queryLatestMatchingVersion(
                        bundleName,
                        versionPattern)

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

            this.installer.install(bundleName)

            onNext(Event(100.0))
        }
                // Eliminate duplicate progress updates
                .distinctUntilChanged()
    }

    /**
     * Compound boot task
     */
    fun bootTask(bundleName: String,
                 discover: Boolean = false,
                 forceDownload: Boolean = false,
                 uninstall: Boolean = false,
                 versionAlias: String,
                 versionPattern: String = ""): Observable<Event> {

        // Discovery task
        val discoveryTask = if (discover) this.discoverTask() else Observable.empty()

        // Main task
        val mainTask = if (uninstall)
            this.uninstallTask(
                    bundleName = bundleName)
        else
            this.installTask(
                    bundleName = bundleName,
                    forceDownload = forceDownload,
                    versionAlias = versionAlias,
                    versionPattern = versionPattern ?: "")

        // Concat tasks and skew progress events
        return Observable.concat(
                Observable.mergeDelayError(
                        discoveryTask.ignoreElements().cast(),
                        this.selfInstallTask()
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