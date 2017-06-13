package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.DataTestConfiguration
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import sx.junit.StandardTest
import javax.inject.Inject

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
        //locationService.onMessage()
    }
}