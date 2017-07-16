package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.reactivex.ReactiveSupport
import io.requery.reactivex.ReactiveEntityStore
import io.requery.sql.*
import org.sqlite.SQLiteDataSource
import java.io.File
import javax.sql.CommonDataSource
import javax.sql.DataSource
import org.deku.leoz.mobile.model.Models

/**
 * Database configuration for local tests
 * Created by n3 on 10/12/2016.
 */
class DatabaseTestConfiguration {
    companion object {
        val path = File("build/db/leoz-mobile.db")

        /** Path of the flyway generated local database */
        val JDBC_URL = "jdbc:sqlite:build/db/leoz-mobile.db"

        val module = Kodein.Module {
            path.parentFile.mkdirs()

            val url = "jdbc:sqlite:${path.toURI().path}"

            bind<DataSource>() with singleton {
                val ds = SQLiteDataSource()
                ds.url = url
                ds
            }

            bind<CommonDataSource>() with singleton {
                instance<DataSource>()
            }

            bind<KotlinReactiveEntityStore<Persistable>>() with singleton {
                SchemaModifier(instance(), Models.DEFAULT)
                        .createTables(TableCreationMode.CREATE_NOT_EXISTS)

                KotlinReactiveEntityStore<Persistable>(
                        KotlinEntityDataStore<Persistable>(
                                KotlinConfiguration(
                                        dataSource = instance(),
                                        model = Models.DEFAULT)))
            }
        }
    }
}