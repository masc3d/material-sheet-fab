package org.deku.leoz.node.data

import com.querydsl.core.types.dsl.Param
import com.querydsl.jpa.impl.JPAQuery
import org.deku.leoz.node.config.DataTestConfiguration
import org.deku.leoz.node.config.PersistenceConfiguration
import org.deku.leoz.node.data.jooq.Tables
import org.deku.leoz.node.data.jpa.MstRoute
import org.deku.leoz.node.data.jpa.MstStation
import org.deku.leoz.node.data.jpa.QMstRoute
import org.deku.leoz.node.data.repository.RouteRepository
import org.eclipse.persistence.config.HintValues
import org.eclipse.persistence.config.QueryHints
import org.eclipse.persistence.sessions.factories.SessionManager
import org.eclipse.persistence.tools.profiler.PerformanceMonitor
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional
import sx.Stopwatch
import sx.junit.PrototypeTest
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceContext
import javax.sql.DataSource

/**
 * Created by masc on 18/10/2016.
 */
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(DataTestConfiguration::class))
@Category(PrototypeTest::class)
class QueryPerformanceTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Inject
    private lateinit var entityManagerFactory: EntityManagerFactory

    @Qualifier(PersistenceConfiguration.QUALIFIER)
    @Inject
    private lateinit var dataSource: DataSource

    @Inject
    private lateinit var routeRepository: RouteRepository

    @Inject
    private lateinit var dsl: DSLContext

    private val executorService = Executors.newCachedThreadPool()

    fun run(block: () -> Unit, threads: Int = 1, repeat: Int = 1) {
        val sw = Stopwatch.createStarted()
        val threadlist = mutableListOf<Future<*>>()
        for (i in 0..threads) {
            threadlist.add(executorService.submit {
                try {
                    val times = mutableListOf<Long>()
                    for (j in 0..repeat) {
                        val sw2 = Stopwatch.createStarted()
                        block()
                        times.add(sw2.elapsed(TimeUnit.MICROSECONDS))
                    }
                    var avg = 0L
                    times.forEach { avg += it }
                    avg /= times.count()

                    log.info("Medium execution time ${avg}µs")
                } catch (e: Throwable) {
                    log.error(e.message, e)
                }
            })
        }
        threadlist.forEach { it.get() }
        log.info("${sw}")
    }

    @Transactional
    @Test
    open fun testSelectMaxQueryDsl() {
        val qRoute = QMstRoute.mstRoute
        val q = JPAQuery<MstRoute>(this.entityManagerFactory.createEntityManager())
                .from(qRoute)
                .select(qRoute.syncId.max())
                .createQuery()

        for (i in 0..1000)
            run(
                    block = {
                        q.singleResult
                    },
                    threads = 4,
                    repeat = 1000
            )
    }

    @Transactional
    @Test
    open fun testSelectMaxQueryDslInline() {
        for (i in 0..1000)
            run(
                    block = {
                        val qRoute = QMstRoute.mstRoute
                        val q = JPAQuery<MstRoute>(this.entityManagerFactory.createEntityManager())
                                .from(qRoute)
                                .select(qRoute.syncId.max())
                                .createQuery()

                        q.singleResult
                    },
                    threads = 4,
                    repeat = 1000
            )
    }

    @Transactional
    @Test
    open fun testSelectMaxQueryDslCached() {
        val qRoute = QMstRoute.mstRoute

        val QUERY_NAME = "MstRoute.MaxSyncId"

        // TODO: query result cache currently only works for queries returning entities, not for custom data/single values/aggregates etc.
        val qdslQuery = JPAQuery<MstRoute>(this.entityManagerFactory.createEntityManager())
                .from(qRoute)
                .select(qRoute.syncId.max())

        val query = qdslQuery.createQuery()

        query.toString()
        query.setHint(QueryHints.QUERY_RESULTS_CACHE, HintValues.TRUE)
        query.setHint(QueryHints.QUERY_RESULTS_CACHE_SIZE, (500).toString())
        this.entityManager.entityManagerFactory.addNamedQuery(QUERY_NAME, query)

        for (i in 0..1000)
            run(
                    block = {
                        val nq = this.entityManager
                                .createNamedQuery(QUERY_NAME)

//                        nq.setParameter(1, 100)
                        nq.resultList
//                        log.info("${result}")
                    },
                    threads = 4,
                    repeat = 1000
            )
    }

    @Transactional
    @Test
    open fun testSelectEntityQueryDsl() {
        val qRoute = QMstRoute.mstRoute

        for (i in 0..1000)
            run(
                    block = {
                        val sw = Stopwatch.createStarted()
                        val ts = Timestamp(Date().time)
                        val result = JPAQuery<MstRoute>(this.entityManager)
                                .from(qRoute)
                                .where(qRoute.layer.eq(1)
                                        .and(qRoute.country.eq("DE"))
                                        .and(qRoute.zipFrom.loe("50181"))
                                        .and(qRoute.zipTo.goe("50181"))
                                        .and(qRoute.validFrom.before(ts))
                                        .and(qRoute.validTo.after(ts))
                                )
                                .createQuery()
                                .resultList

                        log.info("${sw} ${result.count()} ${ts}")
                    },
                    threads = 4,
                    repeat = 1000
            )
    }

    @Transactional
    @Test
    open fun testSelectEntityQueryDslRepository() {
        val qRoute = QMstRoute.mstRoute

        val session = SessionManager.getManager().sessions.asIterable().first().value
        val profiler = session.profiler

        val r = Random()

        for (i in 0..500)
            run(
                    block = {
                        //                        val sw = Stopwatch.createStarted()
                        this.routeRepository.findAll(qRoute.syncId.eq(r.nextInt(100).toLong()))
//                        log.info("${result}")
                    },
                    threads = 4,
                    repeat = 1000
            )

        if (profiler is PerformanceMonitor)
            profiler.dumpResults()
    }

    @Transactional
    @Test
    open fun testSelectEntityQueryDslParameterized() {
        val qRoute = QMstRoute.mstRoute

        val pSyncId = Param(Long::class.java)
        val q = JPAQuery<MstRoute>(this.entityManagerFactory.createEntityManager())
                .from(qRoute)
                .where(qRoute.syncId.eq(pSyncId))

        for (i in 0..1000)
            run(
                    block = {
                        //                        val sw = Stopwatch.createStarted()
                        q
                                .set(pSyncId, 100)
                                .createQuery()
                                .singleResult
//                        log.info("${result}")
                    },
                    threads = 4,
                    repeat = 1000
            )
    }

    @Transactional
    @Test
    open fun testSelectEntityQueryDslCached() {
        val qRoute = QMstRoute.mstRoute
        val pSyncId = Param(Long::class.java)

        val QUERY_NAME = "MstRoute.SyncId"

        // TODO: query result cache currently only works for queries returning entities, not for custom data/single values/aggregates etc.
        val qdslQuery = JPAQuery<MstStation>(entityManager)
                .from(qRoute)
                .where(qRoute.syncId.eq(pSyncId))
                .orderBy(qRoute.syncId.desc())
                .set(pSyncId, 0)

        val query = qdslQuery.createQuery()

        query.toString()
        query.setHint(QueryHints.QUERY_RESULTS_CACHE, HintValues.TRUE)
        query.setHint(QueryHints.QUERY_RESULTS_CACHE_SIZE, (500).toString())
        this.entityManager.entityManagerFactory.addNamedQuery(QUERY_NAME, query)

        val r = Random()

        for (i in 0..1000)
            run(
                    block = {
                        val nq = this.entityManager
                                .createNamedQuery(QUERY_NAME)

                        nq.setParameter(1, r.nextInt(100).toLong())
                        nq.resultList
//                        log.info("${result}")
                    },
                    threads = 4,
                    repeat = 1000
            )
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

        run(
                block = {
                    val sw = Stopwatch.createStarted()

                    val stmt = cn.createStatement()

                    val result = stmt.executeQuery(SQL_QUERY)
                    result.next()

                    val maxSyncId = result.getLong(1)
                    result.close()
                    stmt.close()

                    log.info("${maxSyncId} ${sw}")
                },
                threads = 10,
                repeat = 10
        )

        cn.close()
    }

    @Test
    open fun testSelectJdbcPrepared() {
        val cn = this.dataSource.connection

        val p = ThreadLocal.withInitial {
            cn.prepareStatement(SQL_QUERY)
        }
        for (i in 0..100) {
            run(
                    block = {
                        val pstmt = p.get()

                        val result = pstmt.executeQuery()
                        result.next()

                        result.getLong(1)
                    },
                    threads = 4,
                    repeat = 1000
            )

        }
        cn.close()
    }

    @Transactional(PersistenceConfiguration.QUALIFIER)
    @Test
    open fun testSelectMaxJooqPrepared() {
        val field = Tables.MST_ROUTE.SYNC_ID.max()

        val p = ThreadLocal.withInitial {
            this.dsl
                    .select(field)
                    .from(Tables.MST_ROUTE)
                    .keepStatement(true)
        }
        for (i in 0..1000)
            run(
                    block = {

                        p.get().fetchOne().get(field)
//                        log.info(r)
                    },
                    threads = 4,
                    repeat = 1000)

    }

    @Transactional(PersistenceConfiguration.QUALIFIER)
    @Test
    open fun testSelectMaxJooqInline() {
        val field = Tables.MST_ROUTE.SYNC_ID.max()

        for (i in 0..1000)
            run(
                    block = {

                        this.dsl
                                .select(field)
                                .from(Tables.MST_ROUTE)
                    },
                    threads = 4,
                    repeat = 1000)

    }

    @Transactional(PersistenceConfiguration.QUALIFIER)
    @Test
    open fun testSelectEntityJooqPrepared() {
        val tRoute = Tables.MST_ROUTE

        val pSyncId = DSL.param("syncid", Long::class.java)
        val p = ThreadLocal.withInitial {
            this.dsl
                    .select()
                    .from(tRoute)
                    .where(tRoute.SYNC_ID.eq(pSyncId))
                    .keepStatement(true)
        }

        val r = Random()

        for (i in 0..500)
            run(
                    block = {
                        p.get()
                                .bind(pSyncId.name, r.nextInt(100).toLong())
                                .fetchInto(MstRoute::class.java)
                    },
                    threads = 1,
                    repeat = 1000)

    }

    @Transactional(PersistenceConfiguration.QUALIFIER)
    @Test
    open fun testSelectEntityJooq() {
        val tRoute = Tables.MST_ROUTE

        val r = Random()

        for (i in 0..1000)
            run(
                    block = {
                        this.dsl
                                .select()
                                .from(tRoute)
                                .where(tRoute.SYNC_ID.eq(r.nextInt(100).toLong()))
                                .fetchInto(MstRoute::class.java)
                    },
                    threads = 4,
                    repeat = 1000)

    }

    @Transactional
    @Test
    open fun testEntityManagerFind() {
        for (i in 0..1000)
            run(
                    block = {
                        this.entityManager.find(MstRoute::class.java, 46182L)
                    },
                    threads = 4,
                    repeat = 1000
            )
    }
}