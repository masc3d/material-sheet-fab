package org.deku.leoz.node.data

import org.deku.leoz.node.config.DataTestConfiguration
import org.deku.leoz.node.data.jpa.QTadNodeGeoposition.tadNodeGeoposition
import org.deku.leoz.node.data.repository.SyncRepository
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import sx.Stopwatch
import sx.junit.PrototypeTest
import sx.log.slf4j.info
import javax.inject.Inject
import javax.persistence.EntityManager

/**
 * Created by masc on 18.01.18.
 */

/**
 * Created by masc on 18/10/2016.
 */
@Category(PrototypeTest::class)
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(DataTestConfiguration::class))
open class SyncRepositoryTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var entityManager: EntityManager

    private val REPETITIONS = 50

    @Inject
    private lateinit var repository: SyncRepository

    @Test
    fun testCountNewerThan() {
        for (i in 0..REPETITIONS) {
            val syncIds = this.repository.findSyncIdMinMax(
                    tadNodeGeoposition,
                    tadNodeGeoposition.syncId) ?: (0L..0L)

            val count = Stopwatch.createStarted(this, "COUNT NEWER", Level.INFO, {
                repository.countNewerThan(tadNodeGeoposition, tadNodeGeoposition.syncId, syncIds.endInclusive - 200000)
            })

            log.info { "Count newer -> ${count}" }
        }
    }

    @Test
    fun testFindNewerThan() {
        val syncIds = this.repository.findSyncIdMinMax(
                tadNodeGeoposition,
                tadNodeGeoposition.syncId) ?: (0L..0L)

        val cursor = repository.findNewerThan(
                tadNodeGeoposition,
                tadNodeGeoposition.syncId,
                syncIds.endInclusive - 10000,
                2000)

        var total = 0
        while (cursor.hasNext()) {
            cursor.next()
            total++
        }

        log.info("Found ${total}")
    }
}