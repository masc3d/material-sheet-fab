package org.deku.leoz.rest.v1

import org.deku.leoz.rest.WebserviceTest
import org.deku.leoz.rest.entity.internal.v1.Station
import org.deku.leoz.rest.service.internal.v1.StationService
import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory

/**
 * Created by masc on 17.09.14.
 */
class StationServiceTest : WebserviceTest() {
    private val log = LoggerFactory.getLogger(this.javaClass)
    @Test
    @Throws(Exception::class)
    fun testGet() {
        val ts = this.getService(StationService::class.java)
        val stations = ts.get()
        log.info("Received ${stations.count()} stations")
    }
}
