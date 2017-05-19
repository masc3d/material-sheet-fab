package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.MstUser
import org.deku.leoz.central.data.jooq.tables.records.MstUserRecord
import org.deku.leoz.hashUserPassword
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import sx.text.parseHex
import javax.inject.Inject
import sx.time.toLocalDate
import java.util.*
import javax.inject.Named
import org.deku.leoz.central.data.jooq.tables.MstDebitor
import org.deku.leoz.service.internal.entity.User
import org.jooq.impl.DSL.*
import org.deku.leoz.service.internal.entity.UserRole


/**
 * User repository
 * Created by 27694066 on 25.04.2017.
 */
@Named
open class UserJooqRepository {
    companion object {
        // Backend specific salt. This one shouldn't be reused on other devices
        private val SALT = "27abf393a822078603768c78de67e4a3".parseHex()

        /**
         * Verify password
         * @param password Password to verify
         */
        fun MstUserRecord.verifyPassword(password: String): Boolean {
            return this.password == hashUserPassword(
                    salt = SALT,
                    email = this.email,
                    password = password)
        }

        /**
         * Hash password
         * @param email User email
         * @param password Password to hash
         */
        fun MstUserRecord.setHashedPassword(password: String) {
            this.password = hashUserPassword(
                    salt = SALT,
                    email = email,
                    password = password
            )
        }
    }

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext

//    fun findByKey(key: String): MstUserRecord? {
//        return dslContext.fetchOne(MstUser.MST_USER, Tables.MST_USER.API_KEY.eq(key))
//    }

    fun findByMail(email: String): MstUserRecord? {
        return dslContext.fetchOne(MstUser.MST_USER, Tables.MST_USER.EMAIL.eq(email))
    }

    fun findByAlias(alias: String, debitor: Double): MstUserRecord? {
        //return dslContext.fetchOne(MstUser.MST_USER, Tables.MST_USER.ALIAS.eq(alias))
        return dslContext.select()
                .from(Tables.MST_USER.innerJoin(Tables.MST_DEBITOR)
                        .on(Tables.MST_USER.DEBITOR_ID.eq(Tables.MST_DEBITOR.DEBITOR_ID)))
                .where(Tables.MST_USER.ALIAS.eq(alias).and(Tables.MST_DEBITOR.DEBITOR_NR.eq(debitor)))?.fetchOneInto(MstUser.MST_USER)
    }
    fun findByAlias(alias: String, debitorid: Int): MstUserRecord? {
        return dslContext.fetchOne(MstUser.MST_USER, Tables.MST_USER.ALIAS.eq(alias).and(Tables.MST_USER.DEBITOR_ID.eq(debitorid)))
    }

    fun findByDebitorId(id: Int): List<MstUserRecord>? {
        return dslContext
                .select()
                .from(Tables.MST_USER).where(Tables.MST_USER.DEBITOR_ID.eq(id))
                .fetchInto(MstUserRecord::class.java)
    }

    fun aliasExists(alias: String, debitor: Double): Boolean {
        return findByAlias(alias, debitor) != null
    }
    fun aliasExists(alias: String, debitorid: Int): Boolean {
        return findByAlias(alias, debitorid) != null
    }

    fun mailExists(mail: String): Boolean {
        return findByMail(mail) != null
    }

//    fun hasAuthorizedKey(key: String): Boolean {
//        val userRecord = this.findByKey(key) ?: return false
//        return userRecord.active != 0 && userRecord.expiresOn.toLocalDate().isAfter(Date().toLocalDate())
//    }

    fun findDebitorNoById(id: Int): Double? {
        return dslContext.select(Tables.MST_DEBITOR.DEBITOR_NR)
                .from(Tables.MST_DEBITOR)
                .where(Tables.MST_DEBITOR.DEBITOR_ID.eq(id))
                .fetchOneInto(Double::class.java)
    }

    fun findById(id: Int): MstUserRecord? {
        return dslContext.fetchOne(MstUser.MST_USER, Tables.MST_USER.ID.eq(id))
    }

    fun findDebitorIdByNr(no: Double): Int? {
        return dslContext.select(Tables.MST_DEBITOR.DEBITOR_ID).from(Tables.MST_DEBITOR).where(Tables.MST_DEBITOR.DEBITOR_NR.eq(no))
                .fetchOneInto(Int::class.java)
    }

    fun findByKey(apiKey:String):MstUserRecord? {
        return dslContext.select()
                .from(Tables.MST_USER.innerJoin(Tables.MST_KEY)
                        .on(Tables.MST_USER.KEY_ID.eq(Tables.MST_KEY.KEY_ID)))
                .where(Tables.MST_KEY.KEY.eq(apiKey))?.fetchOneInto(MstUser.MST_USER)
    }


    fun deleteById(id: Int): Boolean {
        return if (dslContext.delete(Tables.MST_USER).where(Tables.MST_USER.ID.eq(id)).execute() > 0) true else false
    }



    fun updateByEmail(email: String, user: User): Boolean {
        var returnValue = false

        if (!UserRole.values().any { it.name == user.role })
            return false

        user.alias ?: return false
        user.debitorId ?: return false

        val isActive: Int
        if (user.active == null || user.active == false) isActive = 0 else isActive = -1

        val isExternalUser: Int
        if (user.externalUser == null || user.externalUser == false) isExternalUser = 0 else isExternalUser = -1

        val rec: MstUserRecord?

        if (findByMail(email) == null && !mailExists(user.email) && !aliasExists(user.alias!!, user.debitorId!!)) {
            rec = dslContext.newRecord(Tables.MST_USER)
        } else {
            rec = findByMail(email)
            rec ?: return false
            if (!rec.email.equals(user.email)) {
                if (mailExists(user.email)) {
                    return false
                }
            }
            if (!rec.alias.equals(user.alias) || rec.debitorId != user.debitorId) {
                if (aliasExists(user.alias!!, user.debitorId!!)) {
                    return false
                }
            }

        }


        rec ?: return false



        rec.email = user.email
        rec.debitorId = user.debitorId
        rec.alias = user.alias
        rec.role = user.role
        rec.password = user.password
        //rec.salt = user.salt
        rec.firstname = user.firstName
        rec.lastname = user.lastName
        rec.active = isActive
        rec.externalUser = isExternalUser
        rec.phone = user.phone
        rec.expiresOn = user.expiresOn

        if (rec.store() > 0) returnValue = true else returnValue = false
        if (returnValue && user.password != null) {
            rec.setHashedPassword(rec.password)
            rec.store()
        }
        return returnValue
    }

}