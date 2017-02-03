package org.deku.leoz.rest

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import org.deku.leoz.config.RestFeignClientConfiguration
import org.deku.leoz.rest.service.internal.v1.BundleService
import org.junit.Test
import org.slf4j.LoggerFactory

/**
 * Created by masc on 02/02/2017.
 */
class BundleServiceTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        init {
            Kodein.global.addImport(RestFeignClientConfiguration.module)

            val config: RestFeignClientConfiguration = Kodein.global.instance()
            config.sslValidation = false
            config.url = "https://leoz-dev:13000/rs/api"
        }
    }

    @Test
    fun testInfo() {
        val bundleService: BundleService = Kodein.global.instance()

        val info = bundleService.info(
                bundleName = "leoz-boot",
                versionAlias = "release")

        log.info("${info}")
    }

    @Test
    fun testDownload() {
        val bundleService: BundleService = Kodein.global.instance()

        bundleService.donwload("leoz-mobile", version = "0.1-SNAPSHOT")
    }
}