package sx.android.database.sqlite

import android.database.sqlite.SQLiteDatabase
import java.io.File

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