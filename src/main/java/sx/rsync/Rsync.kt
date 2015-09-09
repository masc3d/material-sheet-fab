package sx.rsync

import com.google.common.base.StandardSystemProperty
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.logging.LogFactory
import sx.ProcessExecutor
import sx.platform.PlatformId
import java.io.File
import java.net.URL
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.*
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by masc on 15.08.15.
 */
public open class Rsync() {
    companion object {
        val log = LogFactory.getLog(Rsync.javaClass)

        /** Rsync executable base filename */
        public var executableBaseFilename: String = "sx-rsync"
        /** Rsync executable file name */
        public val executableFilename: String by Delegates.lazy {
            this.executableBaseFilename + if (SystemUtils.IS_OS_WINDOWS) ".exe" else ""
        }

        /**
         * Find rsync executabe
         */
        private fun findExecutable(): File? {
            // Search for executable in current and parent paths
            var binPlatformRelPath = Paths.get("bin")
                    .resolve(PlatformId.current().toString())
                    .resolve(executableFilename)

            var binRelPath = Paths.get("bin")
                    .resolve(executableFilename)

            var path = Paths.get("").toAbsolutePath()
            do {
                var binPath: Path

                binPath = path.resolve(binPlatformRelPath)
                if (Files.exists(binPath))
                    return binPath.toFile()

                binPath = path.resolve(binRelPath)
                if (Files.exists(binPath))
                    return binPath.toFile()

                path = path.getParent()
            } while (path != null)

            return null
        }

        /**
         * Ensures all binaries within path have the executable bit/permission set
         * @param path Path
         */
        private fun setExecutablePermissions(path: File) {
            Files.walk(Paths.get(path.toURI()), 1)
                    .filter { p ->
                        val filename = p.toString().toLowerCase()
                        Files.isRegularFile(p) && (!filename.contains('.') || filename.endsWith(".exe") || filename.endsWith(".dll")) }
                    .forEach { p ->
                        // Get file attribute view
                        var fav = Files.getFileAttributeView(p, javaClass<AclFileAttributeView>())

                        if (fav != null) {
                            log.debug("Verifying executable permission for [${p}]")

                            var oldAcls = fav.getAcl()
                            var newAcls = ArrayList<AclEntry>()
                            var update = false
                            for (acl in oldAcls) {
                                // Add executable permission if it's not there yet
                                var perms = acl.permissions()
                                if (!perms.contains(AclEntryPermission.EXECUTE)) {
                                    perms.add(AclEntryPermission.EXECUTE)
                                    // Build new acl from old one with updated permissions
                                    var aclb = AclEntry.newBuilder(acl)
                                    aclb.setPermissions(perms)
                                    newAcls.add(aclb.build())
                                    update = true
                                } else {
                                    newAcls.add(acl)
                                }
                            }
                            if (update) {
                                log.debug("Adding permission to execute to [${p}]")
                                fav.setAcl(newAcls)
                            }
                        }
                    }
        }

        /**
         * Path to rsync executable.
         * When not set explicitly, tries to detect/find executable automatically within current and parent paths
         * */
        public var executableFile: File? = null
            @synchronized get() {
                if ($executableFile == null) {
                    log.debug("Searching for rsync executable [${this.executableFilename}]")
                    $executableFile = this.findExecutable()
                    if ($executableFile == null)
                        throw IllegalStateException("Could not find rsync executable [${this.executableFilename}]")

                    log.debug("Found rsync executable [${$executableFile}]")

                    this.setExecutablePermissions($executableFile!!.getParentFile())
                }

                return $executableFile
            }
    }

    /**
     * Rsync URI
     * @param uri File or rsync URI
     * @param asDirectory Indicates if final path component/directory should be included (implies traling slash if true)
     */
    public class URI(uri: java.net.URI, val asDirectory: Boolean = true) {
        val uri: java.net.URI

        init {
            // Make sure URI has trailing slash (or not) according to flag
            if (!asDirectory) {
                this.uri = if (uri.getPath().endsWith('/')) java.net.URI(uri.toString().trimEnd('/')) else uri
            } else {
                this.uri = if (!uri.getPath().endsWith('/')) java.net.URI(uri.toString() + '/') else uri
            }
        }

        /**
         * @param path Local path
         */
        constructor(path: java.nio.file.Path, asDirectory: Boolean = true) : this(path.toUri(), asDirectory) {
        }

        /**
         * @param uri URI string
         */
        constructor(uri: String, asDirectory: Boolean = true) : this(java.net.URI(uri), asDirectory) {
        }

        /**
         * @param file Local file
         */
        constructor(file: File, asDirectory: Boolean = true) : this(file.toURI(), asDirectory) {
        }

        // Extension methods for java.net.URI
        private fun java.net.URI.isFile(): Boolean {
            return this.getScheme() == "file"
        }

        /**
         * Resolve path
         */
        public fun resolve(vararg str: Any): Rsync.URI {
            val path = str.joinToString("/")
            return Rsync.URI(
                    uri = if (uri.getPath().endsWith('/')) uri.resolve(path) else java.net.URI(uri.toString() + "/" + path),
                    asDirectory = this.asDirectory)
        }

        override fun toString(): String {
            if (!this.uri.isFile())
                return this.uri.toString()

            var rsyncPath: String
            if (SystemUtils.IS_OS_WINDOWS)
            // Return cygwin path on windows systems
                rsyncPath = "/cygdrive${this.uri.getPath().replace(":", "")}"
            else
                rsyncPath = Paths.get(this.uri).toAbsolutePath().toString()

            if (this.uri.getPath().endsWith('/')) {
                if (!rsyncPath.endsWith('/'))
                    rsyncPath += '/'
            } else
                rsyncPath = rsyncPath.trimEnd('/')

            return rsyncPath
        }
    }

    /**
     * Rsync server module permission
     */
    public enum class Permission(val permission: String) {
        READONLY("ro"),
        READWRITE("rw"),
        DENY("deny");

        override fun toString(): String {
            return this.permission
        }
    }

    /**
     * Rsync server module, equivalent to a shared folder
     * */
    public class Module(
            /** Module name */
            var name: String,
            /** The shared folder this module refers to */
            val path: File) {

        /** Secrets file. */
        public var secretsFile: File? = null
        /** Permissions for this module */
        public val permissions: HashMap<Principal, Permission> = HashMap()
    }

    /**
     * Rsync principal, base class for users and groups
     */
    public open class Principal(val name: String)

    /**
     * Rsync user
     * */
    public class User(
            name: String,
            val password: String) : Principal(name) {

        override fun toString(): String {
            return this.name
        }
    }
}