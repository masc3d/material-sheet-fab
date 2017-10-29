package org.deku.leoz

import com.google.common.base.Strings
import org.apache.commons.lang3.SystemUtils
import org.slf4j.LoggerFactory
import sx.io.PermissionUtil
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.DosFileAttributeView
import kotlin.properties.Delegates

/**
 * Leoz core storage class, resembles the top level storage/directory structure for all leoz-core based projects
 * Created by masc on 26.06.15.
 */
open class Storage(
        val baseName: String = "leoz",
        /** Base name for process specific files/directories */
        val appName: String,
        /** Private base directory. Defaults to a system specific path */
        val privateBaseDirectory: File = DEFAULT_PRIVATE_BASE_DIRECTORY,
        /** Public base directory. Defaults ot a system specific path */
        val publicBaseDirectory: File = DEFAULT_PUBLIC_BASE_DIRECTORY) {
    /** Logger */
    private val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        val DEFAULT_PRIVATE_BASE_DIRECTORY =
                if (SystemUtils.IS_OS_WINDOWS) File(System.getenv("ALLUSERSPROFILE")) else File(System.getProperty("user.home"))

        val DEFAULT_PUBLIC_BASE_DIRECTORY =
                if (SystemUtils.IS_OS_WINDOWS) File(System.getenv("PUBLIC")) else DEFAULT_PRIVATE_BASE_DIRECTORY
    }

    // Directories

    /** The process owning user's home directory */
    val userHomeDirectory by lazy {
        when {
            SystemUtils.IS_OS_WINDOWS -> File(System.getenv("USERPROFILE"))
            else -> File(System.getProperty("user.home"))
        }
    }

    /**
     * Private base directory name
     */
    val privateDirectoryName by lazy {
        if (SystemUtils.IS_OS_WINDOWS)
            baseName.capitalize()
        else
            ".${baseName.toLowerCase()}"
    }

    /**
     * Private leoz directory
     */
    val privateDirectory by lazy {
        File(this.privateBaseDirectory, privateDirectoryName).also {
            if (!it.exists()) {
                try {
                    it.mkdirs()

                    // Set permissions if the directory was created
                    if (SystemUtils.IS_OS_WINDOWS) {
                        Files.getFileAttributeView(
                                it.toPath(),
                                DosFileAttributeView::class.java).setHidden(true)

                        PermissionUtil.setAclAllowEverything(
                                path = it,
                                principals = *arrayOf(
                                        PermissionUtil.Win32.SID.Users.fqn,
                                        PermissionUtil.Win32.SID.LocalSystem.fqn))
                    }
                } catch (e: Exception) {
                    if (it.exists()) {
                        it.deleteRecursively()
                    }
                    throw e
                }
            }
        }
    }

    /**
     * Public leoz directory
     */
    val publicDirectory: File by lazy {
        File(this.publicBaseDirectory, this.baseName.capitalize())
                .also { it.mkdirs() }
    }

    /** Local data directory */
    val dataDirectory: File by lazy {
        File(File(this.privateDirectory, "data"), this.appName)
                .also { it.mkdirs() }
    }

    /** Etc/settings directory */
    val etcDirectory: File by lazy {
        File(File(this.privateDirectory, "etc"), this.appName)
                .also { it.mkdirs() }
    }

    /** Local log directory */
    val logDirectory: File by lazy {
        File(this.privateDirectory, "log")
                .also { it.mkdirs() }
    }

    /** Local bundles directory */
    protected val bundlesDirectory: File by lazy {
        File(this.privateDirectory, "bundles")
                .also { it.mkdirs() }
    }

    val bundleInstallationDirectory: File by lazy {
        val d = File(this.bundlesDirectory, "install")
        d.mkdirs()
        d
    }

    /** Run directory, containing runtime related files, eg. bundle lock files */
    val runDirectory: File by lazy {
        File(this.privateDirectory, "run")
                .also { it.mkdirs() }
    }

    /** Create bundle lock filename */
    fun bundleLockFileName(appName: String): String {
        return appName + ".lock"
    }

    /** Lock file for this application */
    val bundleLockFile: File by lazy {
        File(this.runDirectory, this.bundleLockFileName(this.appName))
    }

    /** Create bundle pid file name */
    fun bundlePidFileName(appName: String): String {
        return appName + ".pid"
    }

    val bundlePidFile: File by lazy {
        File(this.runDirectory, this.bundlePidFileName(this.appName))
    }

    /** Application log file */
    val logFile: File by lazy {
        File(this.logDirectory, "${this.appName}.log")
    }

    /** Setup log file */
    val setupLogFile: File by lazy {
        File(this.logDirectory, "${this.appName}-setup.log")
    }

    /** c'tor */
    init {
        // Initialize directories
        if (Strings.isNullOrEmpty(this.privateBaseDirectory.name))
            throw UnsupportedOperationException("Base application data path could not be determined");

        this.log.trace("Storage configuration base directory [${privateDirectory}]")

        if (!this.bundleLockFile.exists()) {
            this.bundleLockFile.createNewFile()
        }
    }
}
