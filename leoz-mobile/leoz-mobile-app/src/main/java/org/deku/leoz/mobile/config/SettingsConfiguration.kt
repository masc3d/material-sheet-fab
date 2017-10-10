package org.deku.leoz.mobile.config

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.bind
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.erased.singleton
import org.deku.leoz.mobile.BuildConfig
import org.deku.leoz.mobile.DebugSettings
import org.deku.leoz.mobile.LocationSettings
import org.deku.leoz.mobile.RemoteSettings
import sx.ConfigurationMap
import sx.YamlConfigurationMap
import java.io.IOException
import java.io.InputStream

/**
 * Settings configurations
 * Created by masc on 15/02/2017.
 */
class SettingsConfiguration {
    companion object {
        val ASSET_SETTINGS = "application.yml"
        val ASSET_SETTINGS_DEBUG = "application-debug.yml"

        val module = Kodein.Module {
            bind<ConfigurationMap>() with singleton {
                val context: Context = instance()

                val sources = mutableListOf<InputStream>()
                sources.add(context.assets.open(ASSET_SETTINGS))
                if (BuildConfig.DEBUG) {
                    try {
                        sources.add(context.assets.open(ASSET_SETTINGS_DEBUG))
                    } catch (e: IOException) {
                        // Optional asset, that's ok
                    }
                }

                try {
                    YamlConfigurationMap(sources = *sources.toTypedArray())
                } finally {
                    // Close streams
                    sources.forEach {
                        it.close()
                    }
                }
            }

            bind<RemoteSettings>() with singleton {
                RemoteSettings(instance<ConfigurationMap>())
            }

            bind<DebugSettings>() with singleton {
                DebugSettings(instance<ConfigurationMap>())
            }

            bind<LocationSettings>() with singleton {
                LocationSettings(instance<ConfigurationMap>())
            }
        }
    }
}