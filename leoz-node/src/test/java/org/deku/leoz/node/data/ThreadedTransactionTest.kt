package org.deku.leoz.node.data

import org.deku.leoz.node.config.PersistenceConfiguration
import org.deku.leoz.node.config.DataTestConfiguration
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceUnit

/**
 * Created by masc on 27.06.15.
 */
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(DataTestConfiguration::class))
class ThreadedTransactionTest {

    @PersistenceUnit(name = PersistenceConfiguration.QUALIFIER)
    private lateinit var mEntityManagerFactory: EntityManagerFactory

    @Test
    fun testThreadedTransactions() {

    }
}
