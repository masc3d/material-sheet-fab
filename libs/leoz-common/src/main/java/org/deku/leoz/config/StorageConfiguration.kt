package org.deku.leoz.config

import com.google.common.base.Strings
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.attribute.*
import java.util.*
import kotlin.properties.Delegates

/**
 * Base class for loez local storage configurations
 * Created by masc on 26.06.15.
 */
abstract class StorageConfiguration(
        /** Base name for process specific files/directories */
        val appName: String) {
    private var log: Log = LogFactory.getLog(this.javaClass)

    // Directories
    /** Local home directory */
    // TODO: change back to val once kotlin bug complaining about uninitialized val (even though it's initialized in init) is resolved
    var baseDirectory: File by Delegates.notNull()

    /** Local data directory */
    val dataDirectory: File by lazy({
        val d = File(File(this.baseDirectory, "data"), this.appName)
        d.mkdirs()
        d
    })

    /** Etc/settings directory */
    val etcDirectory: File by lazy({
        val d = File(File(this.baseDirectory, "etc"), this.appName)
        d.mkdirs()
        d
    })

    /** Local log directory */
    val logDirectory: File by lazy({
        val d = File(this.baseDirectory, "log");
        d.mkdirs()
        d
    })

    /** Local bundles directory */
    protected val bundlesDirectory: File by lazy({
        val d = File(this.baseDirectory, "bundles")
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
        val d = File(this.baseDirectory, "run")
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
        var basePath: File
        var baseDirectoryName: String
        if (SystemUtils.IS_OS_WINDOWS) {
            basePath = File(System.getenv("ALLUSERSPROFILE"))
            baseDirectoryName = "Leoz"
        } else {
            basePath = File(System.getProperty("user.home"))
            baseDirectoryName = ".leoz"
        }
        if (Strings.isNullOrEmpty(basePath.name))
            throw UnsupportedOperationException("Basepath is empty");

        this.baseDirectory = File(basePath, baseDirectoryName)
        this.log.info("Home directory [${baseDirectory}]")

        var baseDirectory = this.baseDirectory.exists()
        this.baseDirectory.mkdirs()
        // Set permissions if the directory was created
        if (!baseDirectory) {
            if (SystemUtils.IS_OS_WINDOWS) {
                // Get file attribute view
                var fav = Files.getFileAttributeView(this.baseDirectory.toPath(), AclFileAttributeView::class.java)

                // Lookup principal
                var fs = FileSystems.getDefault()
                var ups: UserPrincipalLookupService = fs.userPrincipalLookupService
                var gp = ups.lookupPrincipalByGroupName("Everyone")

                // Set ACL
                var aclb = AclEntry.newBuilder()
                aclb.setPermissions(EnumSet.allOf(AclEntryPermission::class.java))
                aclb.setPrincipal(gp)
                aclb.setFlags(AclEntryFlag.DIRECTORY_INHERIT, AclEntryFlag.FILE_INHERIT)
                aclb.setType(AclEntryType.ALLOW)
                fav.acl = Collections.singletonList(aclb.build())
            }
        }

        if (!this.bundleLockFile.exists()) {
            this.bundleLockFile.createNewFile()
        }
    }

    protected fun finalize() {
        log.info("FINALIZED")
    }
}
