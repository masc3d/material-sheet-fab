package org.deku.leoz.rest

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import feign.Feign
import feign.Response
import org.deku.leoz.config.RestFeignClientConfiguration
import org.deku.leoz.rest.service.internal.v1.BundleService
import org.junit.Test
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Type

/**
 * BundleService test
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
        // For binary response stream, need to build target manually, so we can inject a decoder implementation
        val feignConfig: RestFeignClientConfiguration = Kodein.global.instance()
        val feignBuilder: Feign.Builder = Kodein.global.instance()

        val target = feignBuilder
                .decoder(object : feign.codec.Decoder {
                    override fun decode(response: feign.Response, type: Type): Any? {
                        val body = response.body()

                        val stream = body.asInputStream()
                        val length = body.length()
                        log.debug("BODY LENGTH ${length}")

                        var bytesCopied: Long = 0
                        val buffer = ByteArray(8192)
                        var bytes = stream.read(buffer)
                        while (bytes >= 0) {
                            log.debug("READ ${bytes}")
                            //out.write(buffer, 0, bytes)
                            bytesCopied += bytes
                            bytes = stream.read(buffer)
                        }
                        log.debug("TOTAL ${bytesCopied}")
                        return null
                    }
                })
                .target(BundleService::class.java, feignConfig.url)

        target.download("leoz-mobile", "0.1-SNAPSHOT")
    }
}