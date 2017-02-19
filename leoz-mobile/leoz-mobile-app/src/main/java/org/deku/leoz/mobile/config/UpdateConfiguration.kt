package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.*
import org.deku.leoz.mobile.*
import org.deku.leoz.mobile.update.UpdateService
import sx.ConfigurationMap
import sx.ConfigurationMapPath

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
    }

    companion object {
        val module = Kodein.Module {
            bind<UpdateService>() with singleton {
                val settings = Settings(instance())

                val service = UpdateService(
                        executorService = instance(),
                        bundleName = settings.bundleName,
                        versionAlias = settings.versionAlias)
                service.force = settings.force
                service.start()
                service
            }
        }
    }
}