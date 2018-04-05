package org.deku.leoz.node.data

import com.querydsl.core.types.dsl.Param
import com.querydsl.jpa.impl.JPAQuery
import org.deku.leoz.node.config.DataTestConfiguration
import org.deku.leoz.node.config.PersistenceConfiguration
import org.deku.leoz.node.data.jpa.MstRoute
import org.deku.leoz.node.data.jpa.QMstRoute
import org.deku.leoz.node.data.repository.RouteRepository
import org.eclipse.persistence.config.HintValues
import org.eclipse.persistence.config.QueryHints
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional
import sx.Stopwatch
import sx.persistence.querydsl.NamedQuery
import sx.junit.PrototypeTest
import sx.time.toTimestamp
import java.sql.Timestamp
import java.util.*
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.sql.DataSource

/**
 * Created by masc on 07/10/2016.
 */
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(DataTestConfiguration::class))
@Category(PrototypeTest::class)
class RouteRepositoryTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var entityManager: EntityManager

    @Inject
    private lateinit var routeRepository: RouteRepository

    @Qualifier(PersistenceConfiguration.QUALIFIER)
    @Inject
    private lateinit var dataSource: DataSource

    @Transactional
    @Test
    fun testFindAll() {
        val qRoute = QMstRoute.mstRoute

        val result = routeRepository.findAll(
                qRoute.country.eq("DE")
        )

        log.info("RESULT COUNT ${result.count()}")
    }

    @Transactional
    @Test
    fun testFind() {
        for (i in 0..20) {
            val sw = Stopwatch.createStarted()
            val result = routeRepository.findById(46179)
            log.info("${sw} ${result}")
        }
    }

    @Transactional
    @Test
    fun testFindQueryDsl() {
        val qRoute = QMstRoute.mstRoute

        val pLayer = Param(Int::class.java)
        val pCountry = Param(String::class.java)
        val pZipFrom = Param(String::class.java)
        val query = JPAQuery<MstRoute>(entityManager)
                .from(qRoute)
                .select(qRoute.id)
                .where(qRoute.layer.eq(pLayer)
                        .and(qRoute.country.eq(pCountry))
                        .and(qRoute.zipFrom.eq(pZipFrom)))
                .set(pLayer, 0)
                .set(pCountry, "")
                .set(pZipFrom, "")
                .createQuery()

        this.entityManager.entityManagerFactory.addNamedQuery(query.toString(), query)

        for (i in 0..2000) {
            val sw = Stopwatch.createStarted()
            val result = entityManager
                    .createNamedQuery(query.toString())
                    .setParameter(1, 1)
                    .setParameter(2, "DE")
                    .setParameter(3, "50181")
                    .resultList

            log.info("${sw} ${result.count()}")
        }
    }


    @Transactional
    @Test
    fun testFindRoutingSpringDataPrepared() {
        val qRoute = QMstRoute.mstRoute

        class Params {
            val layer = Param(Int::class.java)
            val country = Param(String::class.java)
            val zipFrom = Param(String::class.java)
            val zipTo = Param(String::class.java)
            val validFrom = Param(Timestamp::class.java)
            val validTo = Param(Timestamp::class.java)
        }

        val timestamp = Date().toTimestamp()

        val pquery = NamedQuery(
                entityManager = entityManager,
                resultType = MstRoute::class.java,
                paramsType = Params::class.java,
                hints = listOf(
                        Pair(QueryHints.QUERY_RESULTS_CACHE, HintValues.TRUE),
                        Pair(QueryHints.QUERY_RESULTS_CACHE_SIZE, 500.toString())
                ),
                query = { q, p ->
                    q.from(qRoute)
                            .where(qRoute.layer.eq(p.layer)
                                    .and(qRoute.country.eq(p.country))
                                    .and(qRoute.zipFrom.loe(p.zipFrom))
                                    .and(qRoute.zipTo.goe(p.zipTo))
                                    .and(qRoute.validFrom.before(p.validFrom))
                                    .and(qRoute.validTo.after(p.validTo))
                            )
                }
        )

        for (i in 0..20) {
            val sw = Stopwatch.createStarted()

            fun query(zipFrom: String, zipTo: String): List<MstRoute> {
                val result = pquery.create { q, p ->
                    q.set(p.layer, 1)
                    q.set(p.country, "DE")
                    q.set(p.zipFrom, zipFrom)
                    q.set(p.zipTo, zipTo)
                    q.set(p.validFrom, timestamp)
                    q.set(p.validTo, timestamp)
                }
                        .execute()

                return result.sortedByDescending {
                    it.syncId
                }
            }

            var result: List<MstRoute> = listOf()

            sw.restart()
            result = query(zipFrom = "50181", zipTo = "50181")
            log.info("${sw} ${result.count()}")

            sw.restart()
            result = query(zipFrom = "36286", zipTo = "36286")
            log.info("${sw} ${result.count()}")

            sw.restart()
            result = query(zipFrom = "63571", zipTo = "63571")
            log.info("${sw} ${result.count()}")
        }
    }

    @Transactional
    @Test
    fun testFindRoutingSpringDataPreparedRaw() {
        val qRoute = QMstRoute.mstRoute

        val pLayer = Param(Int::class.java, qRoute.layer.metadata.name)
        val pCountry = Param(String::class.java, qRoute.country.metadata.name)
        val pZipFrom = Param(String::class.java, qRoute.zipFrom.metadata.name)
        val pZipTo = Param(String::class.java, qRoute.zipTo.metadata.name)
        val pValidFrom = Param(Timestamp::class.java, qRoute.validFrom.metadata.name)
        val pValidTo = Param(Timestamp::class.java, qRoute.validTo.metadata.name)

        val d = Date().toTimestamp()
        val query = JPAQuery<MstRoute>(entityManager)
                .from(qRoute)
                .where(qRoute.layer.eq(pLayer)
                        .and(qRoute.country.eq(pCountry))
                        .and(qRoute.zipFrom.loe(pZipFrom))
                        .and(qRoute.zipTo.goe(pZipTo))
                        .and(qRoute.validFrom.before(pValidFrom))
                        .and(qRoute.validTo.after(pValidTo))
                )
                .set(pLayer, 0)
                .set(pCountry, "")
                .set(pZipFrom, "")
                .set(pZipTo, "")
                .set(pValidFrom, Date().toTimestamp())
                .set(pValidTo, Date().toTimestamp())
                .createQuery()

        query.setHint(QueryHints.QUERY_RESULTS_CACHE, HintValues.TRUE)
        query.setHint(QueryHints.QUERY_RESULTS_CACHE_SIZE, 500.toString())

        entityManager.entityManagerFactory.addNamedQuery(
                query.toString(),
                query)

        entityManager
                .createNamedQuery(query.toString(), MstRoute::class.java)
                .parameters
                .forEach {
                    log.info("PARAM ${it.name} POS ${it.position} TYPE ${it.parameterType}")
                }

        val timestamp = Date().toTimestamp()

        for (i in 0..20) {
            val sw = Stopwatch.createStarted()

            fun query(zipFrom: String, zipTo: String): List<MstRoute> {
                val nquery = entityManager
                        .createNamedQuery(query.toString(), MstRoute::class.java)
                        .setParameter(1, 1)
                        .setParameter(2, "DE")
                        .setParameter(3, zipFrom)
                        .setParameter(4, zipTo)
                        .setParameter(5, timestamp)
                        .setParameter(6, timestamp)

                val result = nquery
                        .resultList

                return result.sortedByDescending {
                    it.syncId
                }
            }

            var result: List<MstRoute> = listOf()

            sw.restart()
            result = query(zipFrom = "50181", zipTo = "50181")
            log.info("${sw} ${result.count()}")

            sw.restart()
            result = query(zipFrom = "36286", zipTo = "36286")
            log.info("${sw} ${result.count()}")

            sw.restart()
            result = query(zipFrom = "63571", zipTo = "63571")
            log.info("${sw} ${result.count()}")
        }
    }

    @Transactional
    @Test
    fun testFindRoutingSpringData() {
        val qRoute = QMstRoute.mstRoute

        val timestamp = Date().toTimestamp()

        for (i in 0..2000) {
            val sw = Stopwatch.createStarted()

            val result = routeRepository.findAll(
                    qRoute.layer.eq(1)
                            .and(qRoute.country.eq("DE"))
                            .and(qRoute.zipFrom.loe("50181"))
                            .and(qRoute.zipTo.goe("50181"))
                            .and(qRoute.validFrom.before(timestamp))
                            .and(qRoute.validTo.after(timestamp))
            )

            result.sortedByDescending {
                it.syncId
            }.first()

            log.info("${sw} ${result.count()}")
        }
    }

    @Transactional
    @Test
    fun testFindQueryDsl2() {
        val qRoute = QMstRoute.mstRoute

        for (i in 0..20) {
            val sw = Stopwatch.createStarted()
            val result = routeRepository.findAll(
                    qRoute.layer.eq(1)
                            .and(qRoute.country.eq("DE"))
            )
            log.info("${sw} ${result.count()}")
        }
    }

    val SQL_QUERY = "SELECT * FROM mst_route WHERE layer=1 AND country='DE' AND zip_from='63571'"

    @Test
    fun testSelectJdbcPrepared() {
        val cn = this.dataSource.connection

        val pstmt = cn.prepareStatement(SQL_QUERY)
        for (i in 0..10) {
            val sw = Stopwatch.createStarted()

            val result = pstmt.executeQuery()
            result.next()

            val maxSyncId = result.getLong(1)

            log.info("${maxSyncId} ${sw}")
        }
        cn.close()
    }
}