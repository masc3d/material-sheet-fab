package org.deku.leo2.central.rest.services.v1;

import org.deku.leo2.central.entities.v1.Depot;
import org.deku.leo2.central.entities.v1.DepotRepository;
import org.deku.leo2.central.entities.v1.QDepot;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

/**
 * Created by masc on 17.09.14.
 */
@Component
@Path("v1/depot")
@Produces(MediaType.APPLICATION_JSON)
public class DepotService implements org.deku.leo2.rest.services.v1.DepotService {
    Logger mLog = Logger.getLogger(DepotService.class.getName());

    @PersistenceContext
    EntityManager mEntityManager;

    @Inject
    DepotRepository mDepotRepository;

    /**
     * Convert to service result
     * @param d
     * @return
     */
    static org.deku.leo2.rest.entities.v1.Depot convert(Depot d) {
        org.deku.leo2.rest.entities.v1.Depot rDepot = new org.deku.leo2.rest.entities.v1.Depot();
        rDepot.setDepotMatchcode(d.getDepotMatchcode());
        rDepot.setDepotNr(d.getDepotNr());
        rDepot.setFirma1(d.getFirma1());
        rDepot.setFirma2(d.getFirma2());
        rDepot.setLkz(d.getLkz());
        rDepot.setOrt(d.getOrt());
        rDepot.setPlz(d.getPlz());
        rDepot.setStrasse(d.getStrasse());
        return rDepot;
    }

    @Override
    public org.deku.leo2.rest.entities.v1.Depot[] get() {
        Iterable<Depot> depots = mDepotRepository.findAll();

        return StreamSupport.stream(depots.spliterator(), false)
                .map(d -> convert(d))
                .toArray(size -> new org.deku.leo2.rest.entities.v1.Depot[size]);
    }

    public void test() {
        QDepot q = null;
    }

    @Override
    public org.deku.leo2.rest.entities.v1.Depot[] find(String query) {
        query = query.trim();

        // QueryDSL
        Iterable<Depot> depots = mDepotRepository.findAll(
                QDepot.depot.depotMatchcode.contains(query)
                        .or(QDepot.depot.firma1.contains(query))
                        .or(QDepot.depot.firma2.contains(query))
                .or(QDepot.depot.plz.startsWith(query))
                .or(QDepot.depot.lkz.startsWith(query))
                .or(QDepot.depot.ort.contains(query))
                .or(QDepot.depot.strasse.contains(query)), QDepot.depot.depotMatchcode.asc());

        // JPQL
//        String expStartsWith = query + "%";
//        String expSubstring = "%" + query + "%";
//        List<Depot> results = mEntityManager.createQuery("SELECT D FROM Depot D " +
//                "WHERE D.depotMatchcode LIKE :expStartsWith " +
//                "OR D.firma1 LIKE :expSubstring " +
//                "OR D.firma2 LIKE :expSubstring " +
//                "OR D.plz LIKE :expStartsWith " +
//                "OR D.lkz LIKE :expStartsWith " +
//                "OR D.ort LIKE :expSubstring " +
//                "OR D.strasse LIKE :expSubstring " +
//                "ORDER BY D.depotMatchcode")
//                .setParameter("expStartsWith", expStartsWith)
//                .setParameter("expSubstring", expSubstring)
//                .getResultList();

        // JINQ
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

        return StreamSupport.stream(depots.spliterator(), false)
                .map(d -> convert(d))
                .toArray(size -> new org.deku.leo2.rest.entities.v1.Depot[size]);
    }
}
