package org.deku.leoz.node.data

import org.deku.leoz.node.config.DataTestConfiguration
import org.deku.leoz.node.data.jpa.TadNodeGeoposition
import org.deku.leoz.node.data.repository.EntityRepository
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
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
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(DataTestConfiguration::class))
@Category(PrototypeTest::class)
open class EntityRepositoryTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var entityManager: EntityManager

    @Test
    fun testCountNewerThan() {
        EntityRepository(this.entityManager, TadNodeGeoposition::class.java).also {
            log.info { "Count newer -> ${it.countNewerThan(7100000, 10000)}" }
        }
    }
}