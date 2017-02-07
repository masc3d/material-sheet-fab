package org.deku.leoz.rest

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import feign.Feign
import org.deku.leoz.config.FeignRestClientConfiguration
import org.deku.leoz.config.FeignRestClientConfiguration.Companion.target
import org.deku.leoz.rest.service.internal.v1.BundleService
import org.junit.Test
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream

/**
 * BundleService test
 * Created by masc on 02/02/2017.
 */
class BundleServiceTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val kodein = Kodein {
        import(FeignRestClientConfiguration.module)
    }

    init {
        Kodein.global.addExtend(this.kodein)
        val config: FeignRestClientConfiguration = this.kodein.instance()
    }

    @Test
    fun testInfo() {
        val bundleService: BundleService = Kodein.global.instance()

        val info = bundleService.info(
                bundleName = "leoz-boot",
                versionAlias = "release")

        log.info("${info}")
    }
}