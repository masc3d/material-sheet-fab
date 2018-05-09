package sx.io

import org.slf4j.LoggerFactory
import java.io.File
import java.nio.channels.FileChannel
import java.nio.channels.FileLock
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import sx.Process

/**
 * Process lock file.
 * Supports storing the pid of the lock owner in a supplemental file
 * @property lockFile Lock file
 * @property pidFile Optional PID file
 * Created by masc on 29/11/2016.
 */
class ProcessLockFile(
        val lockFile: File,
        val pidFile: File? = null) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /** File path */
    private val lockFilePath by lazy {
        this.lockFile.toPath()
    }

    /** File channel */
    private val lockFileChannel by lazy {
        FileChannel.open(
                this.lockFilePath,
                StandardOpenOption.READ, StandardOpenOption.WRITE)
    }

    /**
     * Stored pid
     */
    var pid: Long = 0

    private var lock: FileLock? = null

    val isOwner: Boolean
        get() = this.lock != null

    /**
     * c'tor
     */
    init {
        val lock = this.lockFileChannel.tryLock()
        if (lock != null) {
            this.lock = lock
            this.pid = Process.currentProcess.pid
            this.writePid()
        } else {
            this.readPid()
        }
    }

    /**
     * Internal helper for reading pid into instance variable
     */
    fun readPid() {
        if (this.pidFile == null)
            return

        this.pid = try {
            Files.newBufferedReader(this.pidFile.toPath()).use {
                it.readLine().toLong()
            }
        } catch(e: Exception) {
            0
        }
    }

    /**
     * Internal helper to write pid to pid file
     */
    fun writePid() {
        if (this.pidFile != null) {
            Files.newBufferedWriter(this.pidFile.toPath()).use {
                it.write("${this.pid}\n")
            }
        }
    }

    /**
     * Waits and acquires lock
     * @param writeCurrentProcessPid Write current process pid to pid file after acquiring lock
     */
    fun waitForLock(writeCurrentProcessPid: Boolean) {
        if (this.lock == null) {
            this.lock = this.lockFileChannel.lock()
            if (writeCurrentProcessPid) {
                this.pid = Process.currentProcess.pid
                this.writePid()
            }
        }
    }

    override fun toString(): String {
        return "PidFile(file=${this.lockFile}, pid=${this.pid}"
    }
}