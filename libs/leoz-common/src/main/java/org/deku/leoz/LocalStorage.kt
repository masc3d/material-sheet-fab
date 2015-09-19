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

/**
 * Local Storage
 * Created by masc on 26.06.15.
 */
open class LocalStorage {
    private var log: Log = LogFactory.getLog(this.javaClass)

    // Directories
    /** Local home directory */
    val homeDirectory: File;
    /** Local data directory */
    val dataDirectory: File
    /** Etc/settings directory */
    val etcDirectory: File
    /** Local log directory */
    val logDirectory: File
    /** Local bundles directory */
    val bundlesDirectory: File

    /** Local log file */
    val logFile: File

    companion object Singleton {
        private val instance: LocalStorage = LocalStorage()
        @JvmStatic fun instance(): LocalStorage {
            return this.instance;
        }
    }

    /** c'tor */
    init {
        // Initialize directories
        var basePath: String
        var baseDirectory: String
        if (SystemUtils.IS_OS_WINDOWS) {
            basePath = System.getenv("ALLUSERSPROFILE")
            baseDirectory = "Leoz"
        } else {
            basePath = System.getProperty("user.home")
            baseDirectory = ".leoz"
        }
        if (Strings.isNullOrEmpty(basePath))
            throw UnsupportedOperationException("Basepath is empty");

        this.homeDirectory = File(basePath, baseDirectory)
        this.log.info("Home directory [${homeDirectory}]")
        this.dataDirectory = File(this.homeDirectory, "data")
        this.logDirectory = File(this.homeDirectory, "log");
        this.etcDirectory = File(this.homeDirectory, "etc")
        this.bundlesDirectory = File(this.homeDirectory, "bundles")

        this.logFile = File(this.logDirectory, "leoz.log")

        var homeExists = this.homeDirectory.exists()
        this.homeDirectory.mkdirs()
        // Set permissions if the directory was created
        if (!homeExists) {
            if (SystemUtils.IS_OS_WINDOWS) {
                // Get file attribute view
                var fav = Files.getFileAttributeView(Paths.get(this.homeDirectory.toURI()), AclFileAttributeView::class.java)

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
        this.dataDirectory.mkdirs()
        this.logDirectory.mkdirs()
        this.etcDirectory.mkdirs()
        this.bundlesDirectory.mkdirs()
    }
}
