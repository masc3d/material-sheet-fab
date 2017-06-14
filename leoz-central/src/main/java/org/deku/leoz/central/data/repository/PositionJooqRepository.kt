package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import javax.inject.Inject
import javax.inject.Named
import org.deku.leoz.central.data.jooq.tables.records.TrnNodeGeopositionRecord
import sx.time.toTimestamp
import java.util.*
import org.deku.leoz.service.internal.LocationService

/**
 * Created by helke on 29.05.17.
 */
@Named
class PositionJooqRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    lateinit var dslContext: DSLContext

    fun findByUserId(id: Int, from: Date, to: Date): List<TrnNodeGeopositionRecord>? {

        return dslContext
                .select()
                .from(Tables.TRN_NODE_GEOPOSITION)
                .where(Tables.TRN_NODE_GEOPOSITION.USER_ID.eq(id))
                .and(Tables.TRN_NODE_GEOPOSITION.POSITION_DATETIME.between(from.toTimestamp(), to.toTimestamp()))
                .orderBy((Tables.TRN_NODE_GEOPOSITION.POSITION_DATETIME))
                .fetchInto(TrnNodeGeopositionRecord::class.java)
    }

    fun findRecentByUserId(id: Int): List<TrnNodeGeopositionRecord>? {

        return dslContext
                .select()
                .from(Tables.TRN_NODE_GEOPOSITION)
                .where(Tables.TRN_NODE_GEOPOSITION.USER_ID.eq(id))
                //.and(Tables.TRN_NODE_GEOPOSITION.POSITION_DATETIME.between(from.toTimestamp(), to.toTimestamp()))
                .orderBy((Tables.TRN_NODE_GEOPOSITION.POSITION_DATETIME.desc()))
                .limit(1)
                .fetchInto(TrnNodeGeopositionRecord::class.java)
    }

    fun save(geopositionRecord: TrnNodeGeopositionRecord): Boolean {
        geopositionRecord ?: return false
        return (geopositionRecord.store() > 0)
    }

}

fun TrnNodeGeopositionRecord.toGpsData(): LocationService.GpsDataPoint {
    val gpsPoint = LocationService.GpsDataPoint(
            this.latitude,
            this.longitude,
            this.positionDatetime,
            this.speed?.toFloat(),
            this.bearing?.toFloat(),
            this.altitude,
            this.accuracy?.toFloat()

    )
    return gpsPoint
}
