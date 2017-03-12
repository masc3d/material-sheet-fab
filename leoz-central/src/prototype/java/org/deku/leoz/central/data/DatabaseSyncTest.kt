package org.deku.leoz.central.data

import com.google.common.base.Stopwatch
import org.deku.leoz.central.config.DataTestConfiguration
import org.deku.leoz.central.service.DatabaseSyncService
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import sx.junit.PrototypeTest
import javax.inject.Inject

/**
 * Created by masc on 16.05.15.
 */
@Category(PrototypeTest::class)
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(
        DataTestConfiguration::class,
        org.deku.leoz.central.config.DatabaseSyncConfiguration::class,
        org.deku.leoz.central.service.DatabaseSyncService::class
))
class DatabaseSyncTest {
    private val log = LoggerFactory.getLogger(DatabaseSyncTest::class.java)

    @Inject
    private lateinit var databaseSync: DatabaseSyncService

    @Test
    fun test() {
        val sw = Stopwatch.createStarted()
        try {
            this.databaseSync.sync(true)
        } finally {
            log.info(String.format("Took %s", sw.toString()))
        }
    }
}
