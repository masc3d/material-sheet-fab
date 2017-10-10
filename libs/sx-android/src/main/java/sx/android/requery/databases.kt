package sx.android.requery

import io.requery.android.database.sqlite.SQLiteDatabase

/**
 * Returns SQLite version
 * Created by masc on 10.10.17.
 */
val SQLiteDatabase.sqliteVersion: String
    get() = this.stringForQuery("SELECT sqlite_version()", null)

val android.database.sqlite.SQLiteDatabase.sqliteVersion: String
    get() =
        this.compileStatement("SELECT sqlite_version()").let {
            it.use {
                it.simpleQueryForString()
            }
        }
