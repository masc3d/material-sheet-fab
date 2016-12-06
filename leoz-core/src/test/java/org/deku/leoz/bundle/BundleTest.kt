package org.deku.leoz.bundle

import org.deku.leoz.config.BundleTestConfiguration
import org.junit.Assert
import org.junit.Test
import sx.platform.CpuArch
import sx.platform.OperatingSystem
import sx.platform.PlatformId

/**
 * Created by masc on 24.08.15.
 */
class BundleTest {
    val versions = arrayListOf(
            Bundle.Version.parse("1.0.1-FINAL"),
            Bundle.Version.parse("1.3.1-RELEASE"),
            Bundle.Version.parse("1.3.1-FINAL"),
            Bundle.Version.parse("1.5"),
            Bundle.Version.parse("2.4.1-FINAL"))

    @Test
    fun testVersion() {
        var v: Bundle.Version

        v = Bundle.Version.parse("1.0.1-FINAL")
        assert(v.components[0] == 1 && v.components[1] == 0 && v.components[2] == 1 && v.suffix == "FINAL")

        v = Bundle.Version.parse("1.0.10")
        assert(v.components[0] == 1 && v.components[1] == 0 && v.components[2] == 10 && v.suffix == "")
    }

    @Test
    fun testVersionSorting() {
        for (v in versions.sorted())
            println(v)
    }

    @Test
    fun testVersionMatching() {
        var result = this.versions.filter("2.+")
        Assert.assertTrue(result.count() > 0)
        Assert.assertTrue(result.all { v -> v.components[0] == 2 })

        result = this.versions.filter("+RELEASE")
        Assert.assertTrue(result.count() > 0)
    }

    @Test
    fun testCreate() {
        var path = BundleTestConfiguration.releasePath.resolve(PlatformId.current().toString())

        Bundle.create(path, BundleType.LEOZ_BOOT.value, PlatformId.current(), Bundle.Version.parse("0.1"))
    }

    @Test
    fun testLoad() {
        var path = BundleTestConfiguration.releasePath.resolve(PlatformId(OperatingSystem.WINDOWS, CpuArch.X64).toString())

        Bundle.load(path)
    }

    @Test
    fun testVerify() {
        var path = BundleTestConfiguration.releasePath.resolve(PlatformId.current().toString())

        Bundle.load(path).verify()
    }
}