package org.deku.leoz

import com.google.common.base.Strings
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import java.io.File
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.*
import java.util.*
import kotlin.properties.Delegates

/**
 * Local Storage
 * Created by masc on 26.06.15.
 */
open class LocalStorage {
    private var log: Log = LogFactory.getLog(this.javaClass)

    /** Base name for process specific files/directories */
    var appName: String by Delegates.notNull()

    // Directories
    /** Local home directory */
    val baseDirectory: File;

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
    val bundlesDirectory: File

    /** Local log file */
    val logFile: File by lazy({
        val d = File(this.logDirectory, "${this.appName}.log")
        d.mkdirs()
        d
    })

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
        this.bundlesDirectory = File(this.baseDirectory, "bundles")

        var baseDirectory = this.baseDirectory.exists()
        this.baseDirectory.mkdirs()
        // Set permissions if the directory was created
        if (!baseDirectory) {
            if (SystemUtils.IS_OS_WINDOWS) {
                // Get file attribute view
                var fav = Files.getFileAttributeView(Paths.get(this.baseDirectory.toURI()), AclFileAttributeView::class.java)

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
        this.bundlesDirectory.mkdirs()
    }
}
