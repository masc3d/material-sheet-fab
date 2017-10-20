package org.deku.leoz.mobile.config

import android.content.Context
import android.content.res.Configuration
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.bind
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.erased.singleton
import org.deku.leoz.mobile.*
import org.slf4j.LoggerFactory
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

        private val log = LoggerFactory.getLogger(SettingsConfiguration::class.java)

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
                DebugSettings(instance<ConfigurationMap>()).also {
                    log.info(it.toString())
                }
            }

            bind<LocationSettings>() with singleton {
                LocationSettings(instance<ConfigurationMap>())
            }

            bind<UserSettings>() with singleton {
                UserSettings(instance<ConfigurationMap>())
            }
        }
    }
}