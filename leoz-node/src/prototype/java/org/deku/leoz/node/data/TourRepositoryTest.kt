package org.deku.leoz.node.data

import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.PathBuilder
import org.deku.leoz.node.config.DataTestConfiguration
import org.deku.leoz.node.config.PersistenceConfiguration
import org.deku.leoz.node.data.jpa.QTadTour.tadTour
import org.deku.leoz.node.data.jpa.TadTour
import org.deku.leoz.node.data.repository.TadTourRepository
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import sx.junit.PrototypeTest
import sx.persistence.querydsl.from
import sx.persistence.withEntityManager
import java.util.*
import javax.inject.Inject
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceUnit

/**
 * Created by masc on 03.04.2018.
 */
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(DataTestConfiguration::class))
@Category(PrototypeTest::class)
class TourRepositoryTest {
    @Inject
    private lateinit var tourRepository: TadTourRepository

    @PersistenceUnit(name = PersistenceConfiguration.QUALIFIER)
    private lateinit var emf: EntityManagerFactory

    @Test
    fun testFindAll() {
        for (d in tourRepository.findAll()) {
            println(d)
        }
    }
}
