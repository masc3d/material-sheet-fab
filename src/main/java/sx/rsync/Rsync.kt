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
         * Ensures the executable and libraries within the executable file's path have the executable bit set
         */
        private fun makeExecutable(executable: File) {
            Files.walk(Paths.get(executable.toURI()).getParent(), 1)
                    .filter { p -> Files.isRegularFile(p) && (p.endsWith(".exe") || p.endsWith(".dll")) }
                    .forEach { p ->
                        log.debug("Setting executable bit for [${p}]")
                        p.toFile().setExecutable(true)
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

                    this.makeExecutable($executableFile!!)
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
        public fun resolve(str: String): Rsync.URI {
            return Rsync.URI(
                    uri = if (uri.getPath().endsWith('/')) uri.resolve(str) else java.net.URI(uri.toString() + "/" + str),
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

            if (this.uri.getPath().endsWith('/'))
                rsyncPath += FileSystems.getDefault().getSeparator()

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