package sx.rsync

import org.apache.commons.lang3.SystemUtils
import org.apache.commons.logging.LogFactory
import sx.EmbeddedExecutable
import sx.ssh.SshTunnel
import java.io.File
import java.nio.file.Paths
import java.util.*

/**
 * Created by masc on 15.08.15.
 */
open class Rsync() {
    companion object {
        val log = LogFactory.getLog(Rsync::class.java)

        val executable = EmbeddedExecutable("sx-rsync")
    }

    /**
     * Rsync URI
     * @property uri File or rsync URI
     * @property asDirectory Indicates if final path component/directory should be included (implies traling slash if true)
     * @property password Rsync password
     * @property sshTunnel The ssh tunnel to use to establish connection to thie rsync URI
     */
    class URI(uri: java.net.URI,
              val asDirectory: Boolean = true,
              val password: String? = null,
              val sshTunnel: SshTunnel? = null) {
        val uri: java.net.URI

        init {
            // Make sure URI has trailing slash (or not) according to flag
            if (!asDirectory) {
                this.uri = if (uri.path.endsWith('/')) java.net.URI(uri.toString().trimEnd('/')) else uri
            } else {
                this.uri = if (!uri.path.endsWith('/')) java.net.URI(uri.toString() + '/') else uri
            }

            if (sshTunnel != null &&
                    !sshTunnel.host.equals(this.uri.host))
                throw IllegalArgumentException("Rsync desitnation host [${this.uri.host}] does not match SSH host [${sshTunnel.host}]")
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
        fun java.net.URI.isFile(): Boolean {
            return this.scheme == "file"
        }

        /**
         * Resolve path
         */
        fun resolve(vararg str: Any): Rsync.URI {
            val path = str.joinToString("/")
            return Rsync.URI(
                    uri = if (uri.path.endsWith('/')) uri.resolve(path) else java.net.URI(uri.toString() + "/" + path),
                    asDirectory = this.asDirectory,
                    sshTunnel = this.sshTunnel)
        }

        /**
         * Indicates if rsync uri is local
         */
        fun isFile(): Boolean {
            return this.uri.isFile()
        }

        override fun toString(): String {
            if (!this.uri.isFile())
                return this.uri.toString()

            var rsyncPath: String
            if (SystemUtils.IS_OS_WINDOWS)
            // Return cygwin path on windows systems
                rsyncPath = "/cygdrive${this.uri.path.replace(":", "")}"
            else
                rsyncPath = Paths.get(this.uri).toAbsolutePath().toString()

            if (this.uri.path.endsWith('/')) {
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
    enum class Permission(val permission: String) {
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
    class Module(
            /** Module name */
            var name: String,
            /** The shared folder this module refers to */
            val path: File) {

        /** Secrets file. */
        var secretsFile: File? = null
        /** Permissions for this module */
        val permissions: HashMap<Principal, Permission> = HashMap()
    }

    /**
     * Rsync principal, base class for users and groups
     */
    open class Principal(val name: String)

    /**
     * Rsync user
     * */
    class User(
            name: String,
            val password: String) : Principal(name) {

        override fun toString(): String {
            return this.name
        }
    }
}