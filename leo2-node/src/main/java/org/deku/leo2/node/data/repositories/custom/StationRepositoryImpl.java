package org.deku.leo2.node.data.repositories.custom;

import com.google.common.collect.Lists;
import org.deku.leo2.node.data.entities.QStation;
import org.deku.leo2.node.data.entities.Station;
import org.deku.leo2.node.data.repositories.StationRepository;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by masc on 07.05.15.
 */
public class StationRepositoryImpl implements StationRepositoryCustom {
    @Inject
    StationRepository mDepotRepository;

    @Override
    public List<Station> findWithQuery(String query) {
        query = query.trim();

        // QueryDSL
        QStation station = QStation.station;
        Iterable<Station> depots = mDepotRepository.findAll(
                station.stationNr.stringValue().contains(query)
                        .or(station.address1.contains(query))
                        .or(station.address2.contains(query))
                        .or(station.zip.startsWith(query))
                        .or(station.country.startsWith(query))
                        .or(station.city.contains(query))
                        .or(station.street.contains(query)), station.stationNr.asc());

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