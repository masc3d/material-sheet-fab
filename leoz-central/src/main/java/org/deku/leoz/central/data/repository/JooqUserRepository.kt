package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.jooq.dekuclient.Tables.MST_KEY
import org.deku.leoz.central.data.jooq.dekuclient.Tables.MST_USER
import org.deku.leoz.central.data.jooq.dekuclient.Tables.*
import org.deku.leoz.central.data.jooq.dekuclient.tables.MstUser
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.MstUserRecord
import org.deku.leoz.model.UserActivity
import org.deku.leoz.model.UserRole
import org.deku.leoz.node.data.jooq.Tables.MST_DEBITOR
import org.deku.leoz.service.internal.UserService
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import sx.security.DigestType
import sx.security.hash
import sx.text.parseHex
import sx.text.toHexString
import sx.time.toTimestamp
import sx.util.toByteArray
import java.util.*
import javax.inject.Inject

/**
 * User repository
 * Created by 27694066 on 25.04.2017.
 */
@Component
class JooqUserRepository {
    companion object {
        // Backend specific salt. This one shouldn't be reused on other devices
        private val SALT = "27abf393a822078603768c78de67e4a3".parseHex()

        /**
         * Verify password
         * @param password Password to verify
         */
        fun MstUserRecord.verifyPassword(password: String): Boolean {
            return this.password == listOf(
                    SALT,
                    this.email.toByteArray(),
                    password.toByteArray()
            ).hash(DigestType.SHA1).toHexString()
        }

        /**
         * Hash password
         * @param email User email
         * @param password Password to hash
         */
        fun MstUserRecord.setHashedPassword(password: String) {
            this.password = listOf(
                    SALT,
                    this.email.toByteArray(),
                    password.toByteArray()
            ).hash(DigestType.SHA1).toHexString()
        }
    }

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dsl: DSLContext

    fun findByMail(email: String): MstUserRecord? {
        return dsl.fetchOne(
                MST_USER,
                MST_USER.EMAIL.eq(email))
    }

    fun findByAlias(alias: String, debitor: Double): MstUserRecord? {
        return dsl.selectFrom(MST_USER
                .innerJoin(MST_DEBITOR)
                .on(MST_USER.DEBITOR_ID.eq(MST_DEBITOR.DEBITOR_ID)))
                .where(MST_USER.ALIAS.eq(alias)
                        .and(MST_DEBITOR.DEBITOR_NR.eq(debitor)))
                .fetchOneInto(MST_USER)
    }

    fun findByAlias(alias: String, debitorid: Int): MstUserRecord? {
        return dsl.fetchOne(
                MST_USER,
                MST_USER.ALIAS.eq(alias)
                        .and(MST_USER.DEBITOR_ID.eq(debitorid)))
    }

    fun findByDebitorId(id: Int): List<MstUserRecord>? {
        return dsl.selectFrom(MST_USER)
                .where(MST_USER.DEBITOR_ID.eq(id))
                .toList()
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

    fun findById(id: Int): MstUserRecord? {
        return dsl.fetchOne(
                MST_USER,
                MST_USER.ID.eq(id))
    }

    fun findDebitorIdByNr(no: Double): Int? {
        return dsl.selectFrom(MST_DEBITOR)
                .where(MST_DEBITOR.DEBITOR_NR.eq(no))
                .fetchOne(MST_DEBITOR.DEBITOR_ID)
    }

    fun findUserIdsByDebitor(debitorId: Int): List<Int> {
        return dsl.selectFrom(MST_USER)
                .where(MST_USER.DEBITOR_ID.eq(debitorId))
                .fetch(MST_USER.ID)
    }

    fun findByKey(apiKey: String): MstUserRecord? {
        return dsl.select()
                .from(MST_USER.innerJoin(MST_KEY)
                        .on(MST_USER.KEY_UID.eq(MST_KEY.UID)))
                .where(MST_KEY.KEY.eq(apiKey))
                .fetchOneInto(MstUser.MST_USER)
    }

    fun updateKeyIdById(id: Int, keyUid: UUID): Boolean {
        val rec = findById(id)
        rec ?: return false
        rec.setKeyUid(*keyUid.toByteArray())
        return (rec.store() > 0)
    }

    @Transactional(PersistenceConfiguration.QUALIFIER)
    fun logUserActivity(userId: Int, activity: UserActivity, date: Date) {
        val r = dsl.newRecord(Tables.HIS_USER_ACTIVITY).also {
            it.userId = userId
            it.activity = activity.value
            it.tsActivity = date.toTimestamp()
        }
        r.store()
    }

    fun findAllowedStationsByUserId(userId: Int): List<Int> {
        return dsl.select(TBLDEPOTLISTE.DEPOTNR)
                .from(TBLDEPOTLISTE)
                .innerJoin(MST_STATION_USER).on(TBLDEPOTLISTE.ID.eq(MST_STATION_USER.STATION_ID))
                .where(MST_STATION_USER.USER_ID.eq(userId)).and(TBLDEPOTLISTE.ISTGUELTIG.eq(1))
                .fetch(TBLDEPOTLISTE.DEPOTNR)
    }
}

fun MstUserRecord.toUser(): UserService.User =
        UserService.User(
                // IMPORTANT: password must never be set when converting to service instance
                // as it leaks hashes to the client. That's why the initial recommendation
                // was to *not* have password on service level entities and
                // introduce password set/update operations as a dedicated entry point.
                id = this.id,
                email = this.email,
                debitorId = this.debitorId,
                alias = this.alias,
                role = UserRole.valueOf(this.role),
                firstName = this.firstname,
                lastName = this.lastname,
                active = this.isActive,
                externalUser = this.isExternalUser,
                phone = this.phone,
                phoneMobile = this.phoneMobile,
                expiresOn = this.expiresOn,
                passwordExpiresOn = this.passwordExpiresOn
        )

val MstUserRecord.isActive: Boolean
    get() = (this.active ?: 0) != 0

val MstUserRecord.isExternalUser: Boolean
    get() = (this.externalUser ?: 0) != 0
