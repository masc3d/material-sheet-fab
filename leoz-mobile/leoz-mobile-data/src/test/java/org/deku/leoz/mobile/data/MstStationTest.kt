package org.deku.leoz.mobile.data

import org.junit.Test
import org.deku.leoz.android.data.jpa.*
import io.requery.sql.EntityDataStore
import io.requery.rx.SingleEntityStore
import io.requery.Persistable
import org.deku.leoz.android.data.jpa.Models
import io.requery.sql.TableCreationMode
import io.requery.rx.RxSupport
import org.sqlite.SQLiteDataSource
import org.sqlite.javax.SQLiteConnectionPoolDataSource
import java.sql.DriverManager


/**
 * Created by n3 on 09/12/2016.
 */
class MstStationTest {

    /**
     * @return [EntityDataStore] single instance for the application.
     * *
     *
     *
     * * Note if you're using Dagger you can make this part of your application level module returning
     * * `@Provides @Singleton`.
     */
    val entityStore by lazy {
        EntityDataStore<Persistable>(this.dataSource, Models.JPA)
    }

    val dataSource by lazy {
        val ds = SQLiteDataSource()
        ds.url = "jdbc:sqlite:build/db/leoz-mobile.db"
        ds
    }
    @Test
    fun testSelect() {
        val r = Models.JPA

        entityStore.select(MstStation::class.java).get().each {
            println(it)
        }
    }
}