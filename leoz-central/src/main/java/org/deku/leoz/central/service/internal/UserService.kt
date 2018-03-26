package org.deku.leoz.central.service.internal

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import org.deku.leoz.central.Application
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.repository.JooqMailQueueRepository
import org.deku.leoz.central.data.repository.JooqStationRepository
import org.deku.leoz.central.data.repository.JooqUserRepository
import org.deku.leoz.central.data.repository.JooqUserRepository.Companion.setHashedPassword
import org.deku.leoz.central.data.repository.JooqUserRepository.Companion.verifyPassword
import org.deku.leoz.central.data.repository.toUser
import org.deku.leoz.model.AllowedStations
import org.deku.leoz.model.UserRole
import org.deku.leoz.node.rest.authorizedUser
import org.deku.leoz.service.internal.UserService
import org.deku.leoz.service.internal.UserService.User
import org.deku.leoz.time.toDateWithoutTime
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import sx.rs.RestProblem
import sx.time.toSqlDate
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response

/**
 * User service
 * Created by helke on 15.05.17.
 */
@Component
@Path("internal/v1/user")
@Profile(Application.PROFILE_CENTRAL)
class UserService : org.deku.leoz.service.internal.UserService {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dsl: DSLContext

    @Inject
    private lateinit var userRepository: JooqUserRepository

    @Inject
    private lateinit var mailRepository: JooqMailQueueRepository

    @Inject
    private lateinit var stationRepository: JooqStationRepository

    @Inject
    private lateinit var configurationService: ConfigurationService

    @Context
    private lateinit var httpRequest: HttpServletRequest

    override fun get(email: String?, debitorId: Int?): List<User> {
        var debitor_id = debitorId

        val authorizedUser = httpRequest.authorizedUser

        if (debitor_id == null && email == null) {
            debitor_id = authorizedUser.debitorId
        }

        when {

            debitor_id != null -> {
                val userRecList = userRepository.findByDebitorId(debitor_id)
                        ?: throw RestProblem(
                                status = Response.Status.NOT_FOUND,
                                title = "No user found by debitor-id")
                if (userRecList.isEmpty())
                    throw RestProblem(
                            status = Response.Status.NOT_FOUND,
                            title = "No user found by debitor-id")
                val user = mutableListOf<User>()
                userRecList.forEach {

                    if (authorizedUser.role == UserRole.ADMIN.name) {
                        user.add(it.toUser().also { x -> x.allowedStations = stationRepository.findAllowedStationsByUserId(it.id) })
                    } else {
                        if (authorizedUser.debitorId == it.debitorId) {
                            if (UserRole.valueOf(authorizedUser.role!!).value >= UserRole.valueOf(it.role).value) {
                                user.add(it.toUser().also { x -> x.allowedStations = stationRepository.findAllowedStationsByUserId(it.id) })
                            }
                        }
                    }

                }
                return user.toList()
            }
            email != null -> {
                val userRecord = userRepository.findByMail(email)
                        ?: throw RestProblem(
                                status = Response.Status.NOT_FOUND,
                                title = "No user found by email")

                if (UserRole.valueOf(authorizedUser.role!!) == UserRole.ADMIN)
                    return listOf(userRecord.toUser().also { x -> x.allowedStations = stationRepository.findAllowedStationsByUserId(userRecord.id) })
                if ((UserRole.valueOf(authorizedUser.role!!).value >= UserRole.valueOf(userRecord.role).value) &&
                        (authorizedUser.debitorId == userRecord.debitorId)) {
                    return listOf(userRecord.toUser().also { x -> x.allowedStations = stationRepository.findAllowedStationsByUserId(userRecord.id) })
                } else {
                    throw RestProblem(
                            status = Response.Status.FORBIDDEN,
                            title = "User found but no permission returning this user")
                }
            }
            else -> {
                // All query params are omitted.
                // We may return all users here at one point, for those who require it
                // In this case we should sensibly check if the user is allowed to do that.

                throw RestProblem(status = Response.Status.BAD_REQUEST)

            }
        }
    }


    override fun create(user: User, stationMatchcode: String?, debitorNr: Long?, sendAppLink: Boolean) {

        val rec = userRepository.findByMail(user.email)
        if (rec != null) {
            throw RestProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = "Email exists")
        }

        if (user.debitorId == null) {
            when {
                debitorNr != null -> {
                    user.debitorId = userRepository.findDebitorIdByNr(debitorNr.toDouble())
                }

                !stationMatchcode.isNullOrEmpty() -> {
                    user.debitorId = stationRepository.findByMatchcode(matchcode = stationMatchcode!!).debitorId
                }
            }

        }

