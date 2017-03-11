package org.deku.leoz.rest

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import org.deku.leoz.config.FeignRestClientConfiguration
import org.deku.leoz.rest.service.internal.v1.StationService
import org.junit.Test
import org.slf4j.LoggerFactory
import rx.Observable

/**
 * Created by n3 on 10/12/2016.
 */
class StationServiceTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val kodein = Kodein {
        import(FeignRestClientConfiguration.module)
    }

    init {
        Kodein.global.addExtend(this.kodein)
    }

    @Test
    fun testGet() {
        val stationService: StationService = Kodein.global.instance()

        val stations = stationService.get()
        log.info("Received ${stations.count()} stations")
    }
}