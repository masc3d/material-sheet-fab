package org.deku.leoz.node.data.repository

import org.deku.leoz.node.data.jpa.*
import org.deku.leoz.node.data.jpa.QMstKey.mstKey
import org.deku.leoz.node.data.jpa.QMstUser.mstUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import javax.inject.Inject
import javax.persistence.EntityManager
import org.deku.leoz.service.internal.UserService
import sx.persistence.querydsl.from
import sx.text.parseHex
import javax.persistence.PersistenceContext

interface UserRepository :
        JpaRepository<MstUser, Long>,
        QuerydslPredicateExecutor<MstUser>, UserRepositoryExtension

interface UserRepositoryExtension {
    fun findByDebitorId(debitorId: Long): List<MstUser>
    fun findByEmail(email: String): MstUser?
    fun findByAliasAndDebitorId(alias: String, debitorId: Long): MstUser?
    fun findByKey(key: String): MstUser?
}

class UserRepositoryImpl : UserRepositoryExtension {

    @Inject
    private lateinit var userRepository: UserRepository

    @PersistenceContext
    private lateinit var em: EntityManager


    override fun findByAliasAndDebitorId(alias: String, debitorId: Long): MstUser? {
        return userRepository.findOne(
                mstUser.alias.eq(alias)
                        .and(mstUser.debitorId.eq(debitorId))
                         )
                .orElse(null)
    }

    override fun findByDebitorId(debitorId: Long): List<MstUser> {
        return userRepository.findAll(
                mstUser.debitorId.eq(debitorId)).toList()
    }

    override fun findByEmail(email: String): MstUser? {
        return userRepository.findOne(
                mstUser.email.eq(email)).orElse(null)
    }

    override fun findByKey(key: String): MstUser? {
        return em.from(mstUser)
                .innerJoin(mstKey).on(mstUser.keyId.eq(mstKey.id))
                .where(mstKey.key.eq(key))
                .fetchOne()
    }
}

fun MstUser.toUser(): UserService.User =
        UserService.User(
                // IMPORTANT: password must never be set when converting to service instance
                // as it leaks hashes to the client. That's why the initial recommendation
                // was to *not* have password on service level entities and
                // introduce password set/update operations as a dedicated entry point.
                id = this.id.toInt(),
                email = this.email,
                debitorId = this.debitorId.toInt(),
                alias = this.alias,
                role = this.role,
                firstName = this.firstname,
                lastName = this.lastname,
                active = this.isActive,
                externalUser = this.isExternalUser,
                phone = this.phone,
                phoneMobile = this.phoneMobile,
                expiresOn = this.expiresOn,
                passwordExpiresOn = this.passwordExpiresOn
        )

val MstUser.isActive: Boolean
    get() = (this.active ?: 0) != 0

val MstUser.isExternalUser: Boolean
    get() = (this.externalUser ?: 0) != 0

private val SALT = "27abf393a822078603768c78de67e4a3".parseHex()
/**
 * Verify password
 * @param password Password to verify
 */
fun MstUser.verifyPassword(password: String): Boolean {
    return this.password == org.deku.leoz.hashUserPassword(
            salt = SALT,
            email = this.email,
            password = password)
}

/**
 * Hash password
 * @param email User email
 * @param password Password to hash
 */
fun MstUser.setHashedPassword(password: String) {
    this.password = org.deku.leoz.hashUserPassword(
            salt = SALT,
            email = email,
            password = password
    )
}