        update(email = user.email, user = user, sendAppLink = sendAppLink)
    }

    @Transactional(PersistenceConfiguration.QUALIFIER)
    override fun update(email: String, user: User, sendAppLink: Boolean) {
        val authorizedUser = httpRequest.authorizedUser

        var debitor = user.debitorId

        val userRole = user.role?.toUpperCase()
        val alias = user.alias
        val password = user.password
        val lastName = user.lastName
        val firstName = user.firstName
        val phone = user.phone
        val mobilePhone = user.phoneMobile

        //var allowsStations = List<Int>?//AllowedStations()
        val userStations = user.allowedStations
//        if (userStations != null) {
//            allowsStations.allowedStations = userStations.map { j -> j.toString() }.toList()
//        }
        //val mapper = ObjectMapper()
        //mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)

        var isNew = false
        var rec = userRepository.findByMail(email)
        if (rec == null) {
            isNew = true
            rec = dsl.newRecord(Tables.MST_USER)
            //if (user.email == null || user.email.equals("@"))
            if (user.email.equals("@"))
                user.email = email
            if (!user.email.equals(email)) {
                throw  RestProblem(
                        status = Response.Status.BAD_REQUEST,
                        title = "Multiple email for new record")
            }
        } else {
            if (rec.debitorId == null) rec.debitorId = authorizedUser.debitorId
            if (rec.role == null) rec.role = authorizedUser.role

            if (UserRole.valueOf(authorizedUser.role!!) != UserRole.ADMIN) {
                if (rec.debitorId != authorizedUser.debitorId)
                    throw  RestProblem(
                            status = Response.Status.FORBIDDEN,
                            title = "Login user can not change user - debitorId")
            }

            if (UserRole.valueOf(authorizedUser.role!!).value < UserRole.valueOf(rec.role).value) {
                throw  RestProblem(
                        status = Response.Status.FORBIDDEN,
                        title = "Login user can not create/change user - no permission")
            }

            //if (user.email != null) {
            if (!rec.email.equals(user.email)) {
                if (userRepository.mailExists(user.email)) {
                    throw  RestProblem(
                            status = Response.Status.BAD_REQUEST,
                            title = "Duplicate email")
                }
            }
            //}

            val testAlias: String
            if (alias != null) {
                testAlias = alias
            } else
                testAlias = rec.alias
            val testDebitor: Int
            if (debitor != null)
                testDebitor = debitor
            else
                testDebitor = rec.debitorId

            if (!rec.alias.equals(testAlias) || rec.debitorId != testDebitor) {
                if (userRepository.aliasExists(testAlias, testDebitor)) {
                    throw  RestProblem(
                            status = Response.Status.BAD_REQUEST,
                            title = "Duplicate alias or debitorId")
                }
            }
        }

        if (isNew) {
            alias ?: throw  RestProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = "No alias")
            userRole ?: throw  RestProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = "No user role")
            password ?: throw  RestProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = "No password")
            firstName ?: throw  RestProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = "No first name")
            lastName ?: throw  RestProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = "No last name")
            phone ?: throw  RestProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = "No phone")

            if (user.active == null)
                user.active = false

            if (userRepository.mailExists(user.email))
                throw RestProblem(
                        status = Response.Status.BAD_REQUEST,
                        title = "Duplicate email")

            if (debitor == null)
                debitor = authorizedUser.debitorId

            if (debitor == null)
                throw RestProblem(
                        status = Response.Status.BAD_REQUEST,
                        title = "Missing debitor of login user")

            if (userRepository.aliasExists(alias, debitor))
                throw RestProblem(
                        status = Response.Status.BAD_REQUEST,
                        title = "Duplicate alias/debitor")
        }

        if (!UserRole.values().any { it.name == authorizedUser.role })
            throw  RestProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = "Login user role unknown")

        if (debitor != null) {

            if (UserRole.valueOf(authorizedUser.role!!) != UserRole.ADMIN) {
                if (authorizedUser.debitorId != debitor) {
                    throw  RestProblem(
                            status = Response.Status.FORBIDDEN,
                            title = "Login user can not change debitorId")
                }
            }
        }

        if (userRole != null) {
            if (!UserRole.values().any { it.name == userRole })
                throw  RestProblem(
                        status = Response.Status.BAD_REQUEST,
                        title = "User role unknown")

            if (UserRole.valueOf(authorizedUser.role!!).value < UserRole.valueOf(userRole).value) {
                throw  RestProblem(
                        status = Response.Status.FORBIDDEN,
                        title = "Login user can not create/change user - no permission")
            }
        }

        rec ?: throw  RestProblem(
                status = Response.Status.BAD_REQUEST,
                title = "Not found")

        //if ((user.email != null) && (user.email != "@"))
        if (user.email != "@") {
            if (rec.email != user.email) {
                rec.email = user.email
                if (password == null)
                    throw RestProblem(
                            status = Response.Status.CONFLICT,
                            title = "On login-change you have to provide a password"
                    )
            }
        }
        if (debitor != null)
            rec.debitorId = debitor
        if (alias != null)
            rec.alias = alias
        if (userRole != null)
            rec.role = userRole
        if (password != null)
            rec.setHashedPassword(password)
        if (firstName != null)
            rec.firstname = firstName
        if (lastName != null)
            rec.lastname = lastName
        if (user.active != null) {
            rec.active = user.isActive
            if (user.active == true) {
                // TODO: intransparent. expiry check should be done against one definitive field.
                if (user.expiresOn == null) {
                    // TODO: what if both are null?!
                    if (rec.expiresOn != null) {
                        if (java.util.Date() > rec.expiresOn) {
                            throw RestProblem(
                                    status = Response.Status.CONFLICT,
                                    title = "ExpiresOn invalid to activate user"
                            )
                        }
                    }
                } else {
                    if (java.util.Date() > user.expiresOn) {
                        throw RestProblem(
                                status = Response.Status.CONFLICT,
                                title = "ExpiresOn invalid to activate user"
                        )
                    }
                }
            }
        }
        if (user.externalUser != null)
            rec.externalUser = user.isExternalUser
        if (phone != null)
            rec.phone = phone
        if (mobilePhone != null)
            rec.phoneMobile = mobilePhone
        if (user.expiresOn != null)
            rec.expiresOn = user.expiresOn
        else {
            if (user.active != null && (user.active == false)) {
                rec.expiresOn = java.util.Date().toSqlDate()
            }
        }

