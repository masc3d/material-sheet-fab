package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.MstUser
import org.deku.leoz.central.data.jooq.tables.records.MstUserRecord
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import javax.inject.Inject
import org.abstractj.kalium.encoders.Encoder
import org.deku.leoz.rest.entity.internal.v1.User
import sx.time.toLocalDate
import java.security.SecureRandom
import java.util.*
import javax.inject.Named


/**
 * Created by 27694066 on 25.04.2017.
 */
@Named
open class UserJooqRepository {

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext

    fun findByKey(key: String): MstUserRecord? {
        return dslContext.fetchOne(MstUser.MST_USER, Tables.MST_USER.API_KEY.eq(key))
    }

    fun findByMail(mail: String): MstUserRecord? {
        return dslContext.fetchOne(MstUser.MST_USER, Tables.MST_USER.EMAIL.eq(mail))
    }

    fun findByAlias(alias: String): MstUserRecord? {
        return dslContext.fetchOne(MstUser.MST_USER, Tables.MST_USER.ALIAS.eq(alias))
    }

    fun aliasExists(alias: String): Boolean {
        return findByAlias(alias) != null
    }

    fun mailExists(mail: String): Boolean {
        return findByMail(mail) != null
    }

    fun hasAuthorizedKey(key: String): Boolean {
        val userRecord = this.findByKey(key) ?: return false
        return userRecord.active != 0 && userRecord.expiresOn.toLocalDate().isAfter(Date().toLocalDate())
    }

//    fun findByCredentials(mail: String, password: String): MstUserRecord? {
//        val passwordManager: org.abstractj.kalium.crypto.Password = org.abstractj.kalium.crypto.Password()
//        val userRecord = findByMail(mail) ?: return null
//        val storedPassword = userRecord.password
//        if (passwordManager.verify(storedPassword.toByteArray(), password.toByteArray())) {
//            if (userRecord.active != 0 && userRecord.expiresOn.toLocalDate().isAfter(Date().toLocalDate())) {
//                return userRecord
//            } else {
//                return null
//            }
//        } else {
//            return null
//        }
//    }
//
//    fun verifyCredentials(mail: String, password: String): Boolean {
//        findByCredentials(mail, password) ?: return false
//        return true
//    }
//
//    fun changePassword(mail: String, passwordOld: String, passwordNew: String): Boolean {
//        val userRecord = findByCredentials(mail, passwordOld) ?: return false
//
//        userRecord.password = hashPassword(passwordNew.toByteArray(), userRecord.salt.toByteArray())
//
//        return true
//    }
//
//    fun createUser(user: User): Boolean {
//
//        if(aliasExists(user.alias!!) || mailExists(user.email!!)) {
//            return false
//        }
//
//        val userRecord = dslContext.newRecord(Tables.MST_USER)
//
//        val r = SecureRandom()
//        val salt = ByteArray(32)
//        r.nextBytes(salt)
//        val encodedSalt = Base64.getEncoder().encodeToString(salt)
//
//        userRecord.salt = encodedSalt
//        userRecord.email = user.email
//        userRecord.debitorId = user.debitorId ?: 0
//        userRecord.alias = user.alias
//        userRecord.role = user.role
//        userRecord.password = hashPassword(user.password!!.toByteArray(), salt)
//        userRecord.apiKey = user.apiKey
//        userRecord.active = if (user.active ?: false) -1 else 0
//        userRecord.expiresOn = user.expiresOn
//        userRecord.firstname = user.firstName
//        userRecord.lastname = user.lastName
//        userRecord.externalUser = if(user.externalUser ?: false) -1 else 0
//        userRecord.phone = user.phone
//
//        userRecord.store()
//
//        return true
//    }
//
//    /**
//     * @param password Users password to be hashed
//     * @param salt Not used yet.
//     * @return Hashed password
//     */
//    private fun hashPassword(password: ByteArray, salt: ByteArray): String {
//        //TODO: To be replaced by working functions. Password should be hashed with modern crypto libraries. Don't use outdated algorithms, eg. MD5
//        val passwordManager: org.abstractj.kalium.crypto.Password = org.abstractj.kalium.crypto.Password()
//        val sodium = org.abstractj.kalium.NaCl.sodium()
//        return passwordManager.hash(password, Encoder.HEX, 32, 512)
//    }
}