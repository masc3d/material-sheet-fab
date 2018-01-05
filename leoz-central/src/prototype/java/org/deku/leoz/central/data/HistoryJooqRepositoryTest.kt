package org.deku.leoz.central.data

import org.deku.leoz.central.config.DataTestConfiguration
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.repository.JooqHistoryRepository
import org.jooq.DSLContext
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import sx.junit.PrototypeTest

import javax.inject.Inject

/**
 * Created by masc on 02.07.15.
 */
@Category(PrototypeTest::class)
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(
        DataTestConfiguration::class
))
class HistoryJooqRepositoryTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Qualifier(PersistenceConfiguration.QUALIFIER)
    @Inject
    private lateinit var dsl: DSLContext

    @Inject
    private lateinit var historyRepository: JooqHistoryRepository

    @Test
    fun testAdd() {
        val record = dsl.newRecord(Tables.TBLHISTORIE)
        record.info = "TEST"
        historyRepository.save(record)
    }
}
