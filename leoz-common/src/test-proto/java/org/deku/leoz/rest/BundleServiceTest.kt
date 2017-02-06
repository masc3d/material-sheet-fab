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

    companion object {
        init {
            Kodein.global.addImport(FeignRestClientConfiguration.module)

            val config: FeignRestClientConfiguration = Kodein.global.instance()
            config.sslValidation = false
            config.url = "https://leoz-dev:13000/rs/api"
        }
    }

    @Test
    fun testDownload() {
        // For binary response stream, need to build target manually, so we can inject a decoder implementation
        val feignBuilder: Feign.Builder = Kodein.global.instance()

        val bundleService: BundleService = feignBuilder.target(
                apiType = BundleService::class.java,
                output = ByteArrayOutputStream(),
                progressCallback = { p: Float, bytesCopied: Long ->
                    log.debug("Progress ${"%.2f".format(p)}% ${bytesCopied}")
                })

        bundleService.download("leoz-mobile", "0.1-SNAPSHOT")
    }
}