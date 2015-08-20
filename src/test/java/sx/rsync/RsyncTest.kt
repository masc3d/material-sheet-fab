package sx.rsync

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import java.io.File
import java.nio.file.Paths

/**
 * Created by masc on 15.08.15.
 */
abstract class RsyncTest {
    protected val log: Log = LogFactory.getLog(this.javaClass)
    var rsyncExecutablePath: File

    init {
        val rsyncExecutablePath = Paths.get("")
                .toAbsolutePath()
                .getParent()
                .getParent()
                .resolve("bin")
                .resolve("osx64")
                .resolve("leoz-rsync")

        this.rsyncExecutablePath = rsyncExecutablePath.toFile()

        log.info("Rsync executable path [${this.rsyncExecutablePath}]")
    }
}