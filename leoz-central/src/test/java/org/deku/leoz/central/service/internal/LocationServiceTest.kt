package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.DataTestConfiguration
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import sx.junit.StandardTest
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
        LocationService::class
))
class LocationServiceTest {

    @Inject
    lateinit var locationService: LocationService

    @Test
    fun testOnMessage() {
        // TODO: invoke on message with mock objects
        val currentPosition = org.deku.leoz.service.internal.LocationService.GpsDataPoint(
                latitude = 8.0,
                longitude = 55.0,
                time = Date(),
                speed = 55.toFloat(),
                bearing = 0.toFloat(),
                altitude = 0.0,
                accuracy = 0.toFloat()
        )

        val gpsData = org.deku.leoz.service.internal.LocationService.GpsData(userEmail = "user@deku.org",gpsDataPoints= listOf(currentPosition))

        //locationService.onMessage(gpsData, null)
        locationService.onMessage(currentPosition, null)
    }
}