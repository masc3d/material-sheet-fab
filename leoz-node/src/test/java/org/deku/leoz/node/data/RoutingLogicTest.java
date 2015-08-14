package org.deku.leoz.node.data;

import org.deku.leoz.node.DataTest;
import org.deku.leoz.node.rest.ServiceException;
import org.deku.leoz.node.rest.services.v1.RoutingService;
import org.deku.leoz.rest.entities.ShortDate;
import org.deku.leoz.rest.entities.v1.Routing;
import org.deku.leoz.rest.entities.v1.RoutingRequest;
import org.deku.leoz.rest.services.ServiceErrorCode;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;

/**
 * Created by JT on 15.05.15.
 */
@ContextConfiguration(classes = {
        RoutingService.class})
public class RoutingLogicTest extends DataTest {
    @Inject
    RoutingService mRoutingService;

    @Test
    public void testRouting01() {
        try {
            RoutingRequest request = new RoutingRequest();
            Routing r = mRoutingService.request(request);
        } catch (ServiceException e) {
            Assert.assertEquals(ServiceErrorCode.MISSING_PARAMETER , e.getErrorCode());
        }
    }

    @Test
    public void testRouting02() {
        try {
            RoutingRequest request = new RoutingRequest();
            request.setSendDate(new ShortDate("2015-08-01"));
            Routing r = mRoutingService.request(request);
        } catch (ServiceException e) {
            Assert.assertEquals(ServiceErrorCode.MISSING_PARAMETER, e.getErrorCode());
        }
    }

    @Test
    public void testRouting03() {
        try {
            RoutingRequest request = new RoutingRequest();
            request.setSendDate(new ShortDate("2015-08-01"));
            RoutingRequest.RequestParticipant rp = new RoutingRequest.RequestParticipant();
            rp.setZip("64850");
            request.setSender(rp);
            Routing r = mRoutingService.request(request);
        } catch (ServiceException e) {
            Assert.assertEquals(ServiceErrorCode.MISSING_PARAMETER, e.getErrorCode());
        }

    }

    @Test
    public void testRouting04() {
        try {
            RoutingRequest request = new RoutingRequest();
            request.setSendDate(new ShortDate("2015-08-01"));
            RoutingRequest.RequestParticipant rp = new RoutingRequest.RequestParticipant();
            rp.setCountry("DE");
            request.setSender(rp);
            Routing r = mRoutingService.request(request);
        } catch (ServiceException e) {
            Assert.assertEquals(ServiceErrorCode.MISSING_PARAMETER, e.getErrorCode());
        }

    }

    @Test
    public void testRouting05() {
        try {
            RoutingRequest request = new RoutingRequest();
            request.setSendDate(new ShortDate("2015-08-01"));
            RoutingRequest.RequestParticipant rp = new RoutingRequest.RequestParticipant();
            rp.setCountry("D");
            rp.setZip("64850");
            request.setSender(rp);
            Routing r = mRoutingService.request(request);
        } catch (ServiceException e) {
            Assert.assertEquals(ServiceErrorCode.WRONG_PARAMETER_VALUE, e.getErrorCode());
        }

    }

    @Test
    //ohne unvollständiger participant
    public void testRouting06() {
        try {
            RoutingRequest request = new RoutingRequest();
            request.setSendDate(new ShortDate("2015-08-01"));
            RoutingRequest.RequestParticipant rp = new RoutingRequest.RequestParticipant();
            rp.setCountry("DE");
            rp.setZip("6485");
            request.setSender(rp);
            Routing r = mRoutingService.request(request);
        } catch (ServiceException e) {
            Assert.assertEquals(ServiceErrorCode.WRONG_PARAMETER_VALUE, e.getErrorCode());
        }
    }

