package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.*
import org.deku.leoz.mobile.service.UpdateService
import sx.ConfigurationMap
import sx.ConfigurationMapPath
import sx.time.seconds

/**
 * Update service configuration
 * Created by masc on 10/02/2017.
 */
class UpdateConfiguration {
    @ConfigurationMapPath("update")
    class Settings(map: ConfigurationMap) {
        val bundleName: String by map.value("")
        val versionAlias: String by map.value("")
        val force: Boolean by map.value(false)
        val period: Int by map.value(3600)
    }

    companion object {
        val module = Kodein.Module {
            bind<UpdateService>() with singleton {
                val settings = Settings(instance())

                val service = UpdateService(
                        executorService = instance(),
                        bundleName = settings.bundleName,
                        versionAlias = settings.versionAlias,
                        period = settings.period.seconds)
                service.force = settings.force
                service.start()
                service
            }
        }
    }
}