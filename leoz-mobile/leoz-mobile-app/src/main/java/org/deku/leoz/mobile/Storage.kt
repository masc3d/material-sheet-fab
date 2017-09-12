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

    val deviceManagementDir: File by lazy {
        File(this.externalDir, "mdm").also {
            log.info("Device management dir [${it}]")
            it.mkdirs()
        }
    }

    val cacheDir: File by lazy {
        this.context.cacheDir.also {
            log.info("Cache dir [${it}]")
        }
    }

    val logDir: File by lazy {
        this.dataDir.resolve("log").also {
            it.mkdirs()
        }
    }

    val imageDir: File by lazy {
        this.dataDir.resolve("image").also {
            it.mkdirs()
        }
    }
}