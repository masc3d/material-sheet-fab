package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.repository.UserJooqRepository
import org.deku.leoz.central.data.repository.UserJooqRepository.Companion.setHashedPassword
import org.deku.leoz.central.data.repository.isActive
import org.deku.leoz.central.data.repository.toUser
import org.deku.leoz.model.UserPreferenceKey
import org.deku.leoz.node.rest.DefaultProblem
import org.deku.leoz.service.internal.UserService.User
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import sx.rs.auth.ApiKey
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import org.deku.leoz.service.internal.UserService
import org.deku.leoz.model.UserRole
import java.util.*
import javax.ws.rs.core.Response

/**
 * TODO test apikey-user ->berechtigung (API keys should be verified on higher level -> {@link ApiKeyRequestFilter})
 * Created by helke on 15.05.17.
 */
@Named
@ApiKey(true)
@Path("internal/v1/user")
class UserService : UserService {
    //private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext

    @Inject
    private lateinit var userRepository: UserJooqRepository

    override fun get(email: String?, debitorId: Int?, apiKey: String?): List<User> {
        var debitor_id = debitorId
        apiKey ?:
                throw DefaultProblem(status = Response.Status.BAD_REQUEST)
        val authorizedUserRecord = userRepository.findByKey(apiKey)
        authorizedUserRecord ?:
                throw DefaultProblem(status = Response.Status.BAD_REQUEST)


        if (!authorizedUserRecord.isActive) {
            throw DefaultProblem(
                    title = "user deactivated",
                    status = Response.Status.UNAUTHORIZED)
        }
        if (Date() > authorizedUserRecord.expiresOn) {
            throw DefaultProblem(
                    title = "user account expired",
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
                val user = mutableListOf<User>()
                userRecList.forEach {

                    if (UserRole.valueOf(authorizedUserRecord.role) == UserRole.ADMIN) {
                        user.add(it.toUser())
                    } else {
                        if (authorizedUserRecord.debitorId == it.debitorId) {
                            if (UserRole.valueOf(authorizedUserRecord.role).value >= UserRole.valueOf(it.role).value) {
                                user.add(it.toUser())
                            }
                        }
                    }

                }
                return user.toList()
            }
            email != null -> {
                val userRecord = userRepository.findByMail(email)
                        ?: throw DefaultProblem(
                        status = Response.Status.NOT_FOUND,
                        title = "no user found by email")
                if (UserRole.valueOf(authorizedUserRecord.role) == UserRole.ADMIN)
                    return listOf(userRecord.toUser())
                if ((UserRole.valueOf(authorizedUserRecord.role).value >= UserRole.valueOf(userRecord.role).value) && (authorizedUserRecord.debitorId == userRecord.debitorId)) {
                    return listOf(userRecord.toUser())
                } else {
                    throw DefaultProblem(
                            status = Response.Status.FORBIDDEN,
                            title = "user found but no permission returning this user")
                }
            }
            else -> {
                // All query params are omitted.
                // We may return all users here at one point, for those who require it
                // In this case we should sensibly check if the user is allowed to do that.

                throw DefaultProblem(status = Response.Status.BAD_REQUEST)

            }
        }
    }


    override fun create(user: User, apiKey: String?, sendCredentials: Boolean) {

        var rec = userRepository.findByMail(user.email)
        if (rec != null) {
            throw DefaultProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = "email exists")
        }

