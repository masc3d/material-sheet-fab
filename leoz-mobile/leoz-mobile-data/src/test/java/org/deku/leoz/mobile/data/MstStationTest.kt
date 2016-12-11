package org.deku.leoz.mobile.data

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import io.requery.Persistable
import io.requery.rx.RxSupport
import io.requery.rx.SingleEntityStore
import io.requery.sql.EntityDataStore
import org.deku.leoz.android.data.jpa.Models
import org.deku.leoz.android.data.jpa.MstStation
import org.junit.Test
import org.sqlite.SQLiteDataSource
import org.sqlite.javax.SQLiteConnectionPoolDataSource
import rx.lang.kotlin.subscribeWith

/**
 * Created by n3 on 09/12/2016.
 */
class MstStationTest {
    companion object {
        init {
            Kodein.global.addImport(DatabaseConfiguration.module)
        }
    }

    val entityStore: SingleEntityStore<Persistable> by Kodein.global.lazy.instance()

    @Test
    fun testSelect() {
        val r = Models.JPA

        entityStore.select(MstStation::class.java).get().toObservable().subscribeWith {
            onNext {
                println(it)
            }
        }
    }
}