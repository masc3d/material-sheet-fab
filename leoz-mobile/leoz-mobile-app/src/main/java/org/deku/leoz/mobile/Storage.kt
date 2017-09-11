package org.deku.leoz.mobile

import android.content.Context
import android.support.v4.content.ContextCompat
import org.slf4j.LoggerFactory
import java.io.File

/**
 * Storage, contains system and application specific paths and file locatinos
 */
class Storage(val context: Context) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Common data path
     */
    val dataDir: File by lazy {
        ContextCompat.getDataDir(this.context)
    }

    val externalDir: File by lazy {
        val dirs = ContextCompat.getExternalFilesDirs(this.context, null)
        log.info("External storage dirs [${dirs.joinToString(", ")}]")
        dirs.filter { it != null }.first()
    }

    val cacheDir: File by lazy {
        val cachePath = this.context.cacheDir
        log.info("Cache path [${cachePath}]")
        cachePath
    }

    val logDir: File by lazy {
        val d = this.dataDir.resolve("log")
        d.mkdirs()
        d
    }

    val imageDir: File by lazy {
        val d = this.dataDir.resolve("image")
        d.mkdirs()
        d
    }
}