        update(user.email, user, apiKey)
    }

    override fun update(email: String, user: User, apiKey: String?, sendCredentials: Boolean) {
        apiKey ?:
                throw DefaultProblem(
                        status = Response.Status.BAD_REQUEST,
                        title = "no apiKey")
        val authorizedUserRecord = userRepository.findByKey(apiKey)
        authorizedUserRecord ?: throw DefaultProblem(
                status = Response.Status.BAD_REQUEST,
                title = "login user not found")

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

        var debitor = user.debitorId

        val userRole = user.role?.toUpperCase()
        val alias = user.alias
        val password = user.password
        val lastName = user.lastName
        val firstName = user.firstName
        val phone = user.phone
        val mobilePhone = user.phoneMobile

        var isNew = false
        var rec = userRepository.findByMail(email)
        if (rec == null) {
            isNew = true
            rec = dslContext.newRecord(Tables.MST_USER)
            //if (user.email == null || user.email.equals("@"))
            if (user.email.equals("@"))
                user.email = email
            if (!user.email.equals(email)) {
                throw  DefaultProblem(
                        status = Response.Status.BAD_REQUEST,
                        title = "multiple email for new record")
            }
        } else {
            if (rec.debitorId == null) rec.debitorId = authorizedUserRecord.debitorId
            if (rec.role == null) rec.role = authorizedUserRecord.role

            if (UserRole.valueOf(authorizedUserRecord.role) != UserRole.ADMIN) {
                if (rec.debitorId != authorizedUserRecord.debitorId)
                    throw  DefaultProblem(
                            status = Response.Status.FORBIDDEN,
                            title = "login user can not change user - debitorId")
            }

            if (UserRole.valueOf(authorizedUserRecord.role).value < UserRole.valueOf(rec.role).value) {
                throw  DefaultProblem(
                        status = Response.Status.FORBIDDEN,
                        title = "login user can not create/change user - no permission")
            }

            //if (user.email != null) {
            if (!rec.email.equals(user.email)) {
                if (userRepository.mailExists(user.email)) {
                    throw  DefaultProblem(
                            status = Response.Status.BAD_REQUEST,
                            title = "duplicate email")
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
                    throw  DefaultProblem(
                            status = Response.Status.BAD_REQUEST,
                            title = "duplicate alias or debitorId")
                }
            }
        }

        if (isNew) {
            alias ?: throw  DefaultProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = "no alias")
            userRole ?: throw  DefaultProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = "no user role")
            password ?: throw  DefaultProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = "no password")
            firstName ?: throw  DefaultProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = "no first name")
            lastName ?: throw  DefaultProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = "no last name")
            phone ?: throw  DefaultProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = "no phone")

            if (user.active == null)
                user.active = false

            if (userRepository.mailExists(user.email))
                throw DefaultProblem(
                        status = Response.Status.BAD_REQUEST,
                        title = "duplicate email")

            if (debitor == null)
                debitor = authorizedUserRecord.debitorId
            if (debitor == null)
                throw DefaultProblem(
                        status = Response.Status.BAD_REQUEST,
                        title = "missing debitor of login user")

            if (userRepository.aliasExists(alias, debitor))
                throw DefaultProblem(
                        status = Response.Status.BAD_REQUEST,
                        title = "duplicate alias/debitor")
        }

        if (!UserRole.values().any { it.name == authorizedUserRecord.role })
            throw  DefaultProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = "login user role unknown")

        if (debitor != null) {

            if (UserRole.valueOf(authorizedUserRecord.role) != UserRole.ADMIN) {
                if (authorizedUserRecord.debitorId != debitor) {
                    throw  DefaultProblem(
                            status = Response.Status.FORBIDDEN,
                            title = "login user can not change debitorId")
                }
            }
        }

        if (userRole != null) {
            if (!UserRole.values().any { it.name == userRole })
                throw  DefaultProblem(
                        status = Response.Status.BAD_REQUEST,
                        title = "user role unknown")

            if (UserRole.valueOf(authorizedUserRecord.role).value < UserRole.valueOf(userRole).value) {
                throw  DefaultProblem(
                        status = Response.Status.FORBIDDEN,
                        title = "login user can not create/change user - no permission")
            }
        }

        rec ?: throw  DefaultProblem(
                status = Response.Status.BAD_REQUEST,
                title = "not found")

        //if ((user.email != null) && (user.email != "@"))
        if (user.email != "@")
            rec.email = user.email
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
        if (user.active != null)
            rec.active = user.isActive
        if (user.externalUser != null)
            rec.externalUser = user.isExternalUser
        if (phone != null)
            rec.phone = phone
        if (mobilePhone != null)
            rec.phoneMobile = mobilePhone
        if (user.expiresOn != null)
            rec.expiresOn = user.expiresOn

        if (!userRepository.save(rec))
            throw DefaultProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = "Problem on update")

        if (sendCredentials) {
            //TODO If SendCredentials is true, the user will receive his credentials via SMS
        }

    }

    override fun getById(userId: Int, apiKey: String?): User {
        val u = userRepository.findById(userId) ?: throw DefaultProblem(
                status = Response.Status.NOT_FOUND,
                title = "User with ID [$userId] not found"
        )

        return this.get(email = u.email, apiKey = apiKey).first()
    }

}