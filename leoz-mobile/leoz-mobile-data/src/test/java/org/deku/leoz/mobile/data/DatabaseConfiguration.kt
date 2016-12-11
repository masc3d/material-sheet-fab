package org.deku.leoz.mobile.data

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import io.requery.Persistable
import io.requery.rx.RxSupport
import io.requery.rx.SingleEntityStore
import io.requery.sql.EntityDataStore
import org.deku.leoz.android.data.jpa.Models
import org.sqlite.SQLiteDataSource
import javax.sql.DataSource

/**
 * Database configuration
 * Created by n3 on 10/12/2016.
 */
class DatabaseConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<DataSource>() with singleton {
                val ds = SQLiteDataSource()
                ds.url = "jdbc:sqlite:build/db/leoz-mobile.db"
                ds
            }

            bind<SingleEntityStore<Persistable>>() with singleton {
                RxSupport.toReactiveStore(
                        EntityDataStore<Persistable>(instance(), Models.JPA)
                )
            }
        }
    }
}