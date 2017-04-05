package sx.packager

import org.junit.Assert
import org.junit.Test
import org.slf4j.LoggerFactory
import sx.packager.Bundle
import sx.packager.config.BundleTestConfiguration
import sx.packager.filter
import sx.platform.CpuArch
import sx.platform.OperatingSystem
import sx.platform.PlatformId
import java.io.File

/**
 * Bundle tests
 * Created by masc on 24.08.15.
 */
class BundlePrototypeTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Test
    fun testCreate() {
        var path = BundleTestConfiguration.releasePath.resolve(PlatformId.current().toString())

        Bundle.create(path, "leoz-boot", PlatformId.current(), Bundle.Version.parse("0.1"))
    }

    @Test
    fun testLoad() {
        var path = BundleTestConfiguration.releasePath.resolve(PlatformId(OperatingSystem.WINDOWS, CpuArch.X64).toString())
    }

    @Test
    fun testVerify() {
        var path = BundleTestConfiguration.releasePath.resolve(PlatformId.current().toString())

        Bundle.load(path).verify()
    }
}