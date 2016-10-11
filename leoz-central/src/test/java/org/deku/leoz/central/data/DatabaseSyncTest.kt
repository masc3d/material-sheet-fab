package org.deku.leoz.central.data

import com.google.common.base.Stopwatch
import org.deku.leoz.central.DataTest
import org.deku.leoz.central.services.DatabaseSyncService
import org.junit.Test
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Import
import javax.inject.Inject

/**
 * Created by masc on 16.05.15.
 */
@Import(
        org.deku.leoz.central.config.DatabaseSyncConfiguration::class,
        org.deku.leoz.central.services.DatabaseSyncService::class)
class DatabaseSyncTest : DataTest() {
    private val log = LoggerFactory.getLogger(DatabaseSyncTest::class.java)

    @Inject
    internal var databaseSync: DatabaseSyncService? = null

    @Test
    fun test() {
        val sw = Stopwatch.createStarted()
        try {
            databaseSync!!.sync(true)
        } finally {
            log.info(String.format("Took %s", sw.toString()))
        }
    }
}
