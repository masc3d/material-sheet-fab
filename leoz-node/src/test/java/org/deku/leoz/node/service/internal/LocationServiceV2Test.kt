package org.deku.leoz.node.service.internal

import org.deku.leoz.model.VehicleType
import org.deku.leoz.node.Application
import org.deku.leoz.node.config.DataTestConfiguration
import org.deku.leoz.service.internal.LocationServiceV2
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import sx.junit.PrototypeTest
import sx.junit.StandardTest
import sx.time.toTimestamp
import java.util.*
import javax.inject.Inject


@Category(StandardTest::class)
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(
        DataTestConfiguration::class,
        org.deku.leoz.node.service.internal.LocationService::class


))
@ActiveProfiles(Application.PROFILE_CLIENT_NODE)
class LocationServiceV2Test {

    @Inject
    lateinit var locationService:org.deku.leoz.node.service.internal.LocationService

    @Test
    fun testOnMessage() {
        val currentPosition = org.deku.leoz.service.internal.LocationServiceV2.GpsDataPoint(
                latitude = 8.0,
                longitude = 55.0,
                time = Date().toTimestamp(),
                speed = 57.toFloat(),
                bearing = 0.toFloat(),
                altitude = 0.0,
                accuracy = 0.toFloat(),
                vehicleType = VehicleType.BIKE
        )

        val gpsMessage = LocationServiceV2.GpsMessage(
                userId = 1,
                dataPoints = arrayOf(currentPosition))

        locationService. onMessage(gpsMessage, null)
    }
}