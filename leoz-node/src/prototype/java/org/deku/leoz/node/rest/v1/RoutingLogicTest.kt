package org.deku.leoz.node.rest.v1

import org.deku.leoz.node.rest.ServiceException
import org.deku.leoz.node.config.DataTestConfiguration
import org.deku.leoz.time.ShortDate
import org.deku.leoz.service.pub.RoutingService.Request
import org.deku.leoz.node.service.pub.RoutingService
import org.deku.leoz.service.entity.ServiceErrorCode
import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import sx.junit.PrototypeTest
import javax.inject.Inject

/**
 * Created by JT on 15.05.15.
 */
@Category(PrototypeTest::class)
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(
        DataTestConfiguration::class,
        RoutingService::class
))
class RoutingLogicTest {
    @Inject
    private lateinit var routingService: RoutingService

    @Test
    fun testRouting01() {
        try {
            val request = Request()
            val r = this.routingService.request(request)
        } catch (e: ServiceException) {
            Assert.assertEquals(ServiceErrorCode.MISSING_PARAMETER, e.errorCode)
        }

    }
/* swagger
DE Feiertag
{
  "sendDate": "2016-06-24",
  "consignee": {
    "country": "DE",
    "zip": "36286",
    "timeFrom": "09:00",
    "timeTo": "12:00"
  }
}
->
  "labelContent": "257",
  "message": "OK",
  "sendDate": "2017-06-25",
  "deliveryDate": "2017-06-27"
}

-----
Rergionaler FT heilige drei könige ->
{
  "sendDate": "2017-01-05",
  "consignee": {
    "country": "DE",
    "zip": "06110",
    "timeFrom": "09:00",
    "timeTo": "12:00",
    "desiredStation": "020"
  }
->
  ],
  "labelContent": "406",
  "message": "OK",
  "sendDate": "2017-01-05",
  "deliveryDate": "2017-01-09"
}
}
*/




    @Test
    fun testRouting02() {
        try {
            val request = Request()
            request.sendDate = ShortDate("2015-08-01")
            val r = this.routingService.request(request)
        } catch (e: ServiceException) {
            Assert.assertEquals(ServiceErrorCode.MISSING_PARAMETER, e.errorCode)
        }

    }

    @Test
    fun testRouting03() {
        try {
            val request = Request()
            request.sendDate = ShortDate("2015-08-01")
            val rp = Request.Participant()
            rp.zip = "64850"
            request.sender = rp
            val r = this.routingService.request(request)
        } catch (e: ServiceException) {
            Assert.assertEquals(ServiceErrorCode.MISSING_PARAMETER, e.errorCode)
        }
    }

    @Test
    fun testRouting04() {
        try {
            val request = Request()
            request.sendDate = ShortDate("2015-08-01")
            val rp = Request.Participant()
            rp.country = "DE"
            request.sender = rp
            val r = this.routingService.request(request)
        } catch (e: ServiceException) {
            Assert.assertEquals(ServiceErrorCode.MISSING_PARAMETER, e.errorCode)
        }
    }

    @Test
    fun testRouting05() {
        try {
            val request = Request()
            request.sendDate = ShortDate("2015-08-01")
            val rp = Request.Participant()
            rp.country = "D"
            rp.zip = "64850"
            request.sender = rp
            val r = this.routingService.request(request)
        } catch (e: ServiceException) {
            Assert.assertEquals(ServiceErrorCode.WRONG_PARAMETER_VALUE, e.errorCode)
        }
    }

    @Test
            //ohne unvollständiger participant
    fun testRouting06() {
        try {
            val request = Request()
            request.sendDate = ShortDate("2015-08-01")
            val rp = Request.Participant()
            rp.country = "DE"
            rp.zip = "6485"
            request.sender = rp
            val r = this.routingService.request(request)
        } catch (e: ServiceException) {
            Assert.assertEquals(ServiceErrorCode.WRONG_PARAMETER_VALUE, e.errorCode)
        }
    }

    @Test
    fun testRouting07() {
        try {
            val request = Request()
            request.sendDate = ShortDate("2015-08-01")
            val rp = Request.Participant()
            rp.country = "DE"
            rp.zip = "A4850"
            request.sender = rp
            val r = this.routingService.request(request)
        } catch (e: ServiceException) {
            Assert.assertEquals(ServiceErrorCode.WRONG_PARAMETER_VALUE, e.errorCode)
        }
    }

