package sx.rsync

import com.google.common.base.StandardSystemProperty
import org.apache.commons.logging.LogFactory
import sx.ProcessExecutor
import sx.platform.PlatformId
import java.io.File
import java.net.URL
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
                } while(path != null)

                throw IllegalStateException("Could not find sx-rsync executable")
            }
    }
}