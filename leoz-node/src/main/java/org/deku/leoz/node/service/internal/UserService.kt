package org.deku.leoz.node.service.internal

import org.deku.leoz.model.UserRole
import org.deku.leoz.node.Application
import org.deku.leoz.node.data.jpa.MstStationUser
import org.deku.leoz.node.data.jpa.MstUser
import org.deku.leoz.node.data.jpa.QMstStationUser.mstStationUser
import org.deku.leoz.node.data.jpa.QMstUser.mstUser
import org.deku.leoz.node.data.repository.*
import org.deku.leoz.node.rest.authorizedUser
import org.deku.leoz.service.internal.ConfigurationService
import org.deku.leoz.service.internal.UserService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import sx.persistence.querydsl.delete
import sx.persistence.querydsl.from
import sx.persistence.transaction
import sx.rs.RestProblem
import sx.time.toSqlDate
import sx.time.toTimestamp
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceUnit
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response

@Component
@Path("internal/v1/user")
@Profile(Application.PROFILE_CLIENT_NODE)
class UserService : org.deku.leoz.service.internal.UserService {
    @Context
    private lateinit var httpRequest: HttpServletRequest

    @Inject
    private lateinit var userRepository: UserRepository

    @Inject
    private lateinit var stationUserRepository: StationUserRepository

    @Inject
    private lateinit var stationRepository: StationRepository

    @Inject
    private lateinit var debitorRepository: DebitorRepository

    @Inject
    private lateinit var debitorStationRepository: DebitorStationRepository

    @Inject
    private lateinit var em: EntityManager

    @PersistenceUnit(name = org.deku.leoz.node.config.PersistenceConfiguration.QUALIFIER)
    private lateinit var emf: EntityManagerFactory

    @Inject
    private lateinit var configurationService: ConfigurationService

    override fun get(email: String?, debitorId: Int?): List<UserService.User> {
        var debitor_id = debitorId

        val authorizedUser = httpRequest.authorizedUser
        if (debitor_id == null && email == null) {
            debitor_id = authorizedUser.debitorId
        }

        when {
            debitor_id != null -> {
                val userRecList = userRepository.findByDebitorId(debitor_id.toLong())

                if (userRecList.isEmpty())
                    throw RestProblem(
                            status = Response.Status.NOT_FOUND,
                            title = "No user found by debitor-id")
                val user = mutableListOf<UserService.User>()
                userRecList.forEach {

                    if (authorizedUser.role == UserRole.ADMIN.name) {
                        user.add(it.toUser().also { x -> x.allowedStations = stationRepository.findAllowedStationsByUserId(it.id) })
                    } else {
                        if (authorizedUser.debitorId!!.toLong() == it.debitorId) {
                            if (UserRole.valueOf(authorizedUser.role!!).value >= UserRole.valueOf(it.role).value) {
                                user.add(it.toUser().also { x -> x.allowedStations = stationRepository.findAllowedStationsByUserId(it.id) })
                            }
                        }
                    }

                }
                return user.toList()
            }
            email != null -> {
                val userRecord = userRepository.findByEmail(email)
                        ?: throw RestProblem(
                                status = Response.Status.NOT_FOUND,
                                title = "No user found by email")
                if (UserRole.valueOf(authorizedUser.role!!) == UserRole.ADMIN)
                    return listOf(userRecord.toUser().also { x -> x.allowedStations = stationRepository.findAllowedStationsByUserId(userRecord.id) })
                if ((UserRole.valueOf(authorizedUser.role!!).value >= UserRole.valueOf(userRecord.role).value) &&
                        (authorizedUser.debitorId!!.toLong() == userRecord.debitorId)) {
                    return listOf(userRecord.toUser().also { x -> x.allowedStations = stationRepository.findAllowedStationsByUserId(userRecord.id) })
                } else {
                    throw RestProblem(
                            status = Response.Status.FORBIDDEN,
                            title = "User found but no permission returning this user")
                }
            }
            else -> {
                throw RestProblem(status = Response.Status.BAD_REQUEST)
            }
        }
    }

    override fun create(user: UserService.User, stationMatchcode: String?, debitorNr: Long?, sendAppLink: Boolean) {
        val rec = userRepository.findByEmail(user.email)
        if (rec != null) {
            throw RestProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = "Email exists")
        }

        if (user.debitorId == null) {
            when {
                debitorNr != null -> {
                    user.debitorId = debitorRepository.findByDebitorNr(debitorNr.toDouble())?.debitorId
                }

                !stationMatchcode.isNullOrEmpty() -> {
                    val stationId = stationRepository.findByStation(stationMatchcode!!.toInt())?.stationId //stationRepository.findByMatchcode(matchcode = stationMatchcode!!).debitorId
                    if (stationId != null) {
                        user.debitorId = debitorStationRepository.findByStationId(stationId)?.debitorId
                    }

                }
            }

        }

