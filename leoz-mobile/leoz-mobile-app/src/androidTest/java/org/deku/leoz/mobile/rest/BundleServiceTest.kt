package org.deku.leoz.mobile.rest

import android.support.test.runner.AndroidJUnit4
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import feign.Feign
import feign.Response
import feign.codec.Decoder
import org.deku.leoz.config.FeignRestClientConfiguration.Companion.target
import org.deku.leoz.mobile.WebserviceTest
import org.deku.leoz.rest.service.internal.v1.BundleService
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.lang.reflect.Type

/**
 * BundleService test
 * Created by masc on 02/02/2017.
 */
@RunWith(AndroidJUnit4::class)
class BundleServiceTest : WebserviceTest() {
    private val log = LoggerFactory.getLogger(this.javaClass)

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