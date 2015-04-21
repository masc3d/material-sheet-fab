package org.deku.leo2.central.rest.services.v1;

import org.deku.leo2.rest.entities.v1.HolidayType;
import org.deku.leo2.rest.entities.v1.LocalDateParam;
import org.deku.leo2.rest.entities.v1.Routing;
import org.deku.leo2.rest.entities.v1.RoutingVia;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Created by masc on 20.04.15.
 */
@Path("v1/routing")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoutingService implements org.deku.leo2.rest.services.v1.RoutingService {
    @Override
    public Routing[] find(LocalDateParam date, String country, String zip, String product) {
        // Dummy implementation
        ArrayList<Routing> result = new ArrayList<>();

        Routing r = new Routing("sector1", "zone1", LocalTime.now(), 12, HolidayType.RegionalBankHoliday, false);
        result.add(r);

        return result.toArray(new Routing[0]);
    }

    @Override
    public RoutingVia findVia(LocalDateParam date, String sourceSector, String destinationSector) {
        return new RoutingVia(new String[] { "S", "X" });
    }
}
