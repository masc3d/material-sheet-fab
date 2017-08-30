package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.records.TadNodeGeopositionRecord
import org.deku.leoz.central.data.repository.*
import org.deku.leoz.node.rest.DefaultProblem
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import sx.rs.auth.ApiKey
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import org.deku.leoz.service.internal.LocationServiceV1
import org.deku.leoz.model.UserRole
import org.deku.leoz.model.VehicleType
import org.deku.leoz.service.internal.LocationServiceV2
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.time.minusMinutes
import sx.time.toTimestamp
import javax.ws.rs.core.Response
import org.slf4j.LoggerFactory
import sx.time.toLocalDate
import java.text.SimpleDateFormat

/**
 * Created by helke on 24.05.17.
 */
@Named
@ApiKey(true)
@Path("internal/v1/location")
open class LocationServiceV1
    :
        org.deku.leoz.service.internal.LocationServiceV1,
        MqHandler<LocationServiceV1.GpsMessage> {

    fun TadNodeGeopositionRecord.toGpsData(): LocationServiceV1.GpsDataPoint {
        val gpsPoint = LocationServiceV1.GpsDataPoint(
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

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext
    @Inject
    private lateinit var userRepository: UserJooqRepository
    @Inject
    private lateinit var posRepository: PositionJooqRepository

    override fun get(email: String?, debitorId: Int?, from: Date?, to: Date?, apiKey: String?): List<LocationServiceV1.GpsData> {
        var debitor_id = debitorId

        val pos_from = from ?: SimpleDateFormat("yyyy-MM-dd").parse(Date().toLocalDate().toString())
        val pos_to = to ?: Date()

        val gpsdataList = mutableListOf<LocationServiceV1.GpsData>()
        var gpsList = mutableListOf<LocationServiceV1.GpsDataPoint>()

        apiKey ?:
                throw DefaultProblem(status = Response.Status.BAD_REQUEST)
        val authorizedUserRecord = userRepository.findByKey(apiKey)
        authorizedUserRecord ?:
                throw DefaultProblem(status = Response.Status.BAD_REQUEST)

        if (!authorizedUserRecord.isActive) {
            throw DefaultProblem(
                    title = "login user deactivated",
                    status = Response.Status.UNAUTHORIZED)
        }
        if (Date() > authorizedUserRecord.expiresOn) {
            throw DefaultProblem(
                    title = "login user account expired",
                    status = Response.Status.UNAUTHORIZED)
        }

        if (debitor_id == null && email == null) {
            debitor_id = authorizedUserRecord.debitorId
        }

        when {
            debitor_id != null -> {
                val userRecList = userRepository.findByDebitorId(debitor_id)
                        ?: throw DefaultProblem(
                        status = Response.Status.NOT_FOUND,
                        title = "no user found by debitor-id")
                if (userRecList.isEmpty())
                    throw DefaultProblem(
                            status = Response.Status.NOT_FOUND,
                            title = "no user found by debitor-id")
                //val user = mutableListOf<LocationService.User>()
                userRecList.forEach {

                    if ((UserRole.valueOf(authorizedUserRecord.role) == UserRole.ADMIN)
                            || ((authorizedUserRecord.debitorId == it.debitorId)
                            && (UserRole.valueOf(authorizedUserRecord.role).value >= UserRole.valueOf(it.role).value))) {

                        val posList = posRepository.findByUserId(it.id, pos_from, pos_to)
                        //gpsList.clear()
                        var gpsListTmp = mutableListOf<LocationServiceV1.GpsDataPoint>()
                        if (posList != null) {
                            /*
                            posList.forEach {
                                gpsListTmp.add(it.toGpsData())
                            }*/
                            gpsListTmp = geoFilter(posList)

                        }
                        gpsdataList.add(LocationServiceV1.GpsData(
                                userId = it.id,
                                userEmail = it.email,
                                gpsDataPoints = gpsListTmp))
                    }
                }
                //if (gpsdataList.isEmpty())
                return gpsdataList.toList()
            }
            email != null -> {
                val userRecord = userRepository.findByMail(email)
                        ?: throw DefaultProblem(
                        status = Response.Status.NOT_FOUND,
                        title = "no user found by email")

                if ((UserRole.valueOf(authorizedUserRecord.role) == UserRole.ADMIN)
                        || ((authorizedUserRecord.debitorId == userRecord.debitorId)
                        && (UserRole.valueOf(authorizedUserRecord.role).value >= UserRole.valueOf(userRecord.role).value))) {

                    val posList = posRepository.findByUserId(userRecord.id, pos_from, pos_to)
                    if (posList != null) {
                        gpsList = geoFilter(posList)
                        /*
                        posList.forEach {
                            gpsList.add(it.toGpsData())
                        }
                        */

                    }
                    gpsdataList.add(LocationServiceV1.GpsData(
                            userId = userRecord.id,
                            userEmail = userRecord.email,
                            gpsDataPoints = gpsList))
                } else {
                    throw DefaultProblem(
                            status = Response.Status.FORBIDDEN,
                            title = "user found but no permission returning this user")
                }


                return gpsdataList.toList()
            }
            else -> {
                // All query params are omitted.
                // We may return all users here at one point, for those who require it
                // In this case we should sensibly check if the user is allowed to do that.

                throw DefaultProblem(status = Response.Status.BAD_REQUEST)

            }
        }

    }

    override fun getRecent(email: String?, debitorId: Int?, duration: Int?, apiKey: String?): List<LocationServiceV1.GpsData> {
        var debitor_id = debitorId

        val gpsdataList = mutableListOf<LocationServiceV1.GpsData>()
        var gpsList = mutableListOf<LocationServiceV1.GpsDataPoint>()

        apiKey ?:
                throw DefaultProblem(status = Response.Status.BAD_REQUEST)
        val authorizedUserRecord = userRepository.findByKey(apiKey)
        authorizedUserRecord ?:
                throw DefaultProblem(status = Response.Status.BAD_REQUEST)

        if (!authorizedUserRecord.isActive) {
            throw DefaultProblem(
                    title = "login user deactivated",
                    status = Response.Status.UNAUTHORIZED)
        }
        if (Date() > authorizedUserRecord.expiresOn) {
            throw DefaultProblem(
                    title = "login user account expired",
                    status = Response.Status.UNAUTHORIZED)
        }

        if (debitor_id == null && email == null) {
            debitor_id = authorizedUserRecord.debitorId
        }

        when {

            debitor_id != null -> {
                val userRecList = userRepository.findByDebitorId(debitor_id)
                        ?: throw DefaultProblem(
                        status = Response.Status.NOT_FOUND,
                        title = "no user found by debitor-id")
                if (userRecList.isEmpty())
                    throw DefaultProblem(
                            status = Response.Status.NOT_FOUND,
                            title = "no user found by debitor-id")
                //val user = mutableListOf<LocationService.User>()
                userRecList.forEach {

                    if ((UserRole.valueOf(authorizedUserRecord.role) == UserRole.ADMIN)
                            || ((authorizedUserRecord.debitorId == it.debitorId)
                            && (UserRole.valueOf(authorizedUserRecord.role).value >= UserRole.valueOf(it.role).value))) {

                        val posList: List<TadNodeGeopositionRecord>?
                        if (duration != null) {
                            val pos_to = Date()
                            val pos_from = Date().minusMinutes(duration)
                            posList = posRepository.findByUserId(it.id, pos_from, pos_to)
                        } else {
                            posList = posRepository.findRecentByUserId(it.id)
                        }
                        //gpsList.clear()
                        var gpsListTmp = mutableListOf<LocationServiceV1.GpsDataPoint>()
                        if (posList != null) {
                            /*
                            posList.forEach {
                                gpsListTmp.add(it.toGpsData())
                            }*/
                            gpsListTmp = geoFilter(posList)

                        }
                        gpsdataList.add(LocationServiceV1.GpsData(
                                userId = it.id,
                                userEmail = it.email,
                                gpsDataPoints = gpsListTmp))
                    }
                }
                //if (gpsdataList.isEmpty())
                return gpsdataList.toList()
            }
            email != null -> {
                val userRecord = userRepository.findByMail(email)
                        ?: throw DefaultProblem(
                        status = Response.Status.NOT_FOUND,
                        title = "no user found by email")

                if ((UserRole.valueOf(authorizedUserRecord.role) == UserRole.ADMIN)
                        || ((authorizedUserRecord.debitorId == userRecord.debitorId)
                        && (UserRole.valueOf(authorizedUserRecord.role).value >= UserRole.valueOf(userRecord.role).value))) {

                    val posList: List<TadNodeGeopositionRecord>?
                    if (duration != null) {
                        val pos_to = Date()
                        val pos_from = Date().minusMinutes(duration)
                        posList = posRepository.findByUserId(userRecord.id, pos_from, pos_to)
                    } else {
                        posList = posRepository.findRecentByUserId(userRecord.id)
                    }

                    if (posList != null) {
                        gpsList = geoFilter(posList)
                        /*
                        posList.forEach {
                            gpsList.add(it.toGpsData())
                        }
                        */

                    }
                    gpsdataList.add(LocationServiceV1.GpsData(
                            userId = userRecord.id,
                            userEmail = userRecord.email,
                            gpsDataPoints = gpsList))
                } else {
                    throw DefaultProblem(
                            status = Response.Status.FORBIDDEN,
                            title = "user found but no permission returning this user")
                }


                return gpsdataList.toList()
            }
            else -> {
                // All query params are omitted.
                // We may return all users here at one point, for those who require it
                // In this case we should sensibly check if the user is allowed to do that.

                throw DefaultProblem(status = Response.Status.BAD_REQUEST)
            }
        }
    }

    /**
     * Location service message handler
     */
    override fun onMessage(message: LocationServiceV1.GpsMessage, replyChannel: MqChannel?) {
        // TODO: from which device are gpsData coming? Add node-id oder user-id to LocationService.GpsData?

        val dataPoints = message.dataPoints?.toList()
                ?: throw DefaultProblem(
                detail = "Missing data points",
                status = Response.Status.BAD_REQUEST)

        log.trace("Received ${dataPoints.count()} from [${message.nodeId}] user [${message.userId}]")

        dataPoints.forEach {
            val r = dslContext.newRecord(Tables.TAD_NODE_GEOPOSITION)

            r.userId = message.userId
            r.latitude = it.latitude
            r.longitude = it.longitude
            r.positionDatetime = it.time?.toTimestamp()
            r.speed = it.speed?.toDouble()
            r.bearing = it.bearing?.toDouble()
            r.altitude = it.altitude
            r.accuracy = it.accuracy?.toDouble()
            r.vehicleType = it.vehicleType?.value?.toUpperCase()

            posRepository.save(r)
        }
    }

    fun geoFilter(posList: List<TadNodeGeopositionRecord>): MutableList<LocationServiceV1.GpsDataPoint> {
        val gpsList = mutableListOf<LocationServiceV1.GpsDataPoint>()

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
            lastSpeed = posList[i].speed
        }

        return gpsList

    }

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
}

/**
 * Location service v2
 */
@Named
@ApiKey(true)
@Path("internal/v2/location")
class LocationServiceV2 :
        org.deku.leoz.service.internal.LocationServiceV2 {

    @Inject
    private lateinit var userRepository: UserJooqRepository

    @Inject
    private lateinit var v1: LocationServiceV1

    override fun get(userId: Int?, debitorId: Int?, from: Date?, to: Date?, apiKey: String?): List<LocationServiceV2.GpsData> {
        // TODO: simply mapped to v1 for now. this should be properly migrated when v1 is phased out.
        val v1Result: List<LocationServiceV1.GpsData>

        when {
            debitorId != null -> {
                v1Result = v1.get(
                        email = null,
                        debitorId = debitorId,
                        from = from,
                        to = to,
                        apiKey = apiKey
                )
            }

            else -> {
                val email = when {
                    userId != null -> {
                        val rUser = userRepository.findById(userId)
                                ?: throw DefaultProblem(status = Response.Status.BAD_REQUEST, detail = "Invalid user id")

                        rUser.email
                    }
                    else -> null
                }

                v1Result = v1.get(
                        email = email,
                        debitorId = debitorId,
                        from = from,
                        to = to,
                        apiKey = apiKey)
            }
        }

        return v1Result.map {
            LocationServiceV2.GpsData(
                    userId = it.userId,
                    gpsDataPoints = it.gpsDataPoints
            )
        }
    }

    override fun getRecent(userId: Int?, debitorId: Int?, duration: Int?, apiKey: String?): List<LocationServiceV2.GpsData> {
        // TODO: simply mapped to v1 for now. this should be properly migrated when v1 is phased out.
        val v1Result: List<LocationServiceV1.GpsData>

        when {
            debitorId != null -> {
                v1Result = v1.getRecent(
                        email = null,
                        debitorId = debitorId,
                        duration = duration,
                        apiKey = apiKey
                )
            }

            else -> {
                val email = when {
                    userId != null -> {
                        val rUser = userRepository.findById(userId)
                                ?: throw DefaultProblem(status = Response.Status.BAD_REQUEST, detail = "Invalid user id")

                        rUser.email
                    }
                    else -> null
                }

                v1Result = v1.getRecent(
                        email = email,
                        debitorId = debitorId,
                        duration = duration,
                        apiKey = apiKey)
            }
        }

        return v1Result.map {
            LocationServiceV2.GpsData(
                    userId = it.userId,
                    gpsDataPoints = it.gpsDataPoints
            )
        }
    }
}