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
import org.junit.Test
import org.sqlite.SQLiteDataSource
import org.sqlite.javax.SQLiteConnectionPoolDataSource
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import java.sql.Timestamp
import java.util.*

/**
 * Created by n3 on 09/12/2016.
 */
//class RequeryTest {
//    companion object {
//        init {
//            Kodein.global.addImport(DatabaseConfiguration.module)
//        }
//    }
//
//    val entityStore: ReactiveEntityStore<Persistable> by Kodein.global.lazy.instance()
//
//    @Test
//    fun testSelect() {
//        val r = Models.REQUERY
//
//        entityStore
//                .select(StationEntity::class.java)
//                .get()
//                .toObservable().subscribeBy(
//                onNext = {
//                    println(it)
//                })
//    }
//
//    @Test
//    fun testInsert() {
//        val r = Models.REQUERY
//
//        val e = StationEntity()
//        e.address1 = "1234"
//        e.timestamp = Timestamp(0)
//        e.syncId = 0
//        e.stationNr = 50
//        entityStore
//                .insert(e)
//                .blockingGet()
//    }
//}