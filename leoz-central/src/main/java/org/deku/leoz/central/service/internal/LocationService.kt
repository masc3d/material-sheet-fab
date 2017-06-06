package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.repository.PositionJooqRepository
import org.deku.leoz.central.data.repository.UserJooqRepository
import org.deku.leoz.central.data.repository.toGpsData
import org.deku.leoz.central.data.repository.toLocationServiceUser
import org.deku.leoz.node.rest.DefaultProblem
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import sx.rs.auth.ApiKey
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import org.deku.leoz.service.internal.LocationService
import org.deku.leoz.service.internal.entity.UserRole
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.time.plusDays
import javax.ws.rs.core.Response

/**
 * Created by helke on 24.05.17.
 */
@Named
@ApiKey(true)
@Path("internal/v1/location")
class LocationService : LocationService, MqHandler<LocationService.GpsDataPoint> {
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
        val pos_from = from ?: Date()
        val pos_to = to ?: pos_from.plusDays(1)

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

                    if ((UserRole.valueOf(authorizedUserRecord.role) == UserRole.Admin)
                            || ((authorizedUserRecord.debitorId == it.debitorId)
                            && (UserRole.valueOf(authorizedUserRecord.role).value >= UserRole.valueOf(it.role).value))) {

                        val posList = posRepository.findByUserId(it.id, pos_from, pos_to)
                        //gpsList.clear()
                        var gpsListTmp = mutableListOf<LocationService.GpsDataPoint>()
                        if (posList != null) {
                            posList.forEach {
                                gpsListTmp.add(it.toGpsData())
                            }

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

                if ((UserRole.valueOf(authorizedUserRecord.role) == UserRole.Admin)
                        || ((authorizedUserRecord.debitorId == userRecord.debitorId)
                        && (UserRole.valueOf(authorizedUserRecord.role).value >= UserRole.valueOf(userRecord.role).value))) {

                    val posList = posRepository.findByUserId(userRecord.id, pos_from, pos_to)
                    if (posList != null) {
                        posList.forEach {
                            gpsList.add(it.toGpsData())
                        }

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

    override fun onMessage(message: LocationService.GpsDataPoint, replyChannel: MqChannel?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}