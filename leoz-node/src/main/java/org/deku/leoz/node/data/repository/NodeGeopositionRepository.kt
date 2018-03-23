package org.deku.leoz.node.data.repository

import org.deku.leoz.node.data.jpa.QTadNodeGeoposition
import org.deku.leoz.node.data.jpa.QTadNodeGeoposition.tadNodeGeoposition
import org.deku.leoz.node.data.jpa.TadNodeGeoposition
import org.deku.leoz.service.internal.LocationServiceV2
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import sx.persistence.querydsl.from
import sx.time.toLocalDateTime
import sx.time.toSqlDate
import sx.time.toTimestamp
import java.util.*
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface NodeGeopositionRepository :
        JpaRepository<TadNodeGeoposition, Long>,
        QuerydslPredicateExecutor<TadNodeGeoposition>, NodeGeopositionRepositoryExtension

interface NodeGeopositionRepositoryExtension {
    fun findByUserIdAndPositionDatetimeAndPositionDatetime(id: Int, from: Date, to: Date): List<TadNodeGeoposition>
    fun findRecentByUserId(id: Int): List<TadNodeGeoposition>?
}

class NodeGeopostionRepositoryImpl : NodeGeopositionRepositoryExtension {
    @Inject
    private lateinit var posRepository: NodeGeopositionRepository

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun findByUserIdAndPositionDatetimeAndPositionDatetime(id: Int, from: Date, to: Date): List<TadNodeGeoposition> {
        return posRepository.findAll(
                tadNodeGeoposition.userId.eq(id)
                        .and(tadNodeGeoposition.positionDatetime.between(from.toTimestamp(), to.toTimestamp()))
        ).toList().sortedBy { it.positionDatetime }

    }

    override fun findRecentByUserId(id: Int): List<TadNodeGeoposition>? {
        return em.from(tadNodeGeoposition)
                .where(tadNodeGeoposition.userId.eq(id))
                .orderBy(tadNodeGeoposition.positionDatetime.desc())
                .limit(1).fetch()
    }
}


fun TadNodeGeoposition.toGpsData(): LocationServiceV2.GpsDataPoint {
    val gpsPoint = org.deku.leoz.service.internal.LocationServiceV2.GpsDataPoint(
            latitude = this.latitude,
            longitude = this.longitude,
            time = this.positionDatetime,
            speed = this.speed?.toFloat(),
            bearing = this.bearing?.toFloat(),
            altitude = this.altitude,
            accuracy = this.accuracy?.toFloat(),
            vehicleType = if (this.vehicleType.isNullOrEmpty()) null else org.deku.leoz.model.VehicleType.valueOf(this.vehicleType)

    )
    return gpsPoint
}