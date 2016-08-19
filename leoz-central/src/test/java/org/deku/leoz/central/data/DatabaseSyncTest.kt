package org.deku.leoz.central.data

import com.google.common.base.Stopwatch
import org.deku.leoz.central.DataTest
import org.deku.leoz.central.services.DatabaseSyncService
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Inject

/**
 * Created by masc on 16.05.15.
 */
class DatabaseSyncTest : DataTest() {
    private val mLog = LoggerFactory.getLogger(DatabaseSyncTest::class.java)

    @Inject
    internal var mDatabaseSync: DatabaseSyncService? = null

    @Test
    fun test() {
        val sw = Stopwatch.createStarted()
        try {
            mDatabaseSync!!.sync(true)
        } finally {
            mLog.info(String.format("Took %s", sw.toString()))
        }
    }
}
