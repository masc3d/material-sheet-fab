package org.deku.leoz.mobile

import android.content.Context
import android.support.v4.content.ContextCompat

/**
 * Storage, contains system and application specific paths and file locatinos
 */
class Storage(val context: Context) {

    /**
     * Common data path
     */
    val dataPath by lazy {
        ContextCompat.getDataDir(this.context)
    }

    val logPath by lazy {
        val d = this.dataPath.resolve("log")
        d.mkdirs()
        d
    }
}