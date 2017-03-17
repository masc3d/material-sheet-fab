package org.deku.leoz.node.data

import com.querydsl.core.types.dsl.Param
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.sql.Configuration
import com.querydsl.sql.H2Templates
import com.querydsl.sql.SQLQueryFactory
import org.deku.leoz.node.config.DataTestConfiguration
import org.deku.leoz.node.data.jpa.MstRoute
import org.deku.leoz.node.data.jpa.QMstRoute
import org.deku.leoz.node.data.repository.master.RouteRepository
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional
import sx.Stopwatch
import sx.junit.PrototypeTest
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
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
open class RouteRepositoryTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var entityManager: EntityManager

    @Inject
    private lateinit var routeRepository: RouteRepository

    @Inject
    private lateinit var dataSource: DataSource

    @Test
    open fun testFindAll() {
        val qRoute = QMstRoute.mstRoute

        routeRepository.findAll(
                qRoute.timestamp.gt(Timestamp.valueOf(LocalDateTime.of(2014, 1, 1, 0, 0, 0))))
                .forEach {
            log.info("${it} ${it.station}")
            return
        }
    }

    @Transactional
    @Test
    open fun testFind() {
        for (i in 0..20) {
            val sw = Stopwatch.createStarted()
            val result = routeRepository.findOne(46179)
            log.info("${sw} ${result}")
        }
    }

    @Transactional
    @Test
    open fun testFindQueryDsl() {
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

        for (i in 0..2000000) {
            val sw = Stopwatch.createStarted()
            val result = entityManager
                    .createNamedQuery(query.toString())
                    .setParameter(1, 1)
                    .setParameter(2, "DE")
                    .setParameter(3, "63571")
                    .resultList
            log.info("${sw} ${result.count()}")
        }
    }

    @Transactional
    @Test
    open fun testFindQueryDsl2() {
        val qRoute = QMstRoute.mstRoute

        for (i in 0..20) {
            val sw = Stopwatch.createStarted()
            val result = routeRepository.findAll(
                    qRoute.layer.eq(1)
                            .and(qRoute.country.eq("DE"))
                            .and(qRoute.zipFrom.eq("63571")))
            log.info("${sw} ${result.count()}")
        }
    }

    val SQL_QUERY = "SELECT * FROM mst_route WHERE layer=1 AND country='DE' AND zip_from='63571'"

    @Test
    open fun testSelectJdbcPrepared() {
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