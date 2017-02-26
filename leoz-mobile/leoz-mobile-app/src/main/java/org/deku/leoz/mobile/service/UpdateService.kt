package org.deku.leoz.mobile.update

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import feign.Feign
import feign.FeignException
import org.deku.leoz.config.FeignRestClientConfiguration.Companion.target
import org.deku.leoz.mobile.BuildConfig
import org.deku.leoz.mobile.model.Storage
import org.deku.leoz.rest.service.internal.v1.BundleService
import org.slf4j.LoggerFactory
import rx.lang.kotlin.BehaviorSubject
import rx.lang.kotlin.PublishSubject
import rx.lang.kotlin.synchronized
import sx.android.ApplicationPackage
import sx.util.zip.verify
import sx.concurrent.Service
import sx.time.Duration
import sx.time.seconds
import java.io.File
import java.io.FileFilter
import java.io.FileOutputStream
import java.net.ConnectException
import java.util.concurrent.ScheduledExecutorService
import java.util.zip.ZipFile

/**
 * Application/APK update service
 * Created by masc on 10/02/2017.
 */
class UpdateService(
        executorService: ScheduledExecutorService,
        val bundleName: String,
        val versionAlias: String,
        period: Duration
) : Service(
        executorService = executorService,
        initialDelay = 1.seconds,
        period = period
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Application package name
     */
    class ApplicationPackageName(val bundleName: String, val version: String) {
        companion object {
            private val regex = Regex("^([-a-zA-Z]+)-([0-9]+[-0-9a-zA-Z\\.]+)\\.apk$")

            fun parse(name: String): ApplicationPackageName {
                val matchResult = regex.find(name)

                if (matchResult == null)
                    throw IllegalArgumentException("Invalid application package name [${name}]")

                return ApplicationPackageName(
                        bundleName = matchResult.groups.get(1)!!.value,
                        version = matchResult.groups.get(2)!!.value)
            }
        }

        override fun toString(): String {
            return "${bundleName}-${version}.apk"
        }
    }

    private val storage: Storage by Kodein.global.lazy.instance()
    private val bundleService: BundleService by Kodein.global.lazy.instance()

    /**
     * Temporary suffix of files currently being downloaded
     */
    private val DOWNLOAD_SUFFIX = ".download"

    /**
     * Forces (re-)download and update, even if applcation is up-to-date
     */
    var force = false

    /**
     * Update service working/download directory
     */
    private val workingDir: File by lazy {
        val dir = this.storage.cacheDir.resolve("apk")
        dir.mkdirs()
        // Make sure system can access this path for installation
        dir.setExecutable(true, false)
        dir.setReadable(true, false)
        dir
    }

    /**
     * Available update or null
     */
    var availableUpdate: ApplicationPackage? = null
        private set


    data class AvailableUpdateEvent(val apk: ApplicationPackage, val version: String)

    /**
     * Available update event (behaviour subject, most recent event will be fire on registration)
     */
    val availableUpdateEvent by lazy { this.availableUpdateEventSubject.asObservable() }
    private val availableUpdateEventSubject by lazy { BehaviorSubject<AvailableUpdateEvent>().synchronized() }

    override fun onStart() {
        super.onStart()
    }

    override fun run() {
        log.info("Update cycle")

        try {
            val updateInfo = this.bundleService.info(this.bundleName, this.versionAlias)
            log.info("${updateInfo}")

            // Cleanup
            this.workingDir.listFiles(FileFilter {
                try {
                    val apk = ApplicationPackageName.parse(it.name)
                    (apk.bundleName != updateInfo.bundleName || apk.version != updateInfo.latestDesignatedVersion!!)
                } catch(e: Throwable) {
                    false
                }
            }).forEach {
                log.info("Removing [${it}]")
                it.delete()
            }

            if (!force && updateInfo.latestDesignatedVersion == BuildConfig.VERSION_NAME) {
                log.info("Application is already up-to-date")
                return
            }

            val apkName = ApplicationPackageName(
                    bundleName = updateInfo.bundleName,
                    version = updateInfo.latestDesignatedVersion!!)

            val apkFile = File(this.workingDir, apkName.toString())
            val downloadFile = File(apkFile.parent, "${apkFile.name}${DOWNLOAD_SUFFIX}")

            if (force || !apkFile.exists()) {
                log.info("Downloading bundle [${downloadFile}]")

                // For binary response stream, need to build target manually, so we can inject a decoder implementation
                val feignBuilder: Feign.Builder = Kodein.global.instance()

                val bundleService: BundleService = feignBuilder.target(
                        apiType = BundleService::class.java,
                        output = FileOutputStream(downloadFile),
                        progressCallback = { p: Float, bytesCopied: Long ->
                            log.debug("Progress ${"%.2f".format(p)}% ${bytesCopied}")
                        })

                bundleService.download(
                        bundleName = updateInfo.bundleName,
                        version = updateInfo.latestDesignatedVersion!!)

                // Verify downloaded apk
                log.info("Verifying [${downloadFile}]")
                val zipFile = ZipFile(downloadFile)
                try {
                    zipFile.verify()
                } catch(e: Throwable) {
                    // Remove invalid archive
                    downloadFile.delete()
                    throw e
                }

                downloadFile.renameTo(apkFile)
            }


            // Clean up incomplete downloads from previous runs
            this.workingDir.listFiles(FileFilter {
                it.name.endsWith(DOWNLOAD_SUFFIX)
            }).forEach {
                log.info("Removing [${it}]")
                it.delete()
            }

            val apk = ApplicationPackage(file = apkFile)
            log.info("Update available [${apk.file}]")

            this.availableUpdate = apk
            this.availableUpdateEventSubject.onNext(AvailableUpdateEvent(apk, apkName.version))
        } catch(e: Exception) {
            when (e) {
                is FeignException -> log.error(e.message)
                else -> {
                    when (e.cause) {
                        is ConnectException -> log.error(e.message)
                        else -> log.error(e.message, e)
                    }
                }
            }
        }
    }
}