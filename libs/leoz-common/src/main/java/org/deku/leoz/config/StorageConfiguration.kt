package org.deku.leoz.config

import com.google.common.base.Strings
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import sx.io.PermissionUtil
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.DosFileAttributeView
import kotlin.properties.Delegates

/**
 * Base class for loez local storage configurations
 * Created by masc on 26.06.15.
 */
abstract class StorageConfiguration(
        /** Base name for process specific files/directories */
        val appName: String) {
    /** Logger */
    private var log: Log = LogFactory.getLog(this.javaClass)

    // Directories

    /**
     * Common system application data path
     */
    var applicationDataPath: File by Delegates.notNull()

    /**
     * Private leoz directory
     */
    // TODO: change back to val once kotlin bug complaining about uninitialized val (even though it's initialized in init) is resolved
    var privateDirectory: File by Delegates.notNull()

    /**
     * Public leoz directory
     */
    val publicDirectory: File by lazy {
        val d = File(this.applicationDataPath, "Leoz")
        d.mkdirs()
        d
    }

    /** Local data directory */
    val dataDirectory: File by lazy({
        val d = File(File(this.privateDirectory, "data"), this.appName)
        d.mkdirs()
        d
    })

    /** Etc/settings directory */
    val etcDirectory: File by lazy({
        val d = File(File(this.privateDirectory, "etc"), this.appName)
        d.mkdirs()
        d
    })

    /** Local log directory */
    val logDirectory: File by lazy({
        val d = File(this.privateDirectory, "log");
        d.mkdirs()
        d
    })

    /** Local bundles directory */
    protected val bundlesDirectory: File by lazy({
        val d = File(this.privateDirectory, "bundles")
        d.mkdirs()
        d
    })

    val bundleInstallationDirectory: File by lazy({
        val d = File(this.bundlesDirectory, "install")
        d.mkdirs()
        d
    })

    /** Run directory, containing runtime related files, eg. bundle lock files */
    val runDirectory: File by lazy({
        val d = File(this.privateDirectory, "run")
        d.mkdirs()
        d
    })

    /** Lock file for this application */
    val bundleLockFile: File by lazy({
        this.lockFile(this.appName)
    })

    /** Application log file */
    val logFile: File by lazy({
        File(this.logDirectory, "${this.appName}.log")
    })

    /** Setup log file */
    val setupLogFile: File by lazy({
        File(this.logDirectory, "${this.appName}-setup.log")
    })

    /**
     * Lock file for specific bundle
     * @param bundleName Bundle name
     **/
    fun lockFile(bundleName: String): File {
        return File(this.runDirectory, bundleName + ".lock")
    }

    /** c'tor */
    init {
        // Initialize directories
        if (SystemUtils.IS_OS_WINDOWS) {
            this.applicationDataPath = File(System.getenv("ALLUSERSPROFILE"))
        } else {
            this.applicationDataPath = File(System.getProperty("user.home"))
        }
        if (Strings.isNullOrEmpty(this.applicationDataPath.name))
            throw UnsupportedOperationException("Base application data path could not be determined");

        var baseDirectoryName: String = ".leoz"

        this.privateDirectory = File(this.applicationDataPath, baseDirectoryName)
        this.log.trace("Storage configuration base directory [${privateDirectory}]")

        // Set permissions if the directory was created
        if (!this.privateDirectory.exists()) {
            try {
                this.privateDirectory.mkdirs()

                if (SystemUtils.IS_OS_WINDOWS) {
                    Files.getFileAttributeView(
                            this.privateDirectory.toPath(),
                            DosFileAttributeView::class.java).setHidden(true)

                    PermissionUtil.setAclAllowEverything(
                            path = this.privateDirectory,
                            principals = *arrayOf(
                                    PermissionUtil.Win32.SID.Users.fqn,
                                    PermissionUtil.Win32.SID.LocalSystem.fqn))
                }
            } catch(e: Exception) {
                if (this.privateDirectory.exists()) {
                    this.privateDirectory.deleteRecursively()
                }
                throw e
            }
        }

        if (!this.bundleLockFile.exists()) {
            this.bundleLockFile.createNewFile()
        }
    }

    /**
     * Initialize storage configuration
     */
    fun initalize() {
        // All intialization is passive (via c'tor) for now
    }
}
