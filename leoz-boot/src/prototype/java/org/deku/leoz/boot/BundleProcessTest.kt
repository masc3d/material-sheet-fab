package org.deku.leoz.boot

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import org.deku.leoz.Storage
import org.deku.leoz.boot.config.BundleConfiguration
import org.deku.leoz.boot.config.StorageConfiguration
import org.deku.leoz.bundle.BundleType
import org.junit.Before
import org.junit.Test
import sx.packager.Bundle
import sx.packager.BundleInstaller
import sx.packager.stop
import java.io.File
import java.nio.file.Files

/**
 * Bundle process prototype tests
 * Created by n3 on 24.04.2017.
 */
class BundleProcessTest {
    val storage: Storage by Kodein.global.lazy.instance()

    val bundleIntaller: BundleInstaller by Kodein.global.lazy.instance()

    @Before
    fun initialize() {
        Kodein.global.addImport(StorageConfiguration.module)
        Kodein.global.addImport(BundleConfiguration.module)
    }

    @Test
    fun testSynchronousStop() {
        val bundlePath = this.bundleIntaller.bundlePath(BundleType.LEOZ_CENTRAL.value)
        val oldBundlePath = File(bundlePath.parentFile, BundleType.LEOZ_CENTRAL.value + ".old")

        println("${bundlePath} ${oldBundlePath}")
        val bundle = Bundle.load(bundlePath)

        bundle.stop()
        Files.move(bundlePath.toPath(), oldBundlePath.toPath())
        Files.move(oldBundlePath.toPath(), bundlePath.toPath())
        //bundle.start()
    }
}