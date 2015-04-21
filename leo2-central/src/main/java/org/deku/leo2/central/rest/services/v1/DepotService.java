package org.deku.leo2.central.rest.services.v1;

import org.deku.leo2.central.Persistence;
import org.deku.leo2.entities.v1.Depot;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by masc on 17.09.14.
 */
@Path("v1/depot")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DepotService implements org.deku.leo2.rest.services.v1.DepotService {
    Logger mLog = Logger.getLogger(DepotService.class.getName());

    @Override
    public Depot[] get() {
        return Persistence.instance()
                .query(Depot.class)
                .toList().toArray(new Depot[0]);
    }

    @Override
    public Depot[] find(String query) {
        query = query.trim();
        String expStartsWith = query + "%";
        String expSubstring = "%" + query + "%";

        // masc20140923. query using JINQ. still buggy with JPQL.like
//        return Persistence.instance()
//                .query(Depot.class, true, true)
//                .where(d -> (JPQL.like(d.getDepotMatchcode(), likeExp)) ||
//                        (JPQL.like(d.getFirma1(), likeExp)) ||
//                        (JPQL.like(d.getFirma2(), likeExp)) ||
//                        (JPQL.like(d.getPlz(), likeExp)) ||
//                        (JPQL.like(d.getLkz(), likeExp)) ||
//                        (JPQL.like(d.getOrt(), likeExp)) ||
//                        (JPQL.like(d.getStrasse(), likeExp)))
//                .toList().toArray(new Depot[0]);

        List<Depot> results = Persistence.instance().getEntityManager().createQuery("SELECT D FROM Depot D " +
                        "WHERE D.depotMatchcode LIKE :expStartsWith " +
                        "OR D.firma1 LIKE :expSubstring " +
                        "OR D.firma2 LIKE :expSubstring " +
                        "OR D.plz LIKE :expStartsWith " +
                        "OR D.lkz LIKE :expStartsWith " +
                        "OR D.ort LIKE :expSubstring " +
                        "OR D.strasse LIKE :expSubstring " +
                        "ORDER BY D.depotMatchcode")
                .setParameter("expStartsWith", expStartsWith)
                .setParameter("expSubstring", expSubstring)
                .getResultList();

        return results.toArray(new Depot[0]);

    }
}
