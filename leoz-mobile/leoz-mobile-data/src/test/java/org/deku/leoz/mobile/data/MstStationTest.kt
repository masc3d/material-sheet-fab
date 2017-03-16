package org.deku.leoz.mobile.data

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.rxkotlin.subscribeBy
import io.requery.Persistable
import io.requery.rx.RxSupport
import io.requery.reactivex.ReactiveEntityStore
import io.requery.sql.EntityDataStore
import org.deku.leoz.mobile.data.jpa.Models
import org.deku.leoz.mobile.data.jpa.MstStation
import org.junit.Test
import org.sqlite.SQLiteDataSource
import org.sqlite.javax.SQLiteConnectionPoolDataSource
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable

/**
 * Created by n3 on 09/12/2016.
 */
class MstStationTest {
    companion object {
        init {
            Kodein.global.addImport(DatabaseConfiguration.module)
        }
    }

    val entityStore: ReactiveEntityStore<Persistable> by Kodein.global.lazy.instance()

    @Test
    fun testSelect() {
        val r = Models.JPA

        entityStore.select(MstStation::class.java).get().toObservable().subscribeBy(
                onNext = {
                    println(it)
                })
    }
}