//todo read from mst_station_user
//        rec.allowedStations = stations
        rec.store()


        //check auth? evtl erst ab powerUser?

        if (userStations != null) {
            val allowedStations = stationRepository.findAllowedStationsByUserId(rec.id)

            if (userStations != allowedStations) {
                if (UserRole.valueOf(authorizedUser.role!!).value >= UserRole.POWERUSER.value) {

                    val possibleStations = stationRepository.findStationsByDebitorId(rec.debitorId).map { x->x.toInt() }

                    userStations.forEach {
                        if (!possibleStations.contains(it))
                            throw  RestProblem(
                                    status = Response.Status.CONFLICT,
                                    title = "Allowed station mismatch debitor ")
                        if (!allowedStations.contains(it)) {
                            //Insert into mst_station_user
                            val recStation = dsl.newRecord(Tables.MST_STATION_USER)
                            recStation.userId = rec.id
                            val stationId = stationRepository.findStationIdByDepotNr(it)
                            stationId ?: throw RestProblem(
                                    status = Response.Status.NOT_FOUND,
                                    title = "Station with No [$it] not found"
                            )
                            recStation.stationId = stationId
                            recStation.store()
                        }
                    }
                    allowedStations.forEach {
                        if (!userStations.contains(it)) {
                            //delete from mst_station_user
                            val stationId = stationRepository.findStationIdByDepotNr(it)
                            if (stationId != null) {
                                dsl.deleteFrom(Tables.MST_STATION_USER)
                                        .where(Tables.MST_STATION_USER.USER_ID.eq(rec.id))
                                        .and(Tables.MST_STATION_USER.STATION_ID.eq(stationId))
                                        .execute()
                            }
                        }
                    }

                } else {
                    throw  RestProblem(
                            status = Response.Status.FORBIDDEN,
                            title = "Login user can not set/change allowedStations - no permission")
                }
            }
        }

        if (sendAppLink) {
            sendDownloadLink(userRepository.findByMail(user.email)!!.id!!)
        }
    }

    override fun getById(userId: Int): User {
        val u = userRepository.findById(userId)
                ?: throw NoSuchElementException("User with ID [$userId] not found")

        return this.get(email = u.email).first()
    }

    override fun getIdsByDebitor(debitorId: Int): List<Int> {
        return userRepository.findUserIdsByDebitor(debitorId)
    }

    override fun sendDownloadLink(userId: Int): Boolean {
        var phone = userRepository.findById(userId)!!.phoneMobile!!

        phone = phone.replace("\\D".toRegex(), "")
        phone = phone.removePrefix("00")

        if (phone.first() == '0')
            phone = phone.replaceFirst("0", "49")

        try {
            mailRepository.insertSms(receiver = phone, message = "Hallo und willkommen bei mobileX. Download: http://derkurier.de/mobileX/ Zugangsdaten erhalten Sie in Ihrer Station.")
        } catch (e: Exception) {
            return false
        }

        return true
    }

    @Transactional(PersistenceConfiguration.QUALIFIER)
    override fun changePassword(userId: Int, oldPassword: String, newPassword: String) {
        TODO("not implemented")
        val userRecord = this.userRepository.findById(userId)

        userRecord ?:
        throw RestProblem(title = "User does not exist")

        // Verify credentials
        if (!userRecord.verifyPassword(oldPassword))
            throw RestProblem(
                    title = "User authentication failed",
                    status = Response.Status.UNAUTHORIZED)
        userRecord.setHashedPassword(newPassword)
        userRecord.store()
    }

    override fun getConfigurationById(userId: Int): String {
        return configurationService.getUserConfiguration(userId)
    }

    override fun getCurrentUserConfiguration(): String {
        val authorizedUser = httpRequest.authorizedUser

        return configurationService.getUserConfiguration(authorizedUser.id
                ?: throw RestProblem(status = Response.Status.NOT_FOUND))
    }
}