package org.deku.leoz.rest

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import org.deku.leoz.service.internal.v1.StationService
import org.junit.Test
import org.slf4j.LoggerFactory
import org.deku.leoz.config.RestClientTestConfiguration

/**
 * Created by n3 on 10/12/2016.
 */
class StationServiceTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val kodein = Kodein {
        import(RestClientTestConfiguration.module)
    }

    @Test
    fun testGet() {
        val stationService: StationService = kodein.instance()

        val stations = stationService.get()
        log.info("Received ${stations.count()} stations")
    }
}