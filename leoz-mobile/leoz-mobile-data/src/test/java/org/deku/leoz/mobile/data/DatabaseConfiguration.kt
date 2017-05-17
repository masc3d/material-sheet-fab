package org.deku.leoz.mobile.data

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.support.ConnectionSource
import io.requery.Persistable
import io.requery.reactivex.ReactiveSupport
import io.requery.reactivex.ReactiveEntityStore
import io.requery.sql.EntityDataStore
import org.deku.leoz.mobile.data.requery.Models
import org.sqlite.SQLiteDataSource
import javax.sql.DataSource

/**
 * Database configuration
 * Created by n3 on 10/12/2016.
 */
class DatabaseConfiguration {
    companion object {
        val JDBC_URL = "jdbc:sqlite:leoz-mobile-data/build/db/leoz-mobile.db"

        val module = Kodein.Module {
            bind<DataSource>() with singleton {
                val ds = SQLiteDataSource()
                ds.url = JDBC_URL
                ds
            }

            bind<ConnectionSource>() with singleton {
                JdbcConnectionSource(JDBC_URL)
            }

            bind<ReactiveEntityStore<Persistable>>() with singleton {
                ReactiveSupport.toReactiveStore(
                        EntityDataStore<Persistable>(instance(), Models.REQUERY)
                )
            }
        }
    }
}