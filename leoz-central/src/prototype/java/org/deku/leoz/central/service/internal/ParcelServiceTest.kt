package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.DataTestConfiguration
import org.deku.leoz.central.config.ParcelServiceConfiguration
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
//import org.xnio._private.Messages.msg
import java.io.File
import java.util.Base64

@Category(StandardTest::class)
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(
        DataTestConfiguration::class,
        ParcelServiceConfiguration::class,
        ParcelServiceConfiguration.Settings::class,
        org.deku.leoz.central.service.internal.ParcelServiceV1::class,
        org.deku.leoz.central.service.internal.UserService::class,
        org.deku.leoz.central.service.internal.BagService::class,
        org.deku.leoz.node.service.pub.RoutingService::class,
        org.deku.leoz.node.service.internal.StationService::class
))
class ParcelServiceTest {
    @Inject
    lateinit var parcelService: org.deku.leoz.central.service.internal.ParcelServiceV1


    @Test
    fun testOnMessage() {

        val img = File("/Users/helke/Documents/outOfMemory.jpg").readBytes()
        val encoder = Base64.getEncoder()
        encoder.encodeToString(img)
        val signature = File("/Users/helke/Documents/logo.svg").readBytes()
        encoder.encodeToString(signature)
        //val svgDirect = File("/Users/helke/Documents/logo.svg").readText()
        //val svgDirect = File("/Users/helke/Documents/test.svg").readText()
        //val svgDirect = File("/Users/helke/Documents/drei.svg").readText()
        val svgDirect=this.javaClass.getResourceAsStream("/drei.svg").bufferedReader().use { it.readText() }
        //val event = ParcelServiceV1.Event(event = Event.DELIVERED.value, reason = Reason.NORMAL.id, time = Date().toTimestamp(), parcelScancode = "20450007242", additionalInfo = AdditionalInfo.DeliveredInfo(recipient = "müllerSchmidt", signature = sig64))
        //val event = ParcelServiceV1.Event(event = Event.DELIVERED.value, reason = Reason.NORMAL.id, time = Date().toTimestamp(), parcelId = 2173653856606, parcelScancode = "20450007242", additionalInfo = AdditionalInfo.DeliveredInfo(recipient = "müllerSchmidt", signature = sig64))
        //val event = ParcelServiceV1.Event(event = Event.DELIVERED.value, reason = Reason.NORMAL.id, time = Date().toTimestamp(), parcelId = 1173471944806, parcelScancode = "2041019147", additionalInfo = AdditionalInfo.DeliveredInfo(recipient = "müllerSchmidt", signature = svgDirect))
        //val event = ParcelServiceV1.Event(event = Event.DELIVERED.value, reason = Reason.POSTBOX.id, time = Date().toTimestamp(), parcelId = 15001093,  additionalInfo = AdditionalInfo.DeliveredInfo(recipient = "Tschuh", signature = svgDirect))
        val event = ParcelServiceV1.Event(event = Event.DELIVERED.value, reason = Reason.NORMAL.id, time = Date().toTimestamp(), parcelId = 15001093, additionalInfo = AdditionalInfo.DeliveredInfo(recipient = "Tschiller", signature = svgDirect))
        //val event = ParcelServiceV1.Event(event = Event.DELIVERED.value, reason = Reason.NORMAL.id, time = Date().toTimestamp(), parcelId = 1163467893501, parcelScancode = "86853136142", additionalInfo = AdditionalInfo.DeliveredInfo(recipient = "müllerSchmidtMeier", signature = img64, mimetype = "jpg"))
        //val event = ParcelServiceV1.Event(event = Event.DELIVERED.value, reason = Reason.NORMAL.id, time = Date().toTimestamp(), parcelId = 2173652369401, parcelScancode = "10071321554",from="956", additionalInfo = AdditionalInfo.DeliveredInfo(recipient = "müllerMeier", signature = "abc"))
        //val event = ParcelServiceV1.Event(event = Event.DELIVERED.value, reason = Reason.NEIGHBOUR.id, time = Date().toTimestamp(), parcelScancode = "7280464561",from="956", additionalInfo = AdditionalInfo.DeliveredAtNeighborInfo(name = "schulz", signature = "abc",address = "str nr 6"))
        //event 106,reason=0
        //collie 63407133663 depot 956
        //val event = ParcelServiceV1.Event(event = Event.IMPORT_RECEIVE.value, reason = Reason.NORMAL.id, time = Date().toTimestamp(), parcelId = 15001093, parcelScancode = "2041019142", from = "956", additionalInfo = AdditionalInfo.EmptyInfo)
        //seit 30.10.2017 ohne scancode
        //val event = ParcelServiceV1.Event(event = Event.IMPORT_RECEIVE.value, reason = Reason.NORMAL.id, time = Date().toTimestamp(), parcelId = 0, from = "956", additionalInfo = AdditionalInfo.EmptyInfo)
        //event 107,reason 510
        //val event = ParcelServiceV1.Event(event = Event.DELIVERY_FAIL.value, reason = Reason.WRONG_ROUTING.id, time = Date().toTimestamp(), parcelScancode = "51520039870", from = "956", additionalInfo = AdditionalInfo.EmptyInfo)

        //val event = ParcelServiceV1.Event(event = Event.DELIVERY_FAIL.value, reason = Reason.PARCEL_DAMAGED.id, time = Date().toTimestamp(), parcelScancode = "20450007242", additionalInfo = AdditionalInfo.DamagedInfo(description = "aufgerissen", photo = img64))

        //val msg = ParcelServiceV1.ParcelMessage(events = arrayOf(event))
        val msg = ParcelServiceV1.ParcelMessage(events = arrayOf(event),userId = 3, nodeId = "71f8a014-6139-92a8-1757-ad520452538c", deliveredInfo = ParcelServiceV1.ParcelMessage.DeliveredInfo(recipient = "müllerSchmidtIgor", signature = svgDirect))
        //val msg = ParcelServiceV1.ParcelMessage(events = arrayOf(event),userId = 3, nodeId = "abcf-tzt", postboxDeliveryInfo = ParcelServiceV1.ParcelMessage.PostboxDeliveryInfo(pictureFileUid = UUID.fromString("4c4b1f91-5539-4ac1-9fdd-04ea17deab63")))
        //val msg = ParcelServiceV1.ParcelMessage(events = arrayOf(event), userId = 3, nodeId = "abcf-tzt", signatureOnPaperInfo = ParcelServiceV1.ParcelMessage.SignatureOnPaperInfo(pictureFileUid = UUID.fromString("86f86b71-acac-45fd-b4f1-5b7d2440e501"), recipient = "MrBig1234565432413243567898uztdferste5rhuzt"))
        //val msg = ParcelServiceV1.ParcelMessage(events = arrayOf(event), nodeId = "abcf-tzt", deliveredInfo = ParcelServiceV1.ParcelMessage.DeliveredInfo(recipient = "müllerSchmidtIgor", signature = img64,mimetype="jpg"))


        parcelService.onMessage(msg, null)
    }
}