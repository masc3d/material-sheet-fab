package org.deku.leoz.node.data.repository

import org.deku.leoz.node.data.jpa.MstDebitorStation
import org.deku.leoz.node.data.jpa.QMstDebitorStation
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

        val qDebitorStation = QMstDebitorStation.mstDebitorStation

        val debitorStations = debitorStationRepository.findAll(
                qDebitorStation.debitorId.eq(debitorId)
        )
        return debitorStations.map { it.stationId }.toList()
    }

    override fun findByStationId(stationId: Int): MstDebitorStation? {
        val qDebitorStation = QMstDebitorStation.mstDebitorStation
        return debitorStationRepository.findOne(
                qDebitorStation.stationId.eq(stationId)
        ).orElse(null)
    }
}