package org.deku.leo2.node.data;

import org.deku.leo2.node.DataTest;
import org.deku.leo2.node.rest.services.v1.RoutingService;
import org.deku.leo2.rest.entities.ShortDate;
import org.deku.leo2.rest.entities.v1.Routing;
import org.junit.Test;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * Created by JT on 15.05.15.
 */
public class RoutingLogicTest extends DataTest {
    @Inject
    RoutingService mRoutingService;

    @Test
    public void testRouting() {
        //mRoutingService.find()
    }

    @Test
    public void testRoutingGermany() {
        LocalDate ld = LocalDate.of(2013, 11, 2);
        //LocalDateParam ldt= new LocalDateParam();

        java.sql.Timestamp dt= Timestamp.valueOf("2013-11-02");
//        ldt        = dt.toLocalDateTime( );

        //Routing r = mRoutingService.find(new LocalDateParam(LocalDate.of(2013, 11, 2)),"AT","1010","A");

        Routing r = mRoutingService.find(new ShortDate(LocalDate.parse("2013-11-02")), "AT", "1010", "A");
       // Assert.assertFalse(r.getIsland());

       // http://localhost:8080/leo2/rs/api/v1/routing/find?date=2013-11-02&country=AT&zip=1010&product=A

    }

    @Test
    public void testRoutingPoland() {
        //mRoutingService.find()
    }
}
