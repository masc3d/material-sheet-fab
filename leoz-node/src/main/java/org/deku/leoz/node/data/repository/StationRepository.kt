package org.deku.leoz.node.data.repository

import com.google.common.collect.Lists
import org.deku.leoz.node.data.jpa.*
import org.deku.leoz.node.data.jpa.QMstStation.mstStation
import org.deku.leoz.service.internal.entity.Address
import org.deku.leoz.service.internal.entity.GeoLocation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import javax.inject.Inject
import org.deku.leoz.node.data.jpa.QMstStationUser.mstStationUser

/**
 * Station repository
 * Created by masc on 30.04.15.
 */
interface StationRepository :
        JpaRepository<MstStation, Int>,
        QuerydslPredicateExecutor<MstStation>, StationRepositoryExtension


interface StationRepositoryExtension {
    fun findWithQuery(query: String): List<MstStation>
    fun findByStationNo(stationNo: Int): MstStation?
    fun findByStationIds(stationIds: List<Int>): List<MstStation>
    fun findAllowedStationsByUserId(userId: Long): List<Int>
    fun findStationsByDebitorId(debitorId: Long): List<Int>
}

class StationRepositoryImpl : StationRepositoryExtension {

    @Inject
    private lateinit var depotRepository: StationRepository

    @Inject
    private lateinit var stationUserRepository: StationUserRepository

    @Inject
    private lateinit var stationContractRepo: StationContractRepository


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

    override fun findByStationNo(stationNo: Int): MstStation? {
        // QueryDSL
        return depotRepository.findOne(
                mstStation.stationNr.eq(stationNo))
                .orElse(null)
    }

    override fun findByStationIds(stationIds: List<Int>): List<MstStation> {
        // QueryDSL
        return depotRepository.findAll(
                mstStation.stationId.`in`(stationIds)
        ).toList()
    }

    override fun findAllowedStationsByUserId(userId: Long): List<Int> {
        val stationIds = stationUserRepository.findAll(
                mstStationUser.userId.eq(userId)
        ).map { x -> x.stationId.toInt() }.toList()

        val stationRecords = depotRepository.findByStationIds(stationIds)

        return stationRecords.map { it.stationNr }

    }

    override fun findStationsByDebitorId(debitorId: Long): List<Int> {
        val stationIds = stationContractRepo.findStationIds(debitorId.toInt())
        val stationRecords = depotRepository.findByStationIds(stationIds)
        return stationRecords.map { it.stationNr }
    }
}

/**
 * Convert station record to domain address
 */
fun MstStation.toAddress(): Address =
        Address(
                street = this.street,
                streetNo = this.houseNr,
                zipCode = this.zip,
                countryCode = this.country,
                city = this.city,
                geoLocation = if (
                        this.posLat != null && this.posLong != null &&
                        this.posLat != 0.0 && this.posLong != 0.0)
                    GeoLocation(
                            latitude = this.posLat,
                            longitude = this.posLong
                    ) else null
        )

