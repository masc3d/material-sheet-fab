package org.deku.leoz.node.data.repository

import org.deku.leoz.node.data.jpa.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.criteria.JoinType
import org.deku.leoz.service.internal.UserService
import org.springframework.data.jpa.repository.Query
import sx.text.parseHex
import javax.inject.Qualifier

interface UserRepository :
        JpaRepository<MstUser, Long>,
        QuerydslPredicateExecutor<MstUser>, UserRepositoryExtension

interface UserRepositoryExtension {
    fun findByDebitorId(debitorId: Long): List<MstUser>
    fun findByEmail(email: String): MstUser?
    fun findByAliasAndDebitorId(alias: String, debitorId: Long): MstUser?

    //@Query("select station_nr from mst_station inner join mst_station_user on mst_station.station_id=mst_station_user.station_id where mst_station_user.user_id= ?#{[0]}")
    //fun findAllowedStationsByUserId(userId: Long): List<Int>

}

class UserRepositioryImpl : UserRepositoryExtension {

    @Inject
    private lateinit var userRepository: UserRepository

//    @Inject
//    private lateinit var entityManager: EntityManager

    override fun findByAliasAndDebitorId(alias: String, debitorId: Long): MstUser? {
        val qUser = QMstUser.mstUser
        return userRepository.findOne(
                qUser.alias.eq(alias)
                        .and(qUser.debitorId.eq(debitorId))
                         )
                .orElse(null)
    }

    override fun findByDebitorId(debitorId: Long): List<MstUser> {
        val qUser = QMstUser.mstUser
        return userRepository.findAll(
                qUser.debitorId.eq(debitorId)).toList()
    }

    override fun findByEmail(email: String): MstUser? {
        val qUser = QMstUser.mstUser
        return userRepository.findOne(
                qUser.email.eq(email)).orElse(null)
    }

    //   @Query("select station_nr from mst_station inner join mst_station_user on mst_station.station_id=mst_station_user.station_id where mst_station_user.user_id= ?#{[0]}")
    //   override fun findAllowedStationsByUserId(userId: Long): List<Int> {

//    override fun findAllowedStationsByUserId(userId: Long): List<Int> {
//        //val qUser=QMstUser.mstUser
//        //val qStationUser = QMstStationUser.mstStationUser
//        //val qStation = QMstStation.mstStation
//
////        val builder = entityManager.criteriaBuilder
////        val query = builder.createQuery(MstStation::class.java)
////        val stat=query.from(MstStation::class.java)
////        val userJoin=stat.joins(MstStationUser)//.join("mappedB",JoinType.INNER)
////        userJoin.
////        query.where(builder.equal(userJoin.get))
////        query.where(qStation.stationId.eq(qStationUser.stationId))
////return entityManager.createQuery(query).resultList()
//
//        val l = mutableListOf<Int>(10, 20)
//
//
//        return l
////        return entityManager.createQuery("select station_nr from mst_station inner join mst_station_user on mst_station.station_id=mst_station_user.station_id where mst_station_user.user_id= :userid ",Int::class.java)
////                .setParameter("userid",userId)
////                .resultList
//
//    }
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
                expiresOn = this.expiresOn
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