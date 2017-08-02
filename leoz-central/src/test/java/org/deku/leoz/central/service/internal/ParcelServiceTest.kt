package org.deku.leoz.central.service.internal

import elemental.util.ArrayOf
import org.deku.leoz.central.config.DataTestConfiguration
import org.deku.leoz.model.AdditionalInfo
import org.deku.leoz.model.Event
import org.deku.leoz.model.Reason
import org.deku.leoz.service.internal.ParcelServiceV1
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import sx.junit.StandardTest
import sx.time.toTimestamp
import java.util.*
import javax.inject.Inject
import org.springframework.context.annotation.ComponentScan

@Category(StandardTest::class)
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(
        DataTestConfiguration::class,
        org.deku.leoz.central.service.internal.ParcelServiceV1::class
))
class ParcelServiceTest {
    @Inject
    lateinit var parcelService: org.deku.leoz.central.service.internal.ParcelServiceV1

    @Test
    fun testOnMessage() {
        val event = ParcelServiceV1.Event(event = 106, reason = 0, time = Date().toTimestamp(), parcelScancode = "20450007242", additionalInfo = AdditionalInfo.DeliveredInfo(recipient = "müller", signature = "abc"))
        val msg = ParcelServiceV1.ParcelMessage(events = arrayOf(event))


        parcelService.onMessage(msg, null)
    }
}