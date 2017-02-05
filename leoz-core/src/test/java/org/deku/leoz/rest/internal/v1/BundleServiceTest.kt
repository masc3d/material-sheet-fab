package org.deku.leoz.rest.internal.v1

import org.deku.leoz.rest.WebserviceTest
import org.deku.leoz.rest.service.internal.v1.BundleService
import org.junit.Test
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.InputStream

/**
 * Created by masc on 05/02/2017.
 */
class BundleServiceTest : WebserviceTest() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Test
    fun testInfo() {
        log.info(this.getService(BundleService::class.java).info(
                "leoz-boot",
                "release").toString())
    }

    @Test
    fun testDownload() {
        val response = this.getService(BundleService::class.java).download(
                "leoz-mobile",
                "0.1-SNAPSHOT")

        val stream = response.readEntity(InputStream::class.java)
        val bstream = ByteArrayOutputStream()
        stream.copyTo(bstream)
        log.info("Received ${bstream.size()} bytes")
    }
}