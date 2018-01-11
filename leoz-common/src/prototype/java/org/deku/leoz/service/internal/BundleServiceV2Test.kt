package org.deku.leoz.service.internal

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import org.deku.leoz.config.RestClientTestConfiguration
import org.junit.Test
import org.junit.experimental.categories.Category
import org.slf4j.LoggerFactory
import sx.rs.client.FeignClient
import java.io.ByteArrayOutputStream

/**
 * BundleService test
 * Created by masc on 02/02/2017.
 */
@Category(sx.junit.PrototypeTest::class)
class BundleServiceV2Test {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val kodein = Kodein {
        import(RestClientTestConfiguration.module)
    }

    @Test
    fun testDownload() {
        // For binary response stream, need to build target manually, so we can inject a decoder implementation
        val feignClientProxy = kodein.instance<FeignClient>()

        val bundleService: BundleServiceV2 = feignClientProxy.target(
                apiType = BundleServiceV2::class.java,
                output = ByteArrayOutputStream(),
                progressCallback = { p: Float, bytesCopied: Long ->
                    log.debug("Progress ${"%.2f".format(p)}% ${bytesCopied}")
                })

        bundleService.download("leoz-mobile", "0.1-SNAPSHOT")
    }

    @Test
    fun testInfo() {
        val bundleServiceV1: BundleServiceV1 = kodein.instance()

        log.info(bundleServiceV1.info(
                "leoz-boot",
                "release").toString())
    }
}