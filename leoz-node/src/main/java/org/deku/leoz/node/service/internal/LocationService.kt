package org.deku.leoz.node.service.internal

import io.reactivex.subjects.PublishSubject
import org.deku.leoz.model.UserRole
import org.deku.leoz.node.Application
import org.deku.leoz.node.data.jpa.TadNodeGeoposition
import org.deku.leoz.node.data.repository.NodeGeopositionRepository
import org.deku.leoz.node.data.repository.UserRepository
import org.deku.leoz.node.data.repository.toGpsData
import org.deku.leoz.node.data.repository.NodeRepository
import org.deku.leoz.node.rest.authorizedUser
import org.deku.leoz.service.internal.LocationServiceV1
import org.deku.leoz.service.internal.LocationServiceV2
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import sx.log.slf4j.trace
import sx.log.slf4j.warn
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.persistence.transaction
import sx.rs.RestProblem
import sx.time.plusMinutes
import sx.time.toLocalDate
import sx.time.toTimestamp
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceUnit
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response


@Component
@Path("internal/v2/location")
@Profile(Application.PROFILE_CLIENT_NODE)
class LocationService :
        org.deku.leoz.service.internal.LocationServiceV2,
        MqHandler<Any> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var userRepository: UserRepository

    @Inject
    private lateinit var posRepository: NodeGeopositionRepository

    @Inject
    private lateinit var nodeRepository: NodeRepository

    @Context
    private lateinit var httpRequest: HttpServletRequest

    private val locationReceivedSubject = PublishSubject.create<LocationServiceV2.GpsMessage>()
    /** Location received event */
    val locationReceived = locationReceivedSubject.hide()

    @PersistenceUnit(name = org.deku.leoz.node.config.PersistenceConfiguration.QUALIFIER)
    private lateinit var emf: EntityManagerFactory

    override fun get(userId: Int?, debitorId: Int?, from: Date?, to: Date?): List<LocationServiceV2.GpsData> {
        var debitor_id = debitorId
        val email = when {
            userId != null -> {
                val rUser = userRepository.findById(userId.toLong())
                        ?: throw RestProblem(status = Response.Status.BAD_REQUEST, detail = "Invalid user id")

                if (!rUser.isPresent)
                    throw RestProblem(status = Response.Status.BAD_REQUEST, detail = "Invalid user id")
                rUser.get().email
            }
            else -> null
        }

        val pos_from = from ?: SimpleDateFormat("yyyy-MM-dd").parse(Date().toLocalDate().toString())
        val pos_to = to ?: Date()

        val gpsdataList = mutableListOf<LocationServiceV2.GpsData>()
        var gpsList = mutableListOf<LocationServiceV2.GpsDataPoint>()

        val authorizedUser = httpRequest.authorizedUser

        if (debitor_id == null && email == null) {
            debitor_id = authorizedUser.debitorId
        }

        when {
            email != null -> {
                val userRecord = userRepository.findByEmail(email)
                        ?: throw RestProblem(
                                status = Response.Status.NOT_FOUND,
                                title = "no user found by email")

                if ((UserRole.valueOf(authorizedUser.role!!) == UserRole.ADMIN)
                        || ((authorizedUser.debitorId == userRecord.debitorId.toInt())
                                && (UserRole.valueOf(authorizedUser.role!!).value >= UserRole.valueOf(userRecord.role).value))) {

                    val posList=posRepository.findByUserIdAndPositionDatetimeBetweenOrderByPositionDatetime(userRecord.id.toInt(),pos_from,pos_to)
                    if (posList != null) {
                        gpsList = filter(posList)
                        /*
                        posList.forEach {
                            gpsList.add(it.toGpsData())
                        }
                        */

                    }
                    gpsdataList.add(LocationServiceV2.GpsData(
                            userId = userRecord.id.toInt(),
                            gpsDataPoints = gpsList))
                } else {
                    throw RestProblem(
                            status = Response.Status.FORBIDDEN,
                            title = "user found but no permission returning this user")
                }


                return gpsdataList.toList()
            }
            debitor_id != null -> {
                val userRecList = userRepository.findByDebitorId(debitor_id.toLong())

                if (userRecList.isEmpty())
                    throw RestProblem(
                            status = Response.Status.NOT_FOUND,
                            title = "no user found by debitor-id")
                userRecList.forEach {

                    if ((UserRole.valueOf(authorizedUser.role!!) == UserRole.ADMIN)
                            || ((authorizedUser.debitorId == it.debitorId.toInt())
                                    && (UserRole.valueOf(authorizedUser.role!!).value >= UserRole.valueOf(it.role).value))) {

                        val posList=posRepository.findByUserIdAndPositionDatetimeBetweenOrderByPositionDatetime(it.id.toInt(),pos_from,pos_to)
                        var gpsListTmp = mutableListOf<LocationServiceV2.GpsDataPoint>()
                        if (posList != null) {
                            /*
                            posList.forEach {
                                gpsListTmp.add(it.toGpsData())
                            }*/
                            gpsListTmp = filter(posList)

                        }
                        gpsdataList.add(LocationServiceV2.GpsData(
                                userId = it.id.toInt(),
                                gpsDataPoints = gpsListTmp))
                    }
                }
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
                val rUser = userRepository.findById(userId.toLong())
                        ?: throw RestProblem(status = Response.Status.BAD_REQUEST, detail = "Invalid user id")
                if (!rUser.isPresent)
                    throw RestProblem(status = Response.Status.BAD_REQUEST, detail = "Invalid user id")
                rUser.get().email

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
                val userRecord = userRepository.findByEmail(email)
                        ?: throw RestProblem(
                                status = Response.Status.NOT_FOUND,
                                title = "no user found by email")

                if ((UserRole.valueOf(authorizedUser.role!!) == UserRole.ADMIN)
                        || ((authorizedUser.debitorId == userRecord.debitorId.toInt())
                                && (UserRole.valueOf(authorizedUser.role!!).value >= UserRole.valueOf(userRecord.role).value))) {

                    val posList: List<TadNodeGeoposition>?
                    if (duration != null) {
                        val pos_to = Date()
                        val pos_from = Date().plusMinutes(duration * -1)
                        posList=posRepository.findByUserIdAndPositionDatetimeBetweenOrderByPositionDatetime(userRecord.id.toInt(),pos_from,pos_to)
                    } else {
                        val recentRecord =posRepository.findTopByUserIdOrderByPositionDatetimeDesc(userRecord.id.toInt()) //posRepository.findRecentByUserId(userRecord.id.toInt())
                        posList=if(recentRecord!=null)listOf(recentRecord) else null
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
                            userId = userRecord.id.toInt(),
                            gpsDataPoints = gpsList))
                } else {
                    throw RestProblem(
                            status = Response.Status.FORBIDDEN,
                            title = "user found but no permission returning this user")
                }


                return gpsdataList.toList()
            }
            debitor_id != null -> {
                val userRecList = userRepository.findByDebitorId(debitor_id.toLong())
                        ?: throw RestProblem(
                                status = Response.Status.NOT_FOUND,
                                title = "no user found by debitor-id")
                if (userRecList.isEmpty())
                    throw RestProblem(
                            status = Response.Status.NOT_FOUND,
                            title = "no user found by debitor-id")
                userRecList.forEach {

                    if ((UserRole.valueOf(authorizedUser.role!!) == UserRole.ADMIN)
                            || ((authorizedUser.debitorId == it.debitorId.toInt())
                                    && (UserRole.valueOf(authorizedUser.role!!).value >= UserRole.valueOf(it.role).value))) {

                        val posList: List<TadNodeGeoposition>?
                        if (duration != null) {
                            val pos_to = Date()
                            val pos_from = Date().plusMinutes(duration * -1)
                            posList=posRepository.findByUserIdAndPositionDatetimeBetweenOrderByPositionDatetime(it.id.toInt(),pos_from,pos_to)
                        } else {
                            val recentRecord =posRepository.findTopByUserIdOrderByPositionDatetimeDesc(it.id.toInt())
                            posList=if(recentRecord!=null)listOf(recentRecord) else null
                        }
                        var gpsListTmp = mutableListOf<LocationServiceV2.GpsDataPoint>()
                        if (posList != null) {
                            /*
                            posList.forEach {
                                gpsListTmp.add(it.toGpsData())
                            }*/
                            gpsListTmp = filter(posList)

                        }
                        gpsdataList.add(LocationServiceV2.GpsData(
                                userId = it.id.toInt(),
                                gpsDataPoints = gpsListTmp))
                    }
                }
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

    fun filter(posList: List<TadNodeGeoposition>): MutableList<LocationServiceV2.GpsDataPoint> {
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
            lastSpeed = posList[i].speed ?: 0.0
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

                    emf.transaction { em ->
                        val pid = posRepository.findTopByOrderByPositionIdDesc()?.positionId
                                ?: 0 //(QTadNodeGeoposition.tadNodeGeoposition.positionId.max() ?: 0)
                        val rec = TadNodeGeoposition()
                        rec.userId = message.userId
                        rec.nodeId = nodeRepository.findByKey(message.nodeKey ?: "")?.id?.toInt()
                        rec.latitude = it.latitude
                        rec.longitude = it.longitude
                        rec.positionDatetime = it.time?.toTimestamp()
                        rec.speed = it.speed?.toDouble()
                        rec.bearing = it.bearing?.toDouble()
                        rec.altitude = it.altitude
                        rec.accuracy = it.accuracy?.toDouble()
                        rec.vehicleType = it.vehicleType?.value?.toUpperCase()
                        //rec.debitorId=
                        rec.tsCreated = java.util.Date().toTimestamp()
                        //sync_id??
                        rec.syncId = 0

                        rec.positionId = pid + 1

                        em.persist(rec)
                        em.flush()
                    }


                }
            }
            is LocationServiceV1.GpsMessage -> {
                log.warn { "Received deprecated location message from node [${message.nodeId}] user [${message.userId}]" }
            }
        }
    }

}