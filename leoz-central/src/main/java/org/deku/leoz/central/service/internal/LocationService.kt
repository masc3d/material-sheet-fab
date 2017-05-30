package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.repository.UserJooqRepository
import org.deku.leoz.node.rest.DefaultProblem
import org.deku.leoz.service.entity.Position
import org.deku.leoz.service.internal.entity.GpsData
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import sx.rs.auth.ApiKey
import sx.time.toTimestamp
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.core.Response
import org.deku.leoz.service.internal.LocationService
import sx.mq.MqChannel
import sx.mq.MqHandler

/**
 * Created by helke on 24.05.17.
 */
@Named
@ApiKey(true)
@Path("internal/v1/location")
class LocationService : LocationService, MqHandler<Position> {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext
    @Inject
    private lateinit var userRepository: UserJooqRepository

    override fun get(email: String?, debitorId: Int?, apiKey: String?): List<GpsData> {
        var debitor_id = debitorId
        var user_id: Int?
        val dtNow = Date()
        //val gpsdata = GpsData(49.9, 9.06, 25.3, dtNow.toTimestamp())
        val pos = Position(49.9, 9.06, 1496060435, 25.3.toFloat(), null, null, null, null)
        val gpsdata = GpsData("foo@bar.com", listOf(pos))
        val gpsdataList = mutableListOf<GpsData>()

        gpsdataList.add(gpsdata)
        return gpsdataList

/*
        if (debitor_id == null && email == null) {
            apiKey ?:
                    throw DefaultProblem(status = Response.Status.BAD_REQUEST)
            val authorizedUserRecord = userRepository.findByKey(apiKey)
            debitor_id = authorizedUserRecord?.debitorId

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
                val user = mutableListOf<User>()
                userRecList.forEach {
                    user.add(it.toUser())
                }
                return user.toList()
            }
            email != null -> {
                val userRecord = userRepository.findByMail(email)
                        ?: throw DefaultProblem(
                        status = Response.Status.NOT_FOUND,
                        title = "no user found by email")
                return listOf(userRecord.toUser())
            }
            else -> {
                // All query params are omitted.
                // We may return all users here at one point, for those who require it
                // In this case we should sensibly check if the user is allowed to do that.

                throw DefaultProblem(status = Response.Status.BAD_REQUEST)

            }
        }
        */
    }

    override fun onMessage(message: Position, replyChannel: MqChannel?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}