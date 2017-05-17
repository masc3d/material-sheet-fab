package org.deku.leoz.mobile.data

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.support.ConnectionSource
import org.deku.leoz.mobile.data.ormlite.MstStation
import org.junit.Test
import java.sql.Timestamp

/**
 * ORMlite tests
 * Created by masc on 17.05.17.
 */
class OrmliteTest {
    companion object {
        init {
            Kodein.global.addImport(DatabaseConfiguration.module)
        }
    }

    val connectionSource by Kodein.global.lazy.instance<ConnectionSource>()

    @Test
    fun testInsert() {
        val station = MstStation()
        station.address1 = "1234"
        station.timestamp = Timestamp(0)
        station.syncId = 0
        station.stationNr = 50

        val dao = DaoManager.createDao(connectionSource, MstStation::class.java)
        dao.create(station)
    }
}