package org.deku.leoz.node.test.rest.v1;

import org.deku.leoz.node.test.WebserviceTest;
import org.junit.Ignore;
import org.junit.Rule;
import sx.junit.LoggingStopwatchRule;

//import org.deku.leoz.rest.entities.v1.RoutingVia;
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
