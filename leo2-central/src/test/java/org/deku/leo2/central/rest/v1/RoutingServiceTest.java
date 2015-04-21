package org.deku.leo2.central.rest.v1;

import org.deku.leo2.central.rest.WebserviceTest;
import org.deku.leo2.rest.entities.v1.LocalDateParam;
import org.deku.leo2.rest.entities.v1.Routing;
import org.deku.leo2.rest.services.v1.RoutingService;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import sx.junit.LoggingStopwatchRule;

import java.time.LocalDate;
//import sx.junit.LoggingStopwatchRule;

/**
 * Created by masc on 20.04.15.
 */

public class RoutingServiceTest extends WebserviceTest {
    @Rule
    public LoggingStopwatchRule sw = new LoggingStopwatchRule();

    @Test
    @Ignore
    public void testRouting() {
//        Routing[] routings = this.getService(RoutingService.class).get();
//        System.out.println(routings.toString());

        {
            Routing[] routings = this.getService(RoutingService.class).find(new LocalDateParam(LocalDate.now()), "country", "zip", "product");
            System.out.println(routings.toString());
        }
    }
}
