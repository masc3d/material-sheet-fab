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

    fun findByKey(key: String): MstUserRecord? {
        return dslContext.fetchOne(MstUser.MST_USER, Tables.MST_USER.API_KEY.eq(key))
    }

    fun findByMail(email: String): MstUserRecord? {
        return dslContext.fetchOne(MstUser.MST_USER, Tables.MST_USER.EMAIL.eq(email))
    }

    fun findByAlias(alias: String): MstUserRecord? {
        return dslContext.fetchOne(MstUser.MST_USER, Tables.MST_USER.ALIAS.eq(alias))
    }

    fun find(name: String): MstUserRecord? {
        return findByAlias(name) ?: findByMail(name)
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
}