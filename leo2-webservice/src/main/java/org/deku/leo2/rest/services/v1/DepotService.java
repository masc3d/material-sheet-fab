package org.deku.leo2.rest.services.v1;

import org.deku.leo2.rest.Persistence;
import org.deku.leo2.rest.v1.IDepotService;
import org.deku.leo2.rest.v1.entities.Depot;
import org.jinq.jpa.JPQL;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;

/**
 * Created by masc on 17.09.14.
 */
@Path("v1/depot")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DepotService implements IDepotService {
    Logger mLog = Logger.getLogger(DepotService.class.getName());

    @Override
    public Depot[] get() {
        return Persistence.instance()
                .query(Depot.class)
                .toList().toArray(new Depot[0]);
    }

    @Override
    public Depot[] find(String query) {
        String likeExp = query + "%";
        return Persistence.instance()
                .query(Depot.class, true, true)
                .where(d -> JPQL.like(d.getDepotMatchcode(), likeExp) ||
                        JPQL.like(d.getFirma1(), likeExp) ||
                        JPQL.like(d.getFirma2(), likeExp) ||
                        JPQL.like(d.getPlz(), likeExp) ||
                        JPQL.like(d.getLkz(), likeExp) ||
                        JPQL.like(d.getOrt(), likeExp) ||
                        JPQL.like(d.getStrasse(), likeExp))
                .toList().toArray(new Depot[0]);
    }
}
