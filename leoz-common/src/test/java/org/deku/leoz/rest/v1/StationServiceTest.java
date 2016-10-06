package org.deku.leoz.rest.v1;

import org.deku.leoz.rest.WebserviceTest;
import org.deku.leoz.rest.entities.internal.v1.Station;
import org.deku.leoz.rest.services.internal.v1.StationService;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by masc on 17.09.14.
 */
public class StationServiceTest extends WebserviceTest {
    @Test
    public void testGet() throws Exception {

    }

    @Test
    @Ignore
    public void testGetPerformance() throws Exception {
        StationService ts = this.getService(StationService.class);

        for (int i = 0; i < 20; i++) {
            Station[] stations = ts.get();

            for (Station d : stations) {
                System.out.println(String.format("%d %s", i, d.toString()));
            }
        }
    }
}
