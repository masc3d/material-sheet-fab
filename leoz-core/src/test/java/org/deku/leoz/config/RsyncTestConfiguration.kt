package org.deku.leoz.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.erased.eagerSingleton
import org.apache.commons.lang3.SystemUtils
import sx.rsync.Rsync
import java.nio.file.Paths

/**
 * Leoz rsync configuration
 * Created by masc on 24.08.15.
 */
class RsyncTestConfiguration : RsyncConfiguration() {
    companion object {
        val module = Kodein.Module {
            /** Eager/static initialization */
            bind<RsyncConfiguration>() with eagerSingleton {
                RsyncTestConfiguration()
            }
        }
    }
}