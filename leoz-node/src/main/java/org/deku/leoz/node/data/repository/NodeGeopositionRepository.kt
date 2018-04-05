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
        QuerydslPredicateExecutor<TadNodeGeoposition>, NodeGeopositionRepositoryExtension,NodeGeopositionRepositoryExtensionGeneral

interface NodeGeopositionRepositoryExtension {

}

interface NodeGeopositionRepositoryExtensionGeneral{
    fun findTopByOrderByPositionIdDesc():TadNodeGeoposition?
    fun findTopByUserIdOrderByPositionDatetimeDesc(id: Int):TadNodeGeoposition?
    fun findByUserIdAndPositionDatetimeBetweenOrderByPositionDatetime(id: Int, from: Date, to: Date): List<TadNodeGeoposition>
}

class NodeGeopostionRepositoryImpl : NodeGeopositionRepositoryExtension {
    @Inject
    private lateinit var posRepository: NodeGeopositionRepository

    @PersistenceContext
    private lateinit var em: EntityManager

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