package org.deku.leoz

import org.apache.commons.lang3.SystemUtils
import sx.rsync.Rsync
import sx.rsync.RsyncClient
import java.nio.file.Paths

/**
 * Leoz rsync configuration
 * Created by masc on 24.08.15.
 */
public object RsyncConfiguration {
    public fun initialize() {
        Rsync.executableFile = Paths.get("")
                .toAbsolutePath()
                .parent
                .parent
                .resolve("libs")
                .resolve("sx-common")
                .resolve("bin")
                .resolve(when { SystemUtils.IS_OS_WINDOWS -> "win64" else -> "osx64" })
                .resolve("sx-rsync")
                .toFile()
    }
}