package sx.rsync

import org.apache.commons.lang3.SystemUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import java.io.File
import java.nio.file.Paths

/**
 * Created by masc on 15.08.15.
 */
abstract class RsyncTest {
    protected val log: Log = LogFactory.getLog(this.javaClass)

    init {
        Rsync.executablePath =
                Paths.get("")
                .toAbsolutePath()
                .getParent()
                .getParent()
                .resolve("bin")
                .resolve(when { SystemUtils.IS_OS_WINDOWS -> "win64" else -> "osx64" })
                .resolve("leoz-rsync")
                        .toFile()

        log.info("Rsync executable path [${Rsync.executablePath}]")
    }
}