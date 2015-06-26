package org.deku.leo2.node.rest.v1;

import org.deku.leo2.node.WebserviceTest;
import org.deku.leo2.rest.entities.ShortDate;
import org.deku.leo2.rest.entities.v1.Routing;
//import org.deku.leo2.rest.entities.v1.RoutingVia;
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
@Ignore
public class RoutingServiceTest extends WebserviceTest {
    @Rule
    public LoggingStopwatchRule sw = new LoggingStopwatchRule();

//    @Test
//    public void testEntryPoints() {
//        {
//            RoutingVia r = this.getService(RoutingService.class).findVia(new ShortDate(LocalDate.now()), "source", "destination");
//            System.out.println(r.getLabelContent());
//        }
//
//        {
//            Routing routing = this.getService(RoutingService.class).find(new ShortDate(LocalDate.now()).toString(), "country", "zip", "product");
//            System.out.println(routing.toString());
//        }
//    }
}
