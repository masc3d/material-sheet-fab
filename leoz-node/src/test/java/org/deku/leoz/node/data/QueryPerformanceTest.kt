package org.deku.leoz.node.data

import com.querydsl.jpa.impl.JPAQuery
import org.deku.leoz.node.DataTest
import org.deku.leoz.node.config.PersistenceConfiguration
import org.deku.leoz.node.data.entities.MstRoute
import org.deku.leoz.node.data.entities.MstStation
import org.deku.leoz.node.data.entities.QMstRoute
import org.deku.leoz.node.data.entities.QMstStation
import org.deku.leoz.node.data.repositories.master.RouteRepository
import org.eclipse.persistence.config.HintValues
import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.Transactional
import sx.Stopwatch
import sx.concurrent.Service
import sx.concurrent.task.CompositeExecutorService
import java.time.Duration
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.FlushModeType
import javax.persistence.PersistenceContext
import javax.persistence.criteria.CriteriaQuery
import javax.sql.DataSource

/**
 * Created by masc on 18/10/2016.
 */
open class QueryPerformanceTest : DataTest() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Inject
    private lateinit var dataSource: DataSource

    val QUERYDSL_QUERY by lazy {
        val qRoute = QMstRoute.mstRoute
        JPAQuery<MstRoute>(this.entityManager)
                .from(qRoute)
                .select(qRoute.syncId.max())
                .createQuery()
    }

    @Transactional
    @Test
    open fun testSelectQueryDsl() {
        for (i in 0..10) {
            val sw = Stopwatch.createStarted()
            val result = QUERYDSL_QUERY.singleResult
            log.info("${result} ${sw}")
        }
    }

    val CRITERIA_QUERY by lazy {
        val cb = this.entityManager.criteriaBuilder

        val cq = cb.createQuery(Number::class.java)

        val root = cq.from(MstRoute::class.java)
        val aggregate = cb.max(root.get("syncId"))

        this.entityManager.createQuery(
                cq.select(aggregate)
        )
    }

    @Transactional
    @Test
    open fun testSelectCriteriaApi() {
        for (i in 0..10) {
            val sw = Stopwatch.createStarted()
            val result = CRITERIA_QUERY.singleResult
            log.info("${result} ${sw}")
        }
    }

    val SQL_QUERY = "SELECT MAX(sync_id) FROM mst_route"

    @Transactional
    @Test
    open fun testSelectJpaNativeQuery() {
        val query = this.entityManager.createNativeQuery(SQL_QUERY)

        for (i in 0..10) {
            val sw = Stopwatch.createStarted()
            val result = query.singleResult
            log.info("${result} ${sw}")
        }
    }

    @Test
    open fun testSelectJdbc() {
        val cn = this.dataSource.connection
        for (i in 0..10) {
            val sw = Stopwatch.createStarted()

            val stmt = cn.createStatement()

            val result = stmt.executeQuery(SQL_QUERY)
            result.next()

            val maxSyncId = result.getLong(1)
            result.close()
            stmt.close()

            log.info("${maxSyncId} ${sw}")
        }
        cn.close()
    }

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