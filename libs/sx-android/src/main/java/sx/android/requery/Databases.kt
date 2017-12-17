package sx.android.requery

import io.requery.android.database.sqlite.SQLiteDatabase
import java.io.File

/**
 * Returns SQLite version
 * Created by masc on 10.10.17.
 */
val SQLiteDatabase.sqliteVersion: String
    get() = this.stringForQuery("SELECT sqlite_version()", null)

/**
 * Backup database file
 * Created by masc on 17.12.17.
 */
fun SQLiteDatabase.backupTo(destinationFile: File) {
    // Acquire write lock to prevent corruption of backup database (using empty immediate transaction)
    this.beginTransactionNonExclusive()
    try {
        File(this.path).copyTo(destinationFile, true)
    } finally {
        this.endTransaction()
    }
}