    @Test
    fun testRouting08() {
        try {
            val request = Request()
            request.sendDate = ShortDate("2015-08-01")
            val rp = Request.Participant()
            rp.country = "AT"
            request.consignee = rp
            val r = this.routingService.request(request)
        } catch (e: ServiceException) {
            Assert.assertEquals(ServiceErrorCode.MISSING_PARAMETER, e.errorCode)
        }
    }

    @Test
    fun testRouting09() {
        try {
            val request = Request()
            request.sendDate = ShortDate("2015-08-01")
            val rp = Request.Participant()
            rp.country = "PL"
            rp.zip = "10010"
            request.sender = rp
            val r = this.routingService.request(request)
            Assert.assertEquals(null, r.consignee)
            Assert.assertEquals(r.sender!!.station.toLong(), 514)
            Assert.assertEquals(r.sender!!.zipCode, "10-010")
        } catch (e: ServiceException) {
            Assert.assertEquals("", e.errorCode)
        }
    }

    // Daten sind noch falsch
    //    @Test
    //    public void testRouting10() {
    //        try {
    //            RoutingService.Request request = new RoutingService.Request();
    //            request.setSendDate(new ShortDate("2015-08-01"));
    //            RoutingService.Request.Participant rp = new RoutingService.Request.Participant();
    //            rp.setCountry("GB");
    //            rp.setZip("ab10 6");
    //            request.setSender(rp);
    //            Routing r = mthis.routingService.request(request);
    //            Assert.assertEquals(null, r.getConsignee());
    //            Assert.assertEquals(r.getSender().getStation(), "523");
    //            Assert.assertEquals(r.getSender().getZipCode(), "ab10 6");
    //        } catch (Exception e) {
    //            Assert.assertEquals("", e.getErrorCode());
    //        }
    //    }

    @Test
    fun testRouting11() {
        try {
            val request = Request()
            request.sendDate = ShortDate("2015-08-01")
            val rps = Request.Participant()
            rps.country = "DE"
            rps.zip = "80331"
            request.sender = rps
            val rpc = Request.Participant()
            rpc.country = "DE"
            rpc.zip = "64850"
            request.consignee = rpc

            val r = this.routingService.request(request)
            Assert.assertEquals(r.consignee!!.station.toLong(), 363)
            Assert.assertEquals(r.sender!!.dayType, "Saturday")
            Assert.assertEquals(r.sender!!.station.toLong(), 280)
            Assert.assertEquals(r.deliveryDate!!.toString(), "2015-08-04")
            Assert.assertEquals(r.labelContent, "363")
        } catch (e: ServiceException) {
            Assert.assertEquals("", e.errorCode)
        }
    }

    @Test
    fun testRouting12() {
        try {
            val request = Request()
            request.sendDate = ShortDate("2015-08-01")
            val rps = Request.Participant()
            rps.country = "DE"
            rps.zip = "80331"
            request.sender = rps
            val rpc = Request.Participant()
            rpc.country = "DE"
            rpc.zip = "65850"
            request.consignee = rpc

            val r = this.routingService.request(request)
            Assert.assertEquals(r.consignee!!.station.toLong(), 363)
            Assert.assertEquals(r.sender!!.dayType, "Saturday")
            Assert.assertEquals(r.sender!!.station.toLong(), 280)
            Assert.assertEquals(r.deliveryDate!!.toString(), "2015-08-04")
            Assert.assertEquals(r.labelContent, "363")
        } catch (e: ServiceException) {
            Assert.assertEquals(org.deku.leoz.service.pub.RoutingService.ErrorCode.ROUTE_NOT_AVAILABLE_FOR_GIVEN_PARAMETER, e.errorCode)
        }
    }

    @Test
    fun testRouting13() {
        try {
            val request = Request()
            request.sendDate = ShortDate("2015-08-01")
            val rps = Request.Participant()
            rps.country = "DE"
            rps.zip = "01067"
            request.sender = rps
            val rpc = Request.Participant()
            rpc.country = "DE"
            rpc.zip = "01108"
            request.consignee = rpc

            val r = this.routingService.request(request)
            Assert.assertEquals(r.consignee!!.station.toLong(), 412)
            Assert.assertEquals(r.sender!!.dayType, "Saturday")
            Assert.assertEquals(r.sender!!.station.toLong(), 412)
            Assert.assertEquals(r.deliveryDate!!.toString(), "2015-08-04")
            Assert.assertEquals(r.labelContent, "412")
        } catch (e: ServiceException) {
            Assert.assertEquals(org.deku.leoz.service.pub.RoutingService.ErrorCode.ROUTE_NOT_AVAILABLE_FOR_GIVEN_PARAMETER, e.errorCode)
        }
    }

