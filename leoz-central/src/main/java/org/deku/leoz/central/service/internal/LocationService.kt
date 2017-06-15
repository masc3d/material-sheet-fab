package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.records.MstUserRecord
import org.deku.leoz.central.data.jooq.tables.records.TrnNodeGeopositionRecord
import org.deku.leoz.central.data.repository.*
import org.deku.leoz.node.rest.DefaultProblem
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import sx.rs.auth.ApiKey
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import org.deku.leoz.service.internal.LocationService
import org.deku.leoz.model.UserRole
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.time.minusMinutes
import sx.time.plusDays
import sx.time.toTimestamp
import javax.ws.rs.core.Response
import org.slf4j.LoggerFactory
import sx.logging.slf4j.info


/**
 * Created by helke on 24.05.17.
 */
@Named
@ApiKey(true)
@Path("internal/v1/location")
//class LocationService : LocationService, MqHandler<LocationService.GpsDataPoint> {
class LocationService : LocationService, MqHandler<LocationService.GpsData> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext
    @Inject
    private lateinit var userRepository: UserJooqRepository
    @Inject
    private lateinit var posRepository: PositionJooqRepository

    override fun get(email: String?, debitorId: Int?, from: Date?, to: Date?, apiKey: String?): List<LocationService.GpsData> {
        var debitor_id = debitorId
        //var user_id: Int?
        val pos_from = from ?: Date(Date().year, Date().month, Date().date)
        val pos_to = to ?: Date()//.plusDays(1) //pos_from.plusDays(1)

        val dtNow = Date()
        //val gpsdata = GpsData(49.9, 9.06, 25.3, dtNow.toTimestamp())
        /*
        val pos = LocationService.GpsDataPoint(
                latitude = 49.9,
                longitude = 9.06,
                time = Date(),
                speed = 25.3.toFloat())
*/
        //  val gpsdata = LocationService.GpsData("foo@bar.com", listOf(pos))
        var gpsdataList = mutableListOf<LocationService.GpsData>()
        var gpsList = mutableListOf<LocationService.GpsDataPoint>()

        //  gpsdataList.add(gpsdata)
        //return gpsdataList


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
                val user = mutableListOf<LocationService.User>()
                userRecList.forEach {

                    if ((UserRole.valueOf(authorizedUserRecord.role) == UserRole.ADMIN)
                            || ((authorizedUserRecord.debitorId == it.debitorId)
                            && (UserRole.valueOf(authorizedUserRecord.role).value >= UserRole.valueOf(it.role).value))) {

                        val posList = posRepository.findByUserId(it.id, pos_from, pos_to)
                        //gpsList.clear()
                        var gpsListTmp = mutableListOf<LocationService.GpsDataPoint>()
                        if (posList != null) {
                            /*
                            posList.forEach {
                                gpsListTmp.add(it.toGpsData())
                            }*/
                            gpsListTmp = geoFilter(posList)

                        }
                        gpsdataList.add(LocationService.GpsData(it.email, gpsListTmp))
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
                    gpsdataList.add(LocationService.GpsData(userRecord.email, gpsList))
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

    override fun getRecent(email: String?, debitorId: Int?, duration: Int?, apiKey: String?): List<LocationService.GpsData> {
        var debitor_id = debitorId

        var gpsdataList = mutableListOf<LocationService.GpsData>()
        var gpsList = mutableListOf<LocationService.GpsDataPoint>()



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
                val user = mutableListOf<LocationService.User>()
                userRecList.forEach {

                    if ((UserRole.valueOf(authorizedUserRecord.role) == UserRole.ADMIN)
                            || ((authorizedUserRecord.debitorId == it.debitorId)
                            && (UserRole.valueOf(authorizedUserRecord.role).value >= UserRole.valueOf(it.role).value))) {

                        val posList: List<TrnNodeGeopositionRecord>?
                        if (duration != null) {
                            val pos_to = Date()
                            val pos_from = Date().minusMinutes(duration)
                            posList = posRepository.findByUserId(it.id, pos_from, pos_to)
                        } else {
                            posList = posRepository.findRecentByUserId(it.id)
                        }
                        //gpsList.clear()
                        var gpsListTmp = mutableListOf<LocationService.GpsDataPoint>()
                        if (posList != null) {
                            /*
                            posList.forEach {
                                gpsListTmp.add(it.toGpsData())
                            }*/
                            gpsListTmp = geoFilter(posList)

                        }
                        gpsdataList.add(LocationService.GpsData(it.email, gpsListTmp))
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

                    val posList: List<TrnNodeGeopositionRecord>?
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
                    gpsdataList.add(LocationService.GpsData(userRecord.email, gpsList))
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

    //?? from which device are gpsData coming? Add node-id oder user-id to LocationService.GpsData?
    override fun onMessage(message: LocationService.GpsData, replyChannel: MqChannel?) {
        //override fun onMessage(message: LocationService.GpsDataPoint, replyChannel: MqChannel?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        try {
            //log.info(message)

            val geoList = message.gpsDataPoints
            val userRecord: MstUserRecord?
            val email = message.userEmail
            var userId: Int? = null
            if (email != null) {
                userRecord = userRepository.findByMail(email)
                userId = userRecord?.id
            }

            //val geoPos = message
            geoList?.forEach {
                val geoPos = it
                val geoposRec = dslContext.newRecord(Tables.TRN_NODE_GEOPOSITION)
                if (geoPos.latitude != null)
                    geoposRec.latitude = geoPos.latitude
                if (geoPos.longitude != null)
                    geoposRec.longitude = geoPos.longitude

                if (geoPos.time != null)
                    geoposRec.positionDatetime = geoPos.time?.toTimestamp()
                if (geoPos.speed != null)
                    geoposRec.speed = geoPos.speed?.toDouble()
                if (geoPos.bearing != null)
                    geoposRec.bearing = geoPos.bearing?.toDouble()
                if (geoPos.altitude != null)
                    geoposRec.altitude = geoPos.altitude
                if (geoPos.accuracy != null)
                    geoposRec.accuracy = geoPos.accuracy?.toDouble()

                if (userId != null) {
                    geoposRec.userId = userId
                }

                posRepository.save(geoposRec)
            }


        } catch (e: Exception) {
            log.error(e.message, e)
        }

    }

    fun geoFilter(posList: List<TrnNodeGeopositionRecord>): MutableList<LocationService.GpsDataPoint> {
        var gpsList = mutableListOf<LocationService.GpsDataPoint>()

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
            val tLat = (lastLat - lat) * 3.1415 / 180
            val tLon = (lastLon - lon) * 3.1415 / 180
            val oLat = lat * 3.1415 / 180
            val oLastLat = lastLat * 3.1415 / 180
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