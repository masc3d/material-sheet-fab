package org.deku.leoz.smartlane

import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category
import org.slf4j.LoggerFactory
import sx.log.slf4j.installJulBridge
import java.net.URI
import java.util.concurrent.TimeUnit

/**
 * Created by masc on 12.11.17.
 */
@Category(sx.junit.PrototypeTest::class)
class PingServiceTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    init {
        log.installJulBridge()
    }

    private val pingService by lazy {
        PingService(URI.create("https://dispatch.smartlane.io/der-kurier-test/"))
    }

    @Test
    fun testPing() {
        Assert.assertTrue("Ping timeout",
                this.pingService
                        .ping()
                        .blockingAwait(3, TimeUnit.SECONDS)
        )
    }
}