package org.deku.leoz.node.data.repository

import org.deku.leoz.node.data.jpa.MstDebitorStation
import org.deku.leoz.node.data.jpa.QMstDebitorStation.mstDebitorStation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import javax.inject.Inject

interface DebitorStationRepository :
        JpaRepository<MstDebitorStation, Int>,
        QuerydslPredicateExecutor<MstDebitorStation>, DebitorStationRepositoryExtension


interface DebitorStationRepositoryExtension {
    fun findStationIdsByDebitorid(debitorId: Int): List<Int>
    fun findByStationId(stationId: Int): MstDebitorStation?
}

class DebitorStationRepositoryImpl : DebitorStationRepositoryExtension {
    @Inject
    private lateinit var debitorStationRepository: DebitorStationRepository

    override fun findStationIdsByDebitorid(debitorId: Int): List<Int> {
        val debitorStations = debitorStationRepository.findAll(
                mstDebitorStation.debitorId.eq(debitorId)
        )
        return debitorStations.map { it.stationId }.toList()
    }

    override fun findByStationId(stationId: Int): MstDebitorStation? {
        return debitorStationRepository.findOne(
                mstDebitorStation.stationId.eq(stationId)
        ).orElse(null)
    }
}