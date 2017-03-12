package org.deku.leoz.node.test.rest.v1

import org.deku.leoz.node.rest.ServiceException
import org.deku.leoz.node.rest.service.v1.RoutingService
import org.deku.leoz.node.test.config.DataTestConfiguration
import org.deku.leoz.rest.entity.ShortDate
import org.deku.leoz.rest.entity.v1.RoutingRequest
import org.deku.leoz.rest.service.ServiceErrorCode
import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import sx.junit.StandardTest
import javax.inject.Inject

/**
 * Created by JT on 15.05.15.
 */
@Category(StandardTest::class)
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
            val request = RoutingRequest()
            val r = this.routingService.request(request)
        } catch (e: ServiceException) {
            Assert.assertEquals(ServiceErrorCode.MISSING_PARAMETER, e.errorCode)
        }

    }

    @Test
    fun testRouting02() {
        try {
            val request = RoutingRequest()
            request.sendDate = ShortDate("2015-08-01")
            val r = this.routingService.request(request)
        } catch (e: ServiceException) {
            Assert.assertEquals(ServiceErrorCode.MISSING_PARAMETER, e.errorCode)
        }

    }

    @Test
    fun testRouting03() {
        try {
            val request = RoutingRequest()
            request.sendDate = ShortDate("2015-08-01")
            val rp = RoutingRequest.RequestParticipant()
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
            val request = RoutingRequest()
            request.sendDate = ShortDate("2015-08-01")
            val rp = RoutingRequest.RequestParticipant()
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
            val request = RoutingRequest()
            request.sendDate = ShortDate("2015-08-01")
            val rp = RoutingRequest.RequestParticipant()
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
            val request = RoutingRequest()
            request.sendDate = ShortDate("2015-08-01")
            val rp = RoutingRequest.RequestParticipant()
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
            val request = RoutingRequest()
            request.sendDate = ShortDate("2015-08-01")
            val rp = RoutingRequest.RequestParticipant()
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
            val request = RoutingRequest()
            request.sendDate = ShortDate("2015-08-01")
            val rp = RoutingRequest.RequestParticipant()
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
            val request = RoutingRequest()
            request.sendDate = ShortDate("2015-08-01")
            val rp = RoutingRequest.RequestParticipant()
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
    //            RoutingRequest request = new RoutingRequest();
    //            request.setSendDate(new ShortDate("2015-08-01"));
    //            RoutingRequest.RequestParticipant rp = new RoutingRequest.RequestParticipant();
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
            val request = RoutingRequest()
            request.sendDate = ShortDate("2015-08-01")
            val rps = RoutingRequest.RequestParticipant()
            rps.country = "DE"
            rps.zip = "80331"
            request.sender = rps
            val rpc = RoutingRequest.RequestParticipant()
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
            val request = RoutingRequest()
            request.sendDate = ShortDate("2015-08-01")
            val rps = RoutingRequest.RequestParticipant()
            rps.country = "DE"
            rps.zip = "80331"
            request.sender = rps
            val rpc = RoutingRequest.RequestParticipant()
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
            Assert.assertEquals(org.deku.leoz.rest.service.v1.RoutingService.ErrorCode.ROUTE_NOT_AVAILABLE_FOR_GIVEN_PARAMETER, e.errorCode)
        }
    }

    @Test
    fun testRouting13() {
        try {
            val request = RoutingRequest()
            request.sendDate = ShortDate("2015-08-01")
            val rps = RoutingRequest.RequestParticipant()
            rps.country = "DE"
            rps.zip = "01067"
            request.sender = rps
            val rpc = RoutingRequest.RequestParticipant()
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
            Assert.assertEquals(org.deku.leoz.rest.service.v1.RoutingService.ErrorCode.ROUTE_NOT_AVAILABLE_FOR_GIVEN_PARAMETER, e.errorCode)
        }
    }

    @Test
    fun testRouting14() {
        try {
            val request = RoutingRequest()
            request.sendDate = ShortDate("2015-08-01")
            val rps = RoutingRequest.RequestParticipant()
            rps.country = "DE"
            rps.zip = "20095"
            request.sender = rps
            val rpc = RoutingRequest.RequestParticipant()
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
            Assert.assertEquals(org.deku.leoz.rest.service.v1.RoutingService.ErrorCode.ROUTE_NOT_AVAILABLE_FOR_GIVEN_PARAMETER, e.errorCode)
        }
    }

    @Test
    fun testRouting15() {
        try {
            val request = RoutingRequest()
            request.sendDate = ShortDate("2015-08-01")
            val rps = RoutingRequest.RequestParticipant()
            rps.country = "DE"
            rps.zip = "20095"
            request.sender = rps
            val rpc = RoutingRequest.RequestParticipant()
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
        val request = RoutingRequest(
                sendDate = ShortDate("2016-10-28"),
                consignee = RoutingRequest.RequestParticipant(
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
        val request = RoutingRequest(
                sendDate = ShortDate("2016-10-31"),
                consignee = RoutingRequest.RequestParticipant(
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
        val request = RoutingRequest(
                sendDate = ShortDate("2016-10-28"),
                consignee = RoutingRequest.RequestParticipant(
                        country = "DE",
                        zip = "36286"))

        val routing = this.routingService.request(request)
        Assert.assertEquals(routing.deliveryDate!!.toString(), "2016-10-31")
    }

    @Test
    fun testFebruaryWithoutLeapYear() {
        val request = RoutingRequest(
                sendDate = ShortDate("2017-02-28"),
                consignee = RoutingRequest.RequestParticipant(
                        country = "DE",
                        zip = "36286"))

        val routing = this.routingService.request(request)
        Assert.assertEquals(routing.deliveryDate!!.toString(), "2017-01-01")
    }

    @Test
    fun testFebruaryWithoutLeapYear2() {
        val request = RoutingRequest(
                sendDate = ShortDate("2017-02-29"),
                consignee = RoutingRequest.RequestParticipant(
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
        val request = RoutingRequest(
                sendDate = ShortDate("2016-02-28"),
                consignee = RoutingRequest.RequestParticipant(
                        country = "DE",
                        zip = "36286"))

        val routing = this.routingService.request(request)
        Assert.assertEquals(routing.deliveryDate!!.toString(), "2016-02-29")
    }

    @Test
    fun testFebruaryWithinLeapYear2() {
        val request = RoutingRequest(
                sendDate = ShortDate("2016-02-29"),
                consignee = RoutingRequest.RequestParticipant(
                        country = "DE",
                        zip = "36286"))

        val routing = this.routingService.request(request)
        Assert.assertEquals(routing.deliveryDate!!.toString(), "2016-03-01")
    }
}
