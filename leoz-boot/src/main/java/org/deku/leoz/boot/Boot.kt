package org.deku.leoz.boot

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.google.common.base.Strings
import org.deku.leoz.boot.config.StorageConfiguration
import org.deku.leoz.bundle.Bundle
import org.deku.leoz.bundle.BundleInstaller
import org.deku.leoz.bundle.BundleRepository
import org.deku.leoz.bundle.BundleType
import org.deku.leoz.config.BundleConfiguration
import org.slf4j.LoggerFactory
import rx.Observable
import sx.JarManifest
import sx.platform.JvmUtil
import sx.rsync.Rsync
import sx.rsync.RsyncClient
import sx.rx.task
import java.io.File

/**
 * Boot model
 * Created by masc on 08/11/2016.
 */
class Boot {
    private val log = LoggerFactory.getLogger(this.javaClass)

    // Injections
    private val storage: StorageConfiguration by Kodein.global.lazy.instance()
    private val installer: BundleInstaller by Kodein.global.lazy.instance()

    /**
     * Event
     */
    data class Event(
            val progress: Double
    )

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
            this.installer.uninstall(bundleName)
        }
    }

    /**
     * Boot the bundle
     */
    fun installTask(bundleName: String, forceDownload: Boolean, versionAlias: String, versionPattern: String, bundleRepositoryUri: String? = null): Observable<Event> {
        return task<Event> { onNext ->
            if (Strings.isNullOrEmpty(bundleName)) {
                // Nothing to do
                throw IllegalArgumentException("Missing or empty bundle parameter. Nothing to do, exiting");
            }

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
                // Eliminate duplicate consecutive updates
                .distinctUntilChanged()
    }
}