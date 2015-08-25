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
    init {
        Rsync.executablePath = Paths.get("")
                .toAbsolutePath()
                .getParent()
                .getParent()
                .resolve("bin")
                .resolve(when { SystemUtils.IS_OS_WINDOWS -> "win64" else -> "osx64" })
                .resolve("leoz-rsync")
                .toFile()
    }

    public fun initialize() {
    }
}