        update(email = user.email, user = user, sendAppLink = sendAppLink)
    }

    override fun update(email: String, user: UserService.User, sendAppLink: Boolean) {
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

        emf.transaction { em ->


            var rec = userRepository.findByEmail(email)
            if (rec == null) {
                isNew = true
                rec = MstUser()
                if (user.email.equals("@"))
                    user.email = email
                if (!user.email.equals(email)) {
                    throw  RestProblem(
                            status = Response.Status.BAD_REQUEST,
                            title = "Multiple email for new record")
                }
            } else {
                if (rec.debitorId == null) rec.debitorId = authorizedUser.debitorId!!.toLong()
                if (rec.role == null) rec.role = authorizedUser.role

                if (UserRole.valueOf(authorizedUser.role!!) != UserRole.ADMIN) {
                    if (rec.debitorId != authorizedUser.debitorId!!.toLong())
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
                    if (userRepository.findByEmail(user.email) != null) {
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
                val testDebitor: Long
                if (debitor != null)
                    testDebitor = debitor!!.toLong()
                else
                    testDebitor = rec.debitorId

                if (!rec.alias.equals(testAlias) || rec.debitorId != testDebitor) {
                    if (userRepository.findByAliasAndDebitorId(testAlias, testDebitor) != null) {
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

                if (userRepository.findByEmail(user.email) != null)
                    throw RestProblem(
                            status = Response.Status.BAD_REQUEST,
                            title = "Duplicate email")

                if (debitor == null)
                    debitor = authorizedUser.debitorId

                if (debitor == null)
                    throw RestProblem(
                            status = Response.Status.BAD_REQUEST,
                            title = "Missing debitor of login user")

                if (userRepository.findByAliasAndDebitorId(alias, debitor!!.toLong()) != null)
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
                rec.debitorId = debitor!!.toLong()
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
                    if (user.expiresOn == null) {
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



            if (isNew) {
                //todo default-value
                rec.keyId = 0
                rec.tsCreated = java.util.Date().toTimestamp()
                rec.tsUpdated = java.util.Date().toTimestamp()

                em.persist(rec)
                em.flush()
            } else {
                em.merge(rec)
            }

            //check auth? evtl erst ab powerUser?
            if (userStations != null) {
                val allowedStations = stationRepository.findAllowedStationsByUserId(rec.id)
                if (userStations != allowedStations) {
                    if (UserRole.valueOf(authorizedUser.role!!).value >= UserRole.POWERUSER.value) {
                        val possibleStations = stationRepository.findStationsByDebitorId(rec.debitorId)

                        userStations.forEach {
                            if (!possibleStations.contains(it))
                                throw  RestProblem(
                                        status = Response.Status.CONFLICT,
                                        title = "Allowed station mismatch debitor ")
                            if (!allowedStations.contains(it)) {
                                //Insert into mst_station_user
                                val recStation = MstStationUser()
                                recStation.userId = rec.id
                                val stationId = stationRepository.findByStation(it)?.stationId
                                stationId ?: throw RestProblem(
                                        status = Response.Status.NOT_FOUND,
                                        title = "Station with No [$it] not found"
                                )
                                recStation.stationId = stationId.toLong()
                                recStation.tsCreated = java.util.Date().toTimestamp()
                                recStation.tsUpdated = java.util.Date().toTimestamp()
                                em.persist(recStation)
                                em.flush()
                            }
                        }
                        allowedStations.forEach {
                            if (!userStations.contains(it)) {
                                //delete from mst_station_user
                                val stationId = stationRepository.findByStation(it)?.stationId
                                stationId ?: throw RestProblem(
                                        status = Response.Status.NOT_FOUND,
                                        title = "Station with No [$it] not found"
                                )

                                em.delete(mstStationUser, mstStationUser.userId.eq(rec.id)
                                        .and(mstStationUser.stationId.eq(stationId.toLong()))
                                )
                            }
                        }
                    } else {
                        throw  RestProblem(
                                status = Response.Status.FORBIDDEN,
                                title = "Login user can not set/change allowedStations - no permission")
                    }

                }
            }
        }




        if (sendAppLink) {
            sendDownloadLink(userRepository.findByEmail(user.email)!!.id!!.toInt())
        }
    }

    override fun getById(userId: Int): UserService.User {
        val u = userRepository.findById(userId.toLong()) ?: throw RestProblem(
                status = Response.Status.NOT_FOUND,
                title = "User with ID [$userId] not found"
        )
        if (u.isPresent) {
            return this.get(email = u.get().email).first()
        } else
            throw RestProblem(
                    status = Response.Status.NOT_FOUND,
                    title = "User with ID [$userId] not found"
            )
    }

    override fun getIdsByDebitor(debitorId: Int): List<Int> {
        return this.em.from(mstUser)
                .select(mstUser.id)
                .where(mstUser.debitorId.eq(debitorId.toLong()))
                .fetch()
                .map { it.toInt() }
    }

    override fun sendDownloadLink(userId: Int): Boolean {
        throw NotImplementedError("Unsupported")
    }

    override fun changePassword(userId: Int, oldPassword: String, newPassword: String) {

        val u = userRepository.findById(userId.toLong()) ?: throw RestProblem(
                status = Response.Status.NOT_FOUND,
                title = "User with ID [$userId] not found"
        )
        if (u.isPresent) {
            emf.transaction { em ->
                val userRecord=u.get()
                if (!userRecord.verifyPassword(oldPassword))
                    throw RestProblem(
                            title = "User authentication failed",
                            status = Response.Status.UNAUTHORIZED)
                userRecord.setHashedPassword(newPassword)

                em.merge(userRecord)
            }

        } else
            throw RestProblem(
                    status = Response.Status.NOT_FOUND,
                    title = "User with ID [$userId] not found"
            )
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