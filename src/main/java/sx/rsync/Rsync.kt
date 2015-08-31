package sx.rsync

import com.google.common.base.StandardSystemProperty
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.logging.LogFactory
import sx.ProcessExecutor
import sx.platform.PlatformId
import java.io.File
import java.net.URL
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Created by masc on 15.08.15.
 */
public open class Rsync() {
    companion object {
        val log = LogFactory.getLog(Rsync.javaClass)


        /** Path to rsync executable.
         * When not set explicitly, getter tries auto detect by scanning current and parent directories for
         * relative path 'bin/<platformid>' */
        public var executablePath: File? = null
            get() {
                if ($executablePath != null)
                    return $executablePath

                // Search for executable in current and all parent paths
                var binRelPath = Paths.get("bin").resolve(PlatformId.current().toString())

                var path = Paths.get("").toAbsolutePath()
                do {
                    var binPath = path.resolve(binRelPath)
                    try {
                        if (binPath.toFile().exists()) {
                            $executablePath = binPath.resolve("sx-rsync").toFile()
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
     * @param includeDir Indicates if final path component/directory should be included (implies traling slash if false)
     */
    public class URI(uri: java.net.URI, val includeDir: Boolean = false) {
        val uri: java.net.URI

        init {
            // Make sure URI has trailing slash (or not) according to flag
            if (includeDir) {
                this.uri = if (uri.getPath().endsWith('/')) java.net.URI(uri.toString().trimEnd('/')) else uri
            } else {
                this.uri = if (!uri.getPath().endsWith('/')) java.net.URI(uri.toString() + '/') else uri
            }
        }

        /**
         * @param path Local path
         */
        constructor(path: java.nio.file.Path, includeDir: Boolean = false) : this(path.toUri(), includeDir) {
        }

        /**
         * @param uri URI string
         */
        constructor(uri: String, includeDir: Boolean = false) : this(java.net.URI(uri), includeDir) {
        }

        /**
         * @param file Local file
         */
        constructor(file: File, includeDir: Boolean = false) : this(file.toURI(), includeDir) {
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
                    includeDir = this.includeDir)
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
}