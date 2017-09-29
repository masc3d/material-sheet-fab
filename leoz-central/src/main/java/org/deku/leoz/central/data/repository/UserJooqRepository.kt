package org.deku.leoz.central.data.repository

import com.fasterxml.jackson.databind.ObjectMapper
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.MstUser
import org.deku.leoz.central.data.jooq.tables.records.MstUserRecord
import org.deku.leoz.hashUserPassword
import org.deku.leoz.model.AllowedStations
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import sx.text.parseHex
import javax.inject.Inject
import javax.inject.Named
import org.deku.leoz.service.internal.UserService

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

    fun findByMail(email: String): MstUserRecord? {
        return dslContext.fetchOne(
                MstUser.MST_USER,
                Tables.MST_USER.EMAIL.eq(email))
    }

    fun findByAlias(alias: String, debitor: Double): MstUserRecord? {
        return dslContext.select()
                .from(Tables.MST_USER
                        .innerJoin(Tables.MST_DEBITOR)
                        .on(Tables.MST_USER.DEBITOR_ID.eq(Tables.MST_DEBITOR.DEBITOR_ID)))
                .where(Tables.MST_USER.ALIAS.eq(alias)
                        .and(Tables.MST_DEBITOR.DEBITOR_NR.eq(debitor)))
                .fetchOneInto(MstUser.MST_USER)
    }

    fun findByAlias(alias: String, debitorid: Int): MstUserRecord? {
        return dslContext.fetchOne(
                MstUser.MST_USER,
                Tables.MST_USER.ALIAS.eq(alias)
                        .and(Tables.MST_USER.DEBITOR_ID.eq(debitorid)))
    }

    fun findByDebitorId(id: Int): List<MstUserRecord>? {
        return dslContext.select()
                .from(Tables.MST_USER)
                .where(Tables.MST_USER.DEBITOR_ID.eq(id))
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

    fun findDebitorNoById(id: Int): Double? {
        return dslContext.select(Tables.MST_DEBITOR.DEBITOR_NR)
                .from(Tables.MST_DEBITOR)
                .where(Tables.MST_DEBITOR.DEBITOR_ID.eq(id))
                .fetchOneInto(Double::class.java)
    }

    fun findById(id: Int): MstUserRecord? {
        return dslContext.fetchOne(
                MstUser.MST_USER,
                Tables.MST_USER.ID.eq(id))
    }

    fun findDebitorIdByNr(no: Double): Int? {
        return dslContext.select(Tables.MST_DEBITOR.DEBITOR_ID)
                .from(Tables.MST_DEBITOR)
                .where(Tables.MST_DEBITOR.DEBITOR_NR.eq(no))
                .fetchOneInto(Int::class.java)
    }

    fun findByKey(apiKey: String): MstUserRecord? {
        return dslContext.select()
                .from(Tables.MST_USER.innerJoin(Tables.MST_KEY)
                        .on(Tables.MST_USER.KEY_ID.eq(Tables.MST_KEY.KEY_ID)))
                .where(Tables.MST_KEY.KEY.eq(apiKey))
                .fetchOneInto(MstUser.MST_USER)
    }

    fun deleteById(id: Int): Boolean {
        return if (dslContext.delete(Tables.MST_USER)
                .where(Tables.MST_USER.ID.eq(id))
                .execute() > 0) true else false
    }

    fun updateKeyIdById(id: Int, keyID: Int): Boolean {
        val rec = findById(id)
        rec ?: return false
        rec.keyId = keyID
        return (rec.store() > 0)
    }

    fun save(userRecord: MstUserRecord): Boolean {
        return (userRecord.store() > 0)
    }
}

fun MstUserRecord.toUser(): UserService.User {
    val stations = this.allowedStations?.toString() ?: "{}"
    val mapper = ObjectMapper()
    val allowedStations: AllowedStations = mapper.readValue(stations, AllowedStations::class.java)

    val user = UserService.User(
            // IMPORTANT: password must never be set when converting to service instance
            // as it leaks hashes to the client. That's why the initial recommendation
            // was to *not* have password on service level entities and
            // introduce password set/update operations as a dedicated entry point.
            id = this.id,
            email = this.email,
            debitorId = this.debitorId,
            alias = this.alias,
            role = this.role,
            firstName = this.firstname,
            lastName = this.lastname,
            active = this.isActive,
            externalUser = this.isExternalUser,
            phone = this.phone,
            phoneMobile = this.phoneMobile,
            expiresOn = this.expiresOn,
            allowedStations = allowedStations.allowedStations
    )
    return user
}

val MstUserRecord.isActive: Boolean
    get() = (this.active ?: 0) != 0

val MstUserRecord.isExternalUser: Boolean
    get() = (this.externalUser ?: 0) != 0
