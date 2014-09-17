package org.leo2.rest.v1;

import org.junit.Ignore;
import org.junit.Test;
import org.leo2.rest.WebserviceTest;
import org.leo2.rest.v1.entities.Depot;

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
        IDepotService ts = this.getService(IDepotService.class);

        for (int i = 0; i < 20; i++) {
            Depot[] depots = ts.get();

            for (Depot d : depots) {
                System.out.println(String.format("%d %s", i, d.toString()));
            }
        }
    }
}
