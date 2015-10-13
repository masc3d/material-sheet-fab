package org.deku.leoz.bundle

import org.deku.leoz.config.TestBundleConfiguration
import org.junit.Test
import sx.platform.PlatformId

/**
 * Created by masc on 24.08.15.
 */
class BundleTest {
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
        var l = arrayListOf(
                Bundle.Version.parse("1.0.1-FINAL"),
                Bundle.Version.parse("1.3.1-RELEASE"),
                Bundle.Version.parse("1.3.1-FINAL"),
                Bundle.Version.parse("1.5"),
                Bundle.Version.parse("2.4.1-FINAL")
        )

        for (v in l.sorted())
            println(v)
    }


    @Test
    fun testCreate() {
        var path = TestBundleConfiguration.path.resolve(PlatformId.current().toString())

        Bundle.create(path.toFile(), Bundles.LEOZ_BOOT, PlatformId.current(), Bundle.Version.parse("0.1"))
    }

    @Test
    fun testLoad() {
        var path = TestBundleConfiguration.path.resolve(PlatformId.current().toString())

        Bundle.load(path.toFile())
    }

    @Test
    fun testVerify() {
        var path = TestBundleConfiguration.path.resolve(PlatformId.current().toString())

        Bundle.load(path.toFile()).verify()
    }
}