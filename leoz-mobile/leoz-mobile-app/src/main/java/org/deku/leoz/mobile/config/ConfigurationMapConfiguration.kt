package org.deku.leoz.mobile.config

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.singleton
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import sx.ConfigurationMap
import sx.YamlConfigurationMap

/**
 * Created by n3 on 15/02/2017.
 */
class ConfigurationMapConfiguration {
    companion object {
        val ASSET_SETTINGS = "application.yml"
        val ASSET_SETTINGS_DEBUG = "application-debug.yml"

        val module = Kodein.Module {
            bind<ConfigurationMap>() with singleton {
                val context: Context = instance()

                YamlConfigurationMap(
                        primarySource = context.assets.open(ASSET_SETTINGS),
                        overrides = context.assets.open(ASSET_SETTINGS_DEBUG))
            }
        }
    }
}