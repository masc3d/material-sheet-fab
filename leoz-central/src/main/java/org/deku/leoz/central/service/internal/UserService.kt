package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.tables.records.MstUserRecord
import org.deku.leoz.central.data.repository.UserJooqRepository
import org.deku.leoz.node.rest.DefaultProblem
import org.deku.leoz.service.internal.entity.User
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import sx.rs.auth.ApiKey
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import org.deku.leoz.service.internal.UserService
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

    override fun get(email: String?, debitorId: Int?): List<User> {
        when {
            debitorId != null -> {
                val userRecList = userRepository.findByDebitorId(debitorId)
                        ?: throw DefaultProblem(status = Response.Status.NOT_FOUND)
                if (userRecList.isEmpty())
                    throw DefaultProblem(status = Response.Status.NOT_FOUND)
                val user = mutableListOf<User>()
                userRecList.forEach {
                    user.add(patchRecord2User(it))
                }
                return user.toList()
            }
            email != null -> {
                val userRecord = userRepository.findByMail(email)
                        ?: throw DefaultProblem(status = Response.Status.NOT_FOUND)
                return listOf(patchRecord2User(userRecord))
            }
            else -> {
                // All query params are omitted.
                // We may return all users here at one point, for those who require it
                // In this case we should sensibly check if the user is allowed to do that.
                throw DefaultProblem(status = Response.Status.BAD_REQUEST)
            }
        }
    }

    fun patchRecord2User(userRecord: MstUserRecord): User {
        val active = when (userRecord.active?.toInt()) {
            1, -1 -> true
            else -> false
        }
        val externalUser = when (userRecord.externalUser?.toInt()) {
            1, -1 -> true
            else -> false
        }

        val user = User(userRecord.email,
                userRecord.debitorId,
                /*null,*/
                userRecord.alias,
                userRecord.role,
                userRecord.password,
                /*userRecord.salt,*/
                userRecord.firstname,
                userRecord.lastname,
                /*userRecord.apiKey,*/
                active,
                externalUser,
                userRecord.phone,
                userRecord.expiresOn, userRecord.id
        )
        return user
    }

    override fun create(user: User) {
        user.id=0
        update(0, user)

    }

    override fun update(id: Int, user: User) {

        //debitorID ?
        //role ok?
        if (user.email == "@")
            throw DefaultProblem(status = Response.Status.BAD_REQUEST)

        if (!userRepository.updateById(user))
            throw DefaultProblem(status = Response.Status.BAD_REQUEST)
    }

    override fun delete(id: Int) {
        if (!userRepository.deleteById(id))
            throw DefaultProblem(status = Response.Status.BAD_REQUEST)
    }

    override fun get(id: Int): User {
        val userRecord = userRepository.findById(id)
                ?: throw DefaultProblem(status = Response.Status.NOT_FOUND)
        return patchRecord2User(userRecord)
    }
}