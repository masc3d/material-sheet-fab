package org.deku.leoz.smartlane.socketio

import org.junit.Test
import org.junit.experimental.categories.Category
import java.net.URI

/**
 * Created by masc on 12.11.17.
 */
@Category(sx.junit.PrototypeTest::class)
class PingServiceTest {
    private val pingService by lazy {
        PingService(URI.create("https://dispatch.smartlane.io/der-kurier-test"))
    }

    @Test
    fun testPing() {
        this.pingService.ping()
        Thread.sleep(10000)
    }
}