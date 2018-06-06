package org.deku.leoz.mobile.config

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.bind
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.erased.singleton
import org.deku.leoz.mobile.BuildConfig
import org.deku.leoz.mobile.settings.*
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import sx.ConfigurationMap
import java.io.IOException

/**
 * Settings configurations
 * Created by masc on 15/02/2017.
 */
class SettingsConfiguration(
        /** Map of core settings */
        val settingsMap: Map<String, Any>,
        /** Map of debug specific overrides */
        val debugSettingsMap: Map<String, Any>
) {
    companion object {
        val ASSET_SETTINGS = "application.yml"
        val ASSET_SETTINGS_DEBUG = "application-debug.yml"

        /** Enforce debug settings also for release builds (STRICTLY for testing) */
        val ENFORCE_DEBUG = false

        private val log = LoggerFactory.getLogger(SettingsConfiguration::class.java)

        val module = Kodein.Module {
            bind<SettingsConfiguration>() with singleton {
                val context: Context = instance()
                val yaml = Yaml()

                // Core settings map
                val settingsMap = context.assets.open(ASSET_SETTINGS).let {
                    yaml.loadAs(it, Map::class.java) as Map<String, Any>
                }

                // Debug settings map
                val debugSettingsMap = if (BuildConfig.DEBUG || ENFORCE_DEBUG) {
                    try {
                        context.assets.open(ASSET_SETTINGS_DEBUG).let {
                            yaml.load(it) as Map<String, Any>
                        }
                    } catch (e: IOException) {
                        // Optional asset, that's ok
                        mapOf<String, Any>()
                    }
                } else mapOf()

                SettingsConfiguration(
                        settingsMap = settingsMap,
                        debugSettingsMap = debugSettingsMap
                )
            }

            bind<ConfigurationMap>() with singleton {
                val settingsConfiguration = instance<SettingsConfiguration>()

                ConfigurationMap().also {
                    it.set(listOf(
                            settingsConfiguration.settingsMap,
                            settingsConfiguration.debugSettingsMap
                    ))
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

            bind<TourSettings>() with singleton {
                TourSettings(instance<ConfigurationMap>())
            }
        }
    }
}