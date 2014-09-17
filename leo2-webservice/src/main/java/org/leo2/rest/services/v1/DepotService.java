package org.leo2.rest.services.v1;

import org.leo2.rest.Persistence;
import org.leo2.rest.v1.entities.Depot;
import org.leo2.rest.v1.IDepotService;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by masc on 17.09.14.
 */
@Path("v1/depot")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DepotService implements IDepotService {
    @Override
    public Depot[] get() {
        return Persistence.instance().query(Depot.class).toList().toArray(new Depot[0]);
    }
}
