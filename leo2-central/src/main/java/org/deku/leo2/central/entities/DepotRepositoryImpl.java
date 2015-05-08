package org.deku.leo2.central.entities;

import com.google.common.collect.Lists;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * Created by masc on 07.05.15.
 */
@Named
public class DepotRepositoryImpl implements DepotRepositoryCustom {
    @Inject
    DepotRepository mDepotRepository;

    @Transactional("jpa")
    public List<Depot> findAll() {
        return Lists.newArrayList(mDepotRepository.findAll());
    }

    @Transactional("jpa")
    @Override
    public List<Depot> findWithQuery(String query) {
        query = query.trim();

        // QueryDSL
        QDepot depot = QDepot.depot;
        Iterable<Depot> depots = mDepotRepository.findAll(
                depot.depotMatchcode.contains(query)
                        .or(depot.firma1.contains(query))
                        .or(depot.firma2.contains(query))
                        .or(depot.plz.startsWith(query))
                        .or(depot.lkz.startsWith(query))
                        .or(depot.ort.contains(query))
                        .or(depot.strasse.contains(query)), depot.depotMatchcode.asc());

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

        return Lists.newArrayList(depots);
    }
}
