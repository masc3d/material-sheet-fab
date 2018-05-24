package org.deku.leoz.central.service.internal

import io.reactivex.subjects.PublishSubject
import org.deku.leoz.central.Application
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.TadNodeGeopositionRecord
import org.deku.leoz.central.data.repository.JooqGeopositionRepository
import org.deku.leoz.central.data.repository.JooqNodeRepository
import org.deku.leoz.central.data.repository.JooqUserRepository
import org.deku.leoz.model.UserRole
import org.deku.leoz.model.VehicleType
import org.deku.leoz.node.rest.authorizedUser
import org.deku.leoz.service.internal.LocationServiceV1
import org.deku.leoz.service.internal.LocationServiceV2
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import sx.log.slf4j.trace
import sx.log.slf4j.warn
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.rs.RestProblem
import sx.time.plusMinutes
import sx.time.threeten.toDate
import sx.time.toDate
import sx.time.toLocalDate
import sx.time.toTimestamp
import java.util.*
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response

/**
 * Location service v2
 */
@Component
@Path("internal/v2/location")
@Profile(Application.PROFILE_CENTRAL)
class LocationServiceV2 :
        org.deku.leoz.service.internal.LocationServiceV2,
        MqHandler<Any> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dsl: DSLContext

    @Inject
    private lateinit var userRepository: JooqUserRepository

    @Inject
    private lateinit var posRepository: JooqGeopositionRepository

    @Inject
    private lateinit var nodeRepository: JooqNodeRepository

    @Context
    private lateinit var httpRequest: HttpServletRequest

    private val locationReceivedSubject = PublishSubject.create<LocationServiceV2.GpsMessage>()
    /** Location received event */
    val locationReceived = locationReceivedSubject.hide()

    fun TadNodeGeopositionRecord.toGpsData(): LocationServiceV2.GpsDataPoint {
        val gpsPoint = LocationServiceV2.GpsDataPoint(
                latitude = this.latitude,
                longitude = this.longitude,
                time = this.positionDatetime,
                speed = this.speed?.toFloat(),
                bearing = this.bearing?.toFloat(),
                altitude = this.altitude,
                accuracy = this.accuracy?.toFloat(),
                vehicleType = if (this.vehicleType.isNullOrEmpty()) null else VehicleType.valueOf(this.vehicleType)

        )
        return gpsPoint
    }

    //region REST
    override fun get(userId: Int?, debitorId: Int?, from: Date?, to: Date?): List<LocationServiceV2.GpsData> {
        var debitor_id = debitorId
        val email = when {
            userId != null -> {
                val rUser = userRepository.findById(userId)
                        ?: throw RestProblem(status = Response.Status.BAD_REQUEST, detail = "Invalid user id")

                rUser.email
            }
            else -> null
        }

        val pos_from = from ?: Date().toLocalDate().toDate()
        val pos_to = to ?: Date()

        val gpsdataList = mutableListOf<LocationServiceV2.GpsData>()
        var gpsList = mutableListOf<LocationServiceV2.GpsDataPoint>()

        val authorizedUser = httpRequest.authorizedUser

        if (debitor_id == null && email == null) {
            debitor_id = authorizedUser.debitorId
        }

        when {
            email != null -> {
                val userRecord = userRepository.findByMail(email)
                        ?: throw RestProblem(
                                status = Response.Status.NOT_FOUND,
                                title = "no user found by email")

                if ((authorizedUser.role!! == UserRole.ADMIN)
                        || ((authorizedUser.debitorId == userRecord.debitorId)
                                && (authorizedUser.role!!.value >= UserRole.valueOf(userRecord.role).value))) {

                    val posList = posRepository.findByUserId(userRecord.id, pos_from, pos_to)
                    if (posList != null) {
                        gpsList = filter(posList)
                        /*
                        posList.forEach {
                            gpsList.add(it.toGpsData())
                        }
                        */

                    }
                    gpsdataList.add(LocationServiceV2.GpsData(
                            userId = userRecord.id,
                            gpsDataPoints = gpsList))
                } else {
                    throw RestProblem(
                            status = Response.Status.FORBIDDEN,
                            title = "user found but no permission returning this user")
                }


                return gpsdataList.toList()
            }
            debitor_id != null -> {
                val userRecList = userRepository.findByDebitorId(debitor_id)
                        ?: throw RestProblem(
                                status = Response.Status.NOT_FOUND,
                                title = "no user found by debitor-id")
                if (userRecList.isEmpty())
                    throw RestProblem(
                            status = Response.Status.NOT_FOUND,
                            title = "no user found by debitor-id")
                //val user = mutableListOf<LocationService.User>()
                userRecList.forEach {

                    if ((authorizedUser.role!! == UserRole.ADMIN)
                            || ((authorizedUser.debitorId == it.debitorId)
                                    && (authorizedUser.role!!.value >= UserRole.valueOf(it.role).value))) {

                        val posList = posRepository.findByUserId(it.id, pos_from, pos_to)
                        //gpsList.clear()
                        var gpsListTmp = mutableListOf<LocationServiceV2.GpsDataPoint>()
                        if (posList != null) {
                            /*
                            posList.forEach {
                                gpsListTmp.add(it.toGpsData())
                            }*/
                            gpsListTmp = filter(posList)

                        }
                        gpsdataList.add(LocationServiceV2.GpsData(
                                userId = it.id,
                                gpsDataPoints = gpsListTmp))
                    }
                }
                //if (gpsdataList.isEmpty())
                return gpsdataList.toList()
            }

            else -> {
                // All query params are omitted.
                // We may return all users here at one point, for those who require it
                // In this case we should sensibly check if the user is allowed to do that.

                throw RestProblem(status = Response.Status.BAD_REQUEST)

            }
        }

    }

    override fun getRecent(userId: Int?, debitorId: Int?, duration: Int?): List<LocationServiceV2.GpsData> {
        var debitor_id = debitorId
        val email = when {
            userId != null -> {
                val rUser = userRepository.findById(userId)
                        ?: throw RestProblem(status = Response.Status.BAD_REQUEST, detail = "Invalid user id")

                rUser.email
            }
            else -> null
        }

        val gpsdataList = mutableListOf<LocationServiceV2.GpsData>()
        var gpsList = mutableListOf<LocationServiceV2.GpsDataPoint>()

        val authorizedUser = httpRequest.authorizedUser

        if (debitor_id == null && email == null) {
            debitor_id = authorizedUser.debitorId
        }

        when {
            email != null -> {
                val userRecord = userRepository.findByMail(email)
                        ?: throw RestProblem(
                                status = Response.Status.NOT_FOUND,
                                title = "no user found by email")

                if ((authorizedUser.role!! == UserRole.ADMIN)
                        || ((authorizedUser.debitorId == userRecord.debitorId)
                                && (authorizedUser.role!!.value >= UserRole.valueOf(userRecord.role).value))) {

                    val posList: List<TadNodeGeopositionRecord>?
                    if (duration != null) {
                        val pos_to = Date()
                        val pos_from = Date().plusMinutes(duration * -1)
                        posList = posRepository.findByUserId(userRecord.id, pos_from, pos_to)
                    } else {
                        posList = posRepository.findRecentByUserId(userRecord.id)
                    }

                    if (posList != null) {
                        gpsList = filter(posList)
                        /*
                        posList.forEach {
                            gpsList.add(it.toGpsData())
                        }
                        */

                    }
                    gpsdataList.add(LocationServiceV2.GpsData(
                            userId = userRecord.id,
                            gpsDataPoints = gpsList))
                } else {
                    throw RestProblem(
                            status = Response.Status.FORBIDDEN,
                            title = "user found but no permission returning this user")
                }


                return gpsdataList.toList()
            }
            debitor_id != null -> {
                val userRecList = userRepository.findByDebitorId(debitor_id)
                        ?: throw RestProblem(
                                status = Response.Status.NOT_FOUND,
                                title = "no user found by debitor-id")
                if (userRecList.isEmpty())
                    throw RestProblem(
                            status = Response.Status.NOT_FOUND,
                            title = "no user found by debitor-id")
                //val user = mutableListOf<LocationService.User>()
                userRecList.forEach {

                    if ((authorizedUser.role!! == UserRole.ADMIN)
                            || ((authorizedUser.debitorId == it.debitorId)
                                    && (authorizedUser.role!!.value >= UserRole.valueOf(it.role).value))) {

                        val posList: List<TadNodeGeopositionRecord>?
                        if (duration != null) {
                            val pos_to = Date()
                            val pos_from = Date().plusMinutes(duration * -1)
                            posList = posRepository.findByUserId(it.id, pos_from, pos_to)
                        } else {
                            posList = posRepository.findRecentByUserId(it.id)
                        }
                        //gpsList.clear()
                        var gpsListTmp = mutableListOf<LocationServiceV2.GpsDataPoint>()
                        if (posList != null) {
                            /*
                            posList.forEach {
                                gpsListTmp.add(it.toGpsData())
                            }*/
                            gpsListTmp = filter(posList)

                        }
                        gpsdataList.add(LocationServiceV2.GpsData(
                                userId = it.id,
                                gpsDataPoints = gpsListTmp))
                    }
                }
                //if (gpsdataList.isEmpty())
                return gpsdataList.toList()
            }

            else -> {
                // All query params are omitted.
                // We may return all users here at one point, for those who require it
                // In this case we should sensibly check if the user is allowed to do that.

                throw RestProblem(status = Response.Status.BAD_REQUEST)
            }
        }
    }

    override fun getDistance(lonFirst: Double, latFirst: Double, lonSecond: Double, latSecond: Double): Double {
        try {
            if (lonSecond == 0.0 && latSecond == 0.0)
                throw RestProblem(
                        detail = "Invalid geo-data",
                        status = Response.Status.BAD_REQUEST)

            val tLat = (latSecond - latFirst) * Math.PI / 180
            val tLon = (lonSecond - lonFirst) * Math.PI / 180
            val oLat = latFirst * Math.PI / 180
            val oLastLat = latSecond * Math.PI / 180
            val a = Math.pow(Math.sin(tLat / 2), 2.0) + Math.pow(Math.sin(tLon / 2), 2.0) * Math.cos(oLat) * Math.cos(oLastLat)
            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
            val tDist = 6371 * c
            return tDist

        } catch (e: Exception) {
            throw RestProblem(
                    detail = e.toString(),
                    status = Response.Status.BAD_REQUEST)
        }
    }
    //endregion

    /**
     * Filter positional records
     */
    fun filter(posList: List<TadNodeGeopositionRecord>): MutableList<LocationServiceV2.GpsDataPoint> {
        val gpsList = mutableListOf<LocationServiceV2.GpsDataPoint>()

        var lastLon: Double = 0.0
        var lastLat: Double = 0.0
        var lastSpeed: Double = 0.0
        var steps = 0
        for (i in 0..posList.count() - 1) {
            if (i == 0 || i == posList.count() - 1) {
                gpsList.add((posList[i]).toGpsData())
                lastLon = posList[i].longitude
                lastLat = posList[i].latitude
            } else {
                val distance: Double
                if (posList[i].speed > 60) {
                    distance = 2.0
                } else {
                    distance = 0.1
                }
                if (checkDistance(posList[i].longitude, posList[i].latitude, lastLon, lastLat, distance)) {
                    gpsList.add((posList[i]).toGpsData())
                    lastLon = posList[i].longitude
                    lastLat = posList[i].latitude
                    steps = 0
                } else {
                    if (Math.abs(posList[i].speed - lastSpeed) > 5) {
                        gpsList.add((posList[i]).toGpsData())
                        lastLon = posList[i].longitude
                        lastLat = posList[i].latitude
                        steps = 0
                    } else {
                        steps += 1
                        if (steps < 5) {
                            gpsList.add((posList[i]).toGpsData())
                            lastLon = posList[i].longitude
                            lastLat = posList[i].latitude
                        }

                    }
                }
            }
            lastSpeed = posList[i].speed?: 0.0
        }

        return gpsList

    }

    /**
     * TODO: DOC. check distance, like what
     */
    fun checkDistance(lon: Double, lat: Double, lastLon: Double, lastLat: Double, distance: Double): Boolean {
        var check = false

        try {
            if (lastLon == 0.0 && lastLat == 0.0)
                return true
            val tLat = (lastLat - lat) * Math.PI / 180
            val tLon = (lastLon - lon) * Math.PI / 180
            val oLat = lat * Math.PI / 180
            val oLastLat = lastLat * Math.PI / 180
            val a = Math.pow(Math.sin(tLat / 2), 2.0) + Math.pow(Math.sin(tLon / 2), 2.0) * Math.cos(oLat) * Math.cos(oLastLat)
            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
            val tDist = Math.round(6371 * c)//(,1)
            if (tDist > distance)
                check = true

        } catch (e: Exception) {
            check = false
        }

        return check
    }

    //region MQ handler
    /**
     * Location service message handler
     */
    //region MQ handlers
    @MqHandler.Types(
            LocationServiceV1.GpsMessage::class,
            LocationServiceV2.GpsMessage::class
    )
    override fun onMessage(message: Any, replyChannel: MqChannel?) {
        // TODO: from which device are gpsData coming? Add node-id oder user-id to LocationService.GpsData?

        when (message) {
            is LocationServiceV2.GpsMessage -> {
                val dataPoints = message.dataPoints?.toList()
                        ?: throw RestProblem(
                                detail = "Missing data points",
                                status = Response.Status.BAD_REQUEST)

                log.trace { "Received ${dataPoints.count()} from [${message.nodeKey}] user [${message.userId}]" }

                this.locationReceivedSubject.onNext(message)

                dataPoints.forEach {
                    val r = dsl.newRecord(Tables.TAD_NODE_GEOPOSITION)

                    r.userId = message.userId
                    r.nodeId = nodeRepository.findByKey(message.nodeKey ?: "")?.nodeId
                    r.latitude = it.latitude
                    r.longitude = it.longitude
                    r.positionDatetime = it.time?.toTimestamp()
                    r.speed = it.speed?.toDouble()
                    r.bearing = it.bearing?.toDouble()
                    r.altitude = it.altitude
                    r.accuracy = it.accuracy?.toDouble()
                    r.vehicleType = it.vehicleType?.value?.toUpperCase()
//r.debitorId=
//todo
//V81migration
                    //if (message.nodeKey!=null)
                    //    r.node_uid=message.nodeKey
                    posRepository.save(r)
                }
            }
            is LocationServiceV1.GpsMessage -> {
                log.warn { "Received deprecated location message from node [${message.nodeId}] user [${message.userId}]" }
            }
        }
    }

    //endergion
}