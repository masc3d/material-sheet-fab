package org.deku.leoz.rest.v1;

import org.deku.leoz.rest.WebserviceTest;
import org.deku.leoz.rest.entities.internal.v1.Depot;
import org.deku.leoz.rest.services.internal.v1.DepotService;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by masc on 17.09.14.
 */
public class DepotServiceTest extends WebserviceTest {
    @Test
    public void testGet() throws Exception {

    }

    @Test
    @Ignore
    public void testGetPerformance() throws Exception {
        DepotService ts = this.getService(DepotService.class);

        for (int i = 0; i < 20; i++) {
            Depot[] depots = ts.get();

            for (Depot d : depots) {
                System.out.println(String.format("%d %s", i, d.toString()));
            }
        }
    }
}
