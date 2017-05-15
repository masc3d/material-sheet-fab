package org.deku.leoz.central.service.internal

import io.swagger.annotations.*
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.repository.UserJooqRepository
import org.deku.leoz.service.internal.entity.User
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import sx.rs.auth.ApiKey
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import org.deku.leoz.service.internal.UserService

/**
 * Created by helke on 15.05.17.
 */
@Named
@ApiKey(false)
@Path("internal/v1/user")
class UserService : UserService{

    //private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext

    @Inject
    private lateinit var userRepository: UserJooqRepository

    override fun get(email:String?):User?{
        email ?:return null

        val userRecord=userRepository.findByMail(email)

        userRecord ?:return null

        val active=when(userRecord.active?.toInt()) {
            1, -1 -> true
            else -> false
        }
        val externalUser=when(userRecord.externalUser?.toInt()) {
            1, -1 -> true
            else -> false
        }


        val user=User(userRecord.email,
                userRecord.debitorId,
                null,
                userRecord.alias,
                userRecord.role,
                userRecord.password,
                userRecord.salt,
                userRecord.firstname,
                userRecord.lastname,
                userRecord.apiKey,
                active,
                externalUser,
                userRecord.phone,
                userRecord.expiresOn)

        return user

    }
}