    @Test
    public void testRouting07() {
        try {
            RoutingRequest request = new RoutingRequest();
            request.setSendDate(new ShortDate("2015-08-01"));
            RoutingRequest.RequestParticipant rp = new RoutingRequest.RequestParticipant();
            rp.setCountry("DE");
            rp.setZip("A4850");
            request.setSender(rp);
            Routing r = mRoutingService.request(request);
        } catch (ServiceException e) {
            Assert.assertEquals(ServiceErrorCode.WRONG_PARAMETER_VALUE, e.getErrorCode());
        }
    }

    @Test
    public void testRouting08() {
        try {
            RoutingRequest request = new RoutingRequest();
            request.setSendDate(new ShortDate("2015-08-01"));
            RoutingRequest.RequestParticipant rp = new RoutingRequest.RequestParticipant();
            rp.setCountry("AT");
            request.setConsignee(rp);
            Routing r = mRoutingService.request(request);
        } catch (ServiceException e) {
            Assert.assertEquals(ServiceErrorCode.MISSING_PARAMETER, e.getErrorCode());
        }
    }

    @Test
    public void testRouting09() {
        try {
            RoutingRequest request = new RoutingRequest();
            request.setSendDate(new ShortDate("2015-08-01"));
            RoutingRequest.RequestParticipant rp = new RoutingRequest.RequestParticipant();
            rp.setCountry("PL");
            rp.setZip("10010");
            request.setSender(rp);
            Routing r = mRoutingService.request(request);
            Assert.assertEquals(null, r.getConsignee());
            Assert.assertEquals(r.getSender().getStation(), (Integer)514);
            Assert.assertEquals(r.getSender().getZipCode(), "10-010");
        } catch (ServiceException e) {
            Assert.assertEquals("", e.getErrorCode());
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
//            Routing r = mRoutingService.request(request);
//            Assert.assertEquals(null, r.getConsignee());
//            Assert.assertEquals(r.getSender().getStation(), "523");
//            Assert.assertEquals(r.getSender().getZipCode(), "ab10 6");
//        } catch (Exception e) {
//            Assert.assertEquals("", e.getErrorCode());
//        }
//    }

    @Test
    public void testRouting11() {
        try {
            RoutingRequest request = new RoutingRequest();
            request.setSendDate(new ShortDate("2015-08-01"));
            RoutingRequest.RequestParticipant rps = new RoutingRequest.RequestParticipant();
            rps.setCountry("DE");
            rps.setZip("80331");
            request.setSender(rps);
            RoutingRequest.RequestParticipant rpc = new RoutingRequest.RequestParticipant();
            rpc.setCountry("DE");
            rpc.setZip("64850");
            request.setConsignee(rpc);

            Routing r = mRoutingService.request(request);
            Assert.assertEquals(r.getConsignee().getStation(), (Integer)363);
            Assert.assertEquals(r.getSender().getDayType(), "Saturday");
            Assert.assertEquals(r.getSender().getStation(), (Integer)280);
            Assert.assertEquals(r.getDeliveryDate().toString(), "2015-08-04");
            Assert.assertEquals(r.getLabelContent(), "363");
        } catch (ServiceException e) {
            Assert.assertEquals("", e.getErrorCode());
        }
    }

    @Test
    public void testRouting12() {
        try {
            RoutingRequest request = new RoutingRequest();
            request.setSendDate(new ShortDate("2015-08-01"));
            RoutingRequest.RequestParticipant rps = new RoutingRequest.RequestParticipant();
            rps.setCountry("DE");
            rps.setZip("80331");
            request.setSender(rps);
            RoutingRequest.RequestParticipant rpc = new RoutingRequest.RequestParticipant();
            rpc.setCountry("DE");
            rpc.setZip("65850");
            request.setConsignee(rpc);

            Routing r = mRoutingService.request(request);
            Assert.assertEquals(r.getConsignee().getStation(), (Integer)363);
            Assert.assertEquals(r.getSender().getDayType(), "Saturday");
            Assert.assertEquals(r.getSender().getStation(), (Integer)280);
            Assert.assertEquals(r.getDeliveryDate().toString(), "2015-08-04");
            Assert.assertEquals(r.getLabelContent(), "363");
        } catch (ServiceException e) {
            Assert.assertEquals(org.deku.leoz.rest.services.v1.RoutingService.ErrorCode.ROUTE_NOT_AVAILABLE_FOR_GIVEN_PARAMETER, e.getErrorCode());
        }
    }
    @Test
    public void testRouting13() {
        try {
            RoutingRequest request = new RoutingRequest();
            request.setSendDate(new ShortDate("2015-08-01"));
            RoutingRequest.RequestParticipant rps = new RoutingRequest.RequestParticipant();
            rps.setCountry("DE");
            rps.setZip("01067");
            request.setSender(rps);
            RoutingRequest.RequestParticipant rpc = new RoutingRequest.RequestParticipant();
            rpc.setCountry("DE");
            rpc.setZip("01108");
            request.setConsignee(rpc);

            Routing r = mRoutingService.request(request);
            Assert.assertEquals(r.getConsignee().getStation(), (Integer)412);
            Assert.assertEquals(r.getSender().getDayType(), "Saturday");
            Assert.assertEquals(r.getSender().getStation(), (Integer)412);
            Assert.assertEquals(r.getDeliveryDate().toString(), "2015-08-04");
            Assert.assertEquals(r.getLabelContent(), "412");
        } catch (ServiceException e) {
            Assert.assertEquals(org.deku.leoz.rest.services.v1.RoutingService.ErrorCode.ROUTE_NOT_AVAILABLE_FOR_GIVEN_PARAMETER, e.getErrorCode());
        }
    }
    @Test
    public void testRouting14() {
        try {
            RoutingRequest request = new RoutingRequest();
            request.setSendDate(new ShortDate("2015-08-01"));
            RoutingRequest.RequestParticipant rps = new RoutingRequest.RequestParticipant();
            rps.setCountry("DE");
            rps.setZip("20095");
            request.setSender(rps);
            RoutingRequest.RequestParticipant rpc = new RoutingRequest.RequestParticipant();
            rpc.setCountry("DE");
            rpc.setZip("20148");
            request.setConsignee(rpc);

            Routing r = mRoutingService.request(request);
            Assert.assertEquals(r.getConsignee().getStation(), (Integer)20);
            Assert.assertEquals(r.getSender().getDayType(), "Saturday");
            Assert.assertEquals(r.getSender().getStation(), (Integer)20);
            Assert.assertEquals(r.getDeliveryDate().toString(), "2015-08-04");
            Assert.assertEquals(r.getLabelContent(), "020");
        } catch (ServiceException e) {
            Assert.assertEquals(org.deku.leoz.rest.services.v1.RoutingService.ErrorCode.ROUTE_NOT_AVAILABLE_FOR_GIVEN_PARAMETER, e.getErrorCode());
        }
    }
    @Test
    public void testRouting15() {
        try {
            RoutingRequest request = new RoutingRequest();
            request.setSendDate(new ShortDate("2015-08-01"));
            RoutingRequest.RequestParticipant rps = new RoutingRequest.RequestParticipant();
            rps.setCountry("DE");
            rps.setZip("20095");
            request.setSender(rps);
            RoutingRequest.RequestParticipant rpc = new RoutingRequest.RequestParticipant();
            rpc.setCountry("DE");
            rpc.setZip("44623");
            request.setConsignee(rpc);

            Routing r = mRoutingService.request(request);
            Assert.assertEquals(r.getConsignee().getStation(), (Integer)20);
            Assert.assertEquals(r.getSender().getDayType(), "Saturday");
            Assert.assertEquals(r.getSender().getStation(), (Integer)020);
            Assert.assertEquals(r.getDeliveryDate().toString(), "2015-08-04");
            Assert.assertEquals(r.getLabelContent(), "020");
        } catch (ServiceException e) {
            Assert.assertEquals(ServiceErrorCode.WRONG_PARAMETER_VALUE, e.getErrorCode());
        }
    }

}
