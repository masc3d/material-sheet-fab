package org.deku.leoz.central.service.internal

import org.junit.Test
import org.junit.experimental.categories.Category
import org.slf4j.LoggerFactory
import sx.junit.StandardTest
import sx.log.slf4j.trace
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by masc on 25.05.18.
 */
@Category(StandardTest::class)
class MobileLogParcelMessageParserTest {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Test
    fun testMobileLogParcelMessageParser() {
        val message = "xxxxxxxxxxxxxxx xxxxx [ParcelMessage(userId=4, nodeId=f40ce925-cedd-8eb2-3f1e-8fabef15878c, events=[Event(event=107, reason=503, parcelId=16430525, time=Wed May 09 13:23:36 GMT+02:00 2018, latitude=50.1661101, longitude=9.1730371, fromStation=true, from=null, additionalInfo=org.deku.leoz.model.AdditionalInfo\$EmptyInfo@fff98c3, damagedInfo=null), Event(event=107, reason=503, parcelId=16430525, time=Wed May 09 13:23:36 GMT+02:00 2018, latitude=50.1661101, longitude=9.1730371, fromStation=true, from=null, additionalInfo=org.deku.leoz.model.AdditionalInfo\$EmptyInfo@fff98c3, damagedInfo=null)], deliveredInfo=DeliveredInfo(recipient=Xjxdjjdjd, recipientStreet=null, recipientStreetNo=null, recipientSalutation=Male, signature=<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.2\" baseProfile=\"tiny\" height=\"607\" width=\"720\"><g stroke-linejoin=\"round\" stroke-linecap=\"round\" fill=\"none\" stroke=\"black\"><path stroke-width=\"12\" d=\"M76,140c0,0 0,0 0,0 \"/><path stroke-width=\"10\" d=\"M76,140c4,3 5,2 9,5 \"/><path stroke-width=\"7\" d=\"M85,145c5,3 5,4 9,8 10,9 10,9 19,19 \"/><path stroke-width=\"6\" d=\"M113,172c10,11 10,12 20,23 9,10 8,11 18,20 14,13 15,12 30,23 9,5 9,5 19,9 10,5 10,5 21,9 12,4 12,4 24,8 11,3 11,3 23,5 8,1 8,1 16,0 \"/><path stroke-width=\"7\" d=\"M284,269c9,-2 9,-2 17,-6 9,-5 9,-5 18,-12 \"/><path stroke-width=\"6\" d=\"M319,251c12,-11 12,-12 23,-23 14,-14 13,-15 26,-28 12,-12 11,-14 25,-22 15,-9 16,-9 34,-13 21,-6 21,-5 43,-7 18,-2 18,-3 37,-2 15,0 15,1 30,4 24,6 24,7 47,13 26,6 26,6 53,12 17,4 17,3 35,7 8,2 8,2 16,3 \"/></g></svg>, mimetype=application/svg+xml), signatureOnPaperInfo=null, postboxDeliveryInfo=null)]"

        log.trace { message }

        log.trace {
            RecoveryService.MobileLogParcelMessageParser.parse(
                    message
            )
        }
    }

    @Test
    fun testDate() {
        log.trace {
            SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH).parse(
                    "Fri May 25 07:00:52 GMT+02:00 2018"
            )
        }
    }
}
