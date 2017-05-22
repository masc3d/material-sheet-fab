package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.records.MstUserRecord
import org.deku.leoz.central.data.repository.UserJooqRepository
import org.deku.leoz.central.data.repository.UserJooqRepository.Companion.setHashedPassword
import org.deku.leoz.central.data.repository.toUser
import org.deku.leoz.node.rest.DefaultProblem
import org.deku.leoz.service.internal.entity.User
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import sx.rs.auth.ApiKey
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import org.deku.leoz.service.internal.UserService
import org.deku.leoz.service.internal.entity.UserRole
import org.deku.leoz.service.internal.entity.isActive
import org.deku.leoz.service.internal.entity.isExternalUser
import javax.ws.rs.HeaderParam
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

        if (debitor_id == null && email == null) {
            apiKey ?:
                    throw DefaultProblem(status = Response.Status.BAD_REQUEST)
            val authorizedUserRecord = userRepository.findByKey(apiKey)
            debitor_id = authorizedUserRecord?.debitorId

        }

        when {

            debitor_id != null -> {
                val userRecList = userRepository.findByDebitorId(debitor_id)
                        ?: throw DefaultProblem(status = Response.Status.NOT_FOUND, title = "no user found by debitor-id")
                if (userRecList.isEmpty())
                    throw DefaultProblem(status = Response.Status.NOT_FOUND, title = "no user found by debitor-id")
                val user = mutableListOf<User>()
                userRecList.forEach {
                    user.add(it.toUser())
                }
                return user.toList()
            }
            email != null -> {
                val userRecord = userRepository.findByMail(email)
                        ?: throw DefaultProblem(status = Response.Status.NOT_FOUND, title = "no user found by email")
                return listOf(userRecord.toUser())
            }
            else -> {
                // All query params are omitted.
                // We may return all users here at one point, for those who require it
                // In this case we should sensibly check if the user is allowed to do that.

                throw DefaultProblem(status = Response.Status.BAD_REQUEST)

            }
        }
    }


    override fun create(user: User, apiKey: String?) {
        update(user.email, user, apiKey)

    }

    override fun update(email: String, user: User, apiKey: String?) {


        apiKey ?:
                throw DefaultProblem(status = Response.Status.BAD_REQUEST, title = "no apiKey")
        val authorizedUserRecord = userRepository.findByKey(apiKey)
        authorizedUserRecord ?: throw DefaultProblem(status = Response.Status.BAD_REQUEST, title = "login user not found")

        if (user.email == "@")
            throw DefaultProblem(status = Response.Status.BAD_REQUEST, title = "invalid email")
        user.role = user.role?.toUpperCase() ?: throw  DefaultProblem(status = Response.Status.BAD_REQUEST, title = "no user role")


        if (!UserRole.values().any { it.name == user.role })
            throw  DefaultProblem(status = Response.Status.BAD_REQUEST, title = "user role unknown")

        user.alias ?: throw  DefaultProblem(status = Response.Status.BAD_REQUEST, title = "no alias")
        user.debitorId ?: throw  DefaultProblem(status = Response.Status.BAD_REQUEST, title = "no debitorId")

        if (!UserRole.values().any { it.name == authorizedUserRecord.role })
            throw  DefaultProblem(status = Response.Status.BAD_REQUEST, title = "login user role unknown")

        if (UserRole.valueOf(authorizedUserRecord.role) != UserRole.ADMINISTRATOR) {
            if (authorizedUserRecord.debitorId != user.debitorId) {
                throw  DefaultProblem(status = Response.Status.BAD_REQUEST, title = "login user can not change debitorId")
            }
        }
        val userRole = user.role ?: UserRole.CUSTOMER.toString()
        if (UserRole.valueOf(authorizedUserRecord.role).value < UserRole.valueOf(userRole).value) {
            throw  DefaultProblem(status = Response.Status.BAD_REQUEST, title = "login user can not create/change user - no permission")
        }


        val rec: MstUserRecord?

        if (userRepository.findByMail(email) == null && !userRepository.mailExists(user.email) && !userRepository.aliasExists(user.alias!!, user.debitorId!!)) {
            rec = dslContext.newRecord(Tables.MST_USER)
        } else {
            rec = userRepository.findByMail(email)
            rec ?: throw  DefaultProblem(status = Response.Status.BAD_REQUEST, title = "user unknown")
            if (!rec.email.equals(user.email)) {
                if (userRepository.mailExists(user.email)) {
                    throw  DefaultProblem(status = Response.Status.BAD_REQUEST, title = "duplicate email")
                }
            }
            if (!rec.alias.equals(user.alias) || rec.debitorId != user.debitorId) {
                if (userRepository.aliasExists(user.alias!!, user.debitorId!!)) {
                    throw  DefaultProblem(status = Response.Status.BAD_REQUEST, title = "duplicate alias or debitorId")
                }
            }
        }

        rec ?: throw  DefaultProblem(status = Response.Status.BAD_REQUEST, title = "not found")

        rec.email = user.email
        rec.debitorId = user.debitorId
        rec.alias = user.alias
        rec.role = user.role
        rec.setHashedPassword(user.password ?: "123")
        rec.firstname = user.firstName
        rec.lastname = user.lastName
        rec.active = user.isActive
        rec.externalUser = user.isExternalUser
        rec.phone = user.phone
        rec.expiresOn = user.expiresOn

        if (!userRepository.save(rec))
            throw DefaultProblem(status = Response.Status.BAD_REQUEST, title = "Problem on update")

    }

}