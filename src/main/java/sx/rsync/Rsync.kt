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

/**
 * Created by masc on 15.08.15.
 */
public open class Rsync() {
    companion object {
        val log = LogFactory.getLog(Rsync.javaClass)

        private val executableFilename = "sx-rsync" + if (SystemUtils.IS_OS_WINDOWS) ".exe" else ""

        /** Path to rsync executable.
         * When not set explicitly, getter tries auto detect by scanning current and parent directories for
         * relative path 'bin/<platformid>' */
        public var executablePath: File? = null
            get() {
                if ($executablePath != null)
                    return $executablePath

                // Search for executable in current and all parent paths
                var binRelPath = Paths.get("bin").resolve(PlatformId.current().toString()).resolve(executableFilename)

                var path = Paths.get("").toAbsolutePath()
                do {
                    val binPath = path.resolve(binRelPath)
                    try {
                        if (Files.exists(binPath)) {
                            $executablePath = binPath.toFile()
                            return $executablePath
                        }
                    } catch(e: Exception) {
                        log.warn(e.getMessage(), e)
                    }
                    path = path.getParent()
                } while (path != null)

                throw IllegalStateException("Could not find sx-rsync executable")
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

    public enum class Permission(val permission: String) {
        READONLY("ro"),
        READWRITE("rw"),
        DENY("deny");

        override fun toString(): String {
            return this.permission
        }
    }

    /** Rsync server module, equivalent to a shared folder */
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

    public open class Principal(val name: String)

    /** Rsync user */
    public class User(
            name: String,
            val password: String) : Principal(name) {

        override fun toString(): String {
            return this.name
        }
    }
}