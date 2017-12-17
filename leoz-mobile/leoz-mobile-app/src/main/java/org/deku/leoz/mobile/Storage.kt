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
            ?: throw IllegalStateException("Data directory not available")
    }

    val externalDir: File by lazy {
        ContextCompat.getExternalFilesDirs(this.context, null).let {
            log.info("External storage dirs [${it.joinToString(", ")}]")
            it.filter { it != null }.first()
        }
    }

    val deviceManagementDir: File by lazy {
        this.externalDir.resolve("mdm").also {
            log.info("Device management dir [${it}]")
            it.mkdirs()
        }
    }

    val cacheDir: File by lazy {
        this.context.cacheDir.also {
            log.info("Cache dir [${it}]")
        }
    }

    val diagnosticsDir by lazy {
        this.dataDir.resolve("diagnostics").also {
            it.mkdirs()
        }
    }

    val logDir: File by lazy {
        this.dataDir.resolve("log").also {
            it.mkdirs()
        }
    }
}