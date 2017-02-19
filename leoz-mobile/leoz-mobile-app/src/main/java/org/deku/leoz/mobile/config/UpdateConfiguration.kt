package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.*
import org.deku.leoz.mobile.Settings
import org.deku.leoz.mobile.update.UpdateService
import sx.maps.mapValue
import sx.maps.resolve

/**
 * Update service configuration
 * Created by masc on 10/02/2017.
 */
class UpdateConfiguration {
    class Settings(val map: Map<String, Any>) {
        val bundleName: String by mapValue(map, "")
        val versionAlias: String by mapValue(map, "")
        val force: Boolean by mapValue(map, false)
    }

    companion object {
        val module = Kodein.Module {
            bind<UpdateService>() with singleton {
                val rootSettings: org.deku.leoz.mobile.Settings = instance()
                val settings = Settings(rootSettings.map.resolve("update"))

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