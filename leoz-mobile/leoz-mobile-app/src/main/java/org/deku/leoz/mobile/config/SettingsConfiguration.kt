package org.deku.leoz.mobile.config

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.singleton
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import org.deku.leoz.mobile.BuildConfig
import sx.ConfigurationMap
import sx.YamlConfigurationMap
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

/**
 * Created by n3 on 15/02/2017.
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
                        sources.add(context.assets.open(ASSET_SETTINGS_DEBUG + ".test"))
                    } catch(e: IOException) {
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
        }
    }
}