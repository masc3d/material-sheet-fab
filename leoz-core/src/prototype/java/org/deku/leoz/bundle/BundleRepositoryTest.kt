package org.deku.leoz.bundle

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import io.reactivex.Observable
import io.reactivex.rxkotlin.merge
import io.reactivex.schedulers.Schedulers
import org.apache.commons.lang3.SystemUtils
import org.deku.leoz.config.BundleConfiguration
import org.deku.leoz.config.RsyncTestConfiguration
import org.junit.Test
import org.slf4j.LoggerFactory
import sx.packager.Bundle
import sx.platform.CpuArch
import sx.platform.OperatingSystem
import sx.platform.PlatformId
import sx.rsync.Rsync
import sx.rx.limit
import java.io.File

/**
 * Created by masc on 05.04.17.
 */
class BundleRepositoryTest {
    companion object {
        init {
            Kodein.global.addImport(RsyncTestConfiguration.module)
        }
    }

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Test
    fun testConcurrentDownloads() {
        val BUNDLE_NAME = BundleType.LEOZ_BOOT.value
        val HOST = "leoz-t1.derkurier.de"
        val DOWNLOADS = 16
        val DOWNLOADS_CONCURRENCY = 4

        val repository = BundleConfiguration.createRepository(HOST)

        val rootPath = File("build")
                .resolve("download")

        val scheduler = Schedulers.io().limit(DOWNLOADS_CONCURRENCY)

        try {
            (0..DOWNLOADS).map { i ->
                Observable.fromCallable {
                    val path = rootPath.resolve("${BUNDLE_NAME}-${i}")

                    try {
                        log.trace("START ${path}")
                        path.mkdirs()

                        repository.download(
                                bundleName = BUNDLE_NAME,
                                version = repository.listVersions(BUNDLE_NAME).first(),
                                platformId = PlatformId(OperatingSystem.WINDOWS, CpuArch.X64),
                                destPath = path
                        )
                    } finally {
                        log.trace("Removing ${path}")
                        path.deleteRecursively()
                    }
                }
                        .subscribeOn(scheduler)
            }
                    .merge()
                    .blockingIterable()
                    .toList()
        } finally {
            println("Removing ${rootPath}")
            rootPath.deleteRecursively()
        }
    }
}