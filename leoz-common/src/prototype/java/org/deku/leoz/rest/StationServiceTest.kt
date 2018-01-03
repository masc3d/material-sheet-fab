package org.deku.leoz.rest

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import org.deku.leoz.config.RestClientTestFactory
import org.deku.leoz.service.internal.StationService
import org.junit.Test
import org.junit.experimental.categories.Category
import org.slf4j.LoggerFactory

/**
 * Created by n3 on 10/12/2016.
 */
@Category(sx.junit.PrototypeTest::class)
class StationServiceTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val kodein = Kodein {
        import(RestClientTestFactory.module)
    }

    @Test
    fun testGet() {
        val stationService: StationService = kodein.instance()

        val stations = stationService.get()
        log.info("Received ${stations.count()} stations")
    }
}