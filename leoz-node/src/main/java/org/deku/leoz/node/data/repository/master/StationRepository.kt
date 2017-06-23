package org.deku.leoz.node.data.repository.master

import com.google.common.collect.Lists
import org.deku.leoz.node.data.jpa.MstStation
import org.deku.leoz.node.data.jpa.QMstStation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor
import javax.inject.Inject

/**
 * Station repository
 * Created by masc on 30.04.15.
 */
interface StationRepository :
        JpaRepository<MstStation, Int>,
        QueryDslPredicateExecutor<MstStation>, StationRepositoryExtension

interface StationRepositoryExtension {
    fun findWithQuery(query: String): List<MstStation>
}

class StationRepositoryImpl: StationRepositoryExtension {
    @Inject
    private lateinit var depotRepository: StationRepository

    override fun findWithQuery(query: String): List<MstStation> {
        val token = query.trim { it <= ' ' }

        // QueryDSL
        val qStation = QMstStation.mstStation
        val depots = depotRepository.findAll(
                qStation.stationNr.stringValue().containsIgnoreCase(token)
                        .or(qStation.address1.containsIgnoreCase(token))
                        .or(qStation.address2.containsIgnoreCase(token))
                        .or(qStation.zip.startsWithIgnoreCase(token))
                        .or(qStation.country.startsWithIgnoreCase(token))
                        .or(qStation.city.containsIgnoreCase(token))
                        .or(qStation.street.containsIgnoreCase(token)),
                qStation.stationNr.asc())

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

        return Lists.newArrayList(depots)
    }
}

