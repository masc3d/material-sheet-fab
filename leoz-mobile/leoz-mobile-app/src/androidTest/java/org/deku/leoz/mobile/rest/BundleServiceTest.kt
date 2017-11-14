package org.deku.leoz.mobile.rest

import android.support.test.runner.AndroidJUnit4
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import org.deku.leoz.mobile.WebserviceTest
import org.deku.leoz.service.internal.BundleServiceV2
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import sx.rs.client.FeignClient
import java.io.ByteArrayOutputStream

/**
 * BundleService test
 * Created by masc on 02/02/2017.
 */
@RunWith(AndroidJUnit4::class)
class BundleServiceTest : WebserviceTest() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Test
    fun testInfo() {
        val bundleService: BundleServiceV2 = Kodein.global.instance()

        val info = bundleService.info(
                bundleName = "leoz-boot",
                versionAlias = "release")

        log.info("${info}")
    }

    @Test
    fun testDownload() {
        // For binary response stream, need to build target manually, so we can inject a decoder implementation
        val feignClient: FeignClient = Kodein.global.instance()

        val bundleService: BundleServiceV2 = feignClient.target(
                apiType = BundleServiceV2::class.java,
                output = ByteArrayOutputStream(),
                progressCallback = { p: Float, bytesCopied: Long ->
                    log.debug("Progress ${"%.2f".format(p)}% ${bytesCopied}")
                })

        bundleService.download("leoz-mobile", "0.1-SNAPSHOT")
    }
}