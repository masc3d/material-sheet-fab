package org.deku.leoz.node.data

import com.querydsl.core.types.dsl.Param
import com.querydsl.jpa.impl.JPAQuery
import org.deku.leoz.node.config.DataTestConfiguration
import org.deku.leoz.node.data.jpa.MstStation
import org.deku.leoz.node.data.jpa.QMstRoute
import org.eclipse.persistence.config.HintValues
import org.eclipse.persistence.config.QueryHints
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional
import sx.Stopwatch
import sx.junit.PrototypeTest
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 * Created by masc on 18/10/2016.
 */
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(DataTestConfiguration::class))
@Category(PrototypeTest::class)
open class JpaNamedQueryTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Transactional
    @Test
    open fun testRuntimeNamedQuery() {
        val QUERY_NAME = "MstRoute.test"

        val qRoute = QMstRoute.mstRoute

        // TODO: query result cache currently only works for queries returning entities, not for custom data/single values/aggregates etc.
        val query = JPAQuery<MstStation>(entityManager)
                .from(qRoute)
                .where(qRoute.syncId.eq(1))
                .orderBy(qRoute.syncId.desc())
                .createQuery()

        query.setHint(QueryHints.QUERY_RESULTS_CACHE, HintValues.TRUE)
        query.setHint(QueryHints.QUERY_RESULTS_CACHE_SIZE, (500).toString())
        this.entityManager.entityManagerFactory.addNamedQuery(QUERY_NAME, query)

        val nq = this.entityManager
                .createNamedQuery(QUERY_NAME)

        for (i in 0..10) {
            val sw = Stopwatch.createStarted()

            nq.setParameter(1, 22060)
            nq.resultList

            log.info("${sw}")
        }
    }
}