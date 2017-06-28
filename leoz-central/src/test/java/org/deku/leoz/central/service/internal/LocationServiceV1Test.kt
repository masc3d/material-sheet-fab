package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.DataTestConfiguration
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.deku.leoz.service.internal.LocationServiceV1
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import sx.junit.StandardTest
import sx.time.toTimestamp
import java.util.*
import javax.inject.Inject

//import org.deku.leoz.service.internal.LocationService

/**
 * Created by masc on 13.06.17.
 */
@Category(StandardTest::class)
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(
        DataTestConfiguration::class,
        LocationServiceV1::class
))
class LocationServiceV1Test {

    @Inject
    lateinit var locationService: org.deku.leoz.central.service.internal.LocationServiceV1

    @Test
    fun testOnMessage() {
        // TODO: invoke on message with mock objects
        val currentPosition = org.deku.leoz.service.internal.LocationServiceV1.GpsDataPoint(
                latitude = 8.0,
                longitude = 55.0,
                time = Date().toTimestamp(),
                speed = 57.toFloat(),
                bearing = 0.toFloat(),
                altitude = 0.0,
                accuracy = 0.toFloat()
        )

        val gpsMessage = LocationServiceV1.GpsMessage(
                userId = 0,
                dataPoints = arrayOf(currentPosition))

        locationService. onMessage(gpsMessage, null)
    }
}