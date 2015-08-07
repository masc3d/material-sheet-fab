package org.deku.leo2.node.data;

import org.deku.leo2.node.DataTest;
import org.deku.leo2.node.rest.services.v1.RoutingService;
import org.deku.leo2.rest.entities.v1.Routing;
import org.deku.leo2.rest.entities.v1.RoutingRequest;
import org.junit.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;

/**
 * Created by JT on 15.05.15.
 */
@ContextConfiguration(classes = {
        RoutingService.class })
public class RoutingLogicTest extends DataTest {
    @Inject
    RoutingService mRoutingService;

    @Test
    public void testRouting() {
        RoutingRequest rr = new RoutingRequest();
        Routing r = mRoutingService.request(rr);
        //Assert.assertFalse(r.getIsland());
    }

    @Test
    public void testRoutingPoland() {
        //mRoutingService.find()
    }
}