    @Test
    fun testRouting14() {
        try {
            val request = Request()
            request.sendDate = ShortDate("2015-08-01")
            val rps = Request.Participant()
            rps.country = "DE"
            rps.zip = "20095"
            request.sender = rps
            val rpc = Request.Participant()
            rpc.country = "DE"
            rpc.zip = "20148"
            request.consignee = rpc

            val r = this.routingService.request(request)
            Assert.assertEquals(r.consignee!!.station.toLong(), 20)
            Assert.assertEquals(r.sender!!.dayType, "Saturday")
            Assert.assertEquals(r.sender!!.station.toLong(), 20)
            Assert.assertEquals(r.deliveryDate!!.toString(), "2015-08-04")
            Assert.assertEquals(r.labelContent, "020")
        } catch (e: ServiceException) {
            Assert.assertEquals(org.deku.leoz.service.pub.RoutingService.ErrorCode.ROUTE_NOT_AVAILABLE_FOR_GIVEN_PARAMETER, e.errorCode)
        }
    }

    @Test
    fun testRouting15() {
        try {
            val request = Request()
            request.sendDate = ShortDate("2015-08-01")
            val rps = Request.Participant()
            rps.country = "DE"
            rps.zip = "20095"
            request.sender = rps
            val rpc = Request.Participant()
            rpc.country = "DE"
            rpc.zip = "44623"
            request.consignee = rpc

            val r = this.routingService.request(request)
            Assert.assertEquals(r.consignee!!.station.toLong(), 20)
            Assert.assertEquals(r.sender!!.dayType, "Saturday")
            Assert.assertEquals(r.sender!!.station.toLong(), 16)
            Assert.assertEquals(r.deliveryDate!!.toString(), "2015-08-04")
            Assert.assertEquals(r.labelContent, "020")
        } catch (e: ServiceException) {
            Assert.assertEquals(ServiceErrorCode.WRONG_PARAMETER_VALUE, e.errorCode)
        }
    }

    /**
     * Check zipcode which is affected by regional holiday (Day after send date is holiday) - Weekend between these days
     */
    @Test
    fun testRegionalHoliday01() {
        val request = Request(
                sendDate = ShortDate("2016-10-28"),
                consignee = Request.Participant(
                        country = "DE",
                        zip = "99084"))

        val routing = this.routingService.request(request)
        Assert.assertEquals(routing.deliveryDate!!.toString(), "2016-11-01")
    }

    /**
     * Check zipcode which is affected by regional holiday (Day after send date is holiday)
     */
    @Test
    fun testRegionalHoliday02() {
        val request = Request(
                sendDate = ShortDate("2016-10-31"),
                consignee = Request.Participant(
                        country = "DE",
                        zip = "40822"))

        val routing = this.routingService.request(request)
        Assert.assertEquals(routing.deliveryDate!!.toString(), "2016-11-02")
    }

    /**
     * Check zipcode which is not affected by regional holiday
     */
    @Test
    fun testRegionalHoliday03() {
        val request = Request(
                sendDate = ShortDate("2016-10-28"),
                consignee = Request.Participant(
                        country = "DE",
                        zip = "36286"))

        val routing = this.routingService.request(request)
        Assert.assertEquals(routing.deliveryDate!!.toString(), "2016-10-31")
    }

    @Test
    fun testFebruaryWithoutLeapYear() {
        val request = Request(
                sendDate = ShortDate("2017-02-28"),
                consignee = Request.Participant(
                        country = "DE",
                        zip = "36286"))

        val routing = this.routingService.request(request)
        Assert.assertEquals(routing.deliveryDate!!.toString(), "2017-01-01")
    }

    @Test
    fun testFebruaryWithoutLeapYear2() {
        val request = Request(
                sendDate = ShortDate("2017-02-29"),
                consignee = Request.Participant(
                        country = "DE",
                        zip = "36286"))

        try {
            this.routingService.request(request)
            Assert.fail()
        } catch(e: ServiceException) {

        }
    }

    @Test
    fun testFebruaryWithinLeapYear() {
        val request = Request(
                sendDate = ShortDate("2016-02-28"),
                consignee = Request.Participant(
                        country = "DE",
                        zip = "36286"))

        val routing = this.routingService.request(request)
        Assert.assertEquals(routing.deliveryDate!!.toString(), "2016-02-29")
    }

    @Test
    fun testFebruaryWithinLeapYear2() {
        val request = Request(
                sendDate = ShortDate("2016-02-29"),
                consignee = Request.Participant(
                        country = "DE",
                        zip = "36286"))

        val routing = this.routingService.request(request)
        Assert.assertEquals(routing.deliveryDate!!.toString(), "2016-03-01")
    }
}
