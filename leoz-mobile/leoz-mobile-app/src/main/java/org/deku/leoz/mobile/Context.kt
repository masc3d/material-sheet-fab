package org.deku.leoz.mobile

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import java.util.*
import android.annotation.TargetApi
import android.os.Build

/**
 * Created by phpr on 27.09.2017.
 */
class BaseContextWrapper private constructor(base: Context): ContextWrapper(base) {

    companion object {
        fun wrap(context: Context, language: String? = null): ContextWrapper {
            var context = context
            val config = context.resources.configuration
            var sysLocale: Locale? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                sysLocale = getSystemLocale(config)
            } else {
                sysLocale = getSystemLocaleLegacy(config)
            }
            if (language != null && !sysLocale!!.language.equals(language)) {
                val locale = Locale(language)
                Locale.setDefault(locale)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    setSystemLocale(config, locale)
                } else {
                    setSystemLocaleLegacy(config, locale)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    context = context.createConfigurationContext(config)
                } else {
                    context.resources.updateConfiguration(config, context.resources.displayMetrics)
                }
            }
            return BaseContextWrapper(context)
        }

        fun getSystemLocaleLegacy(config: Configuration): Locale
                = config.locale

        @TargetApi(Build.VERSION_CODES.N)
        fun getSystemLocale(config: Configuration)
                = config.locales.get(0)

        fun setSystemLocaleLegacy(config: Configuration, locale: Locale) {
            config.locale = locale
        }

        @TargetApi(Build.VERSION_CODES.N)
        fun setSystemLocale(config: Configuration, locale: Locale) {
            config.setLocale(locale)
        }
    }
}

fun Context.setLocale(language: String, asDefault: Boolean = false) {
    val locale = Locale(language)

    if (asDefault)
        Locale.setDefault(locale)

    val resources: Resources = this.resources

    val configuration: Configuration = resources.configuration
    configuration.locale = locale

    /**
     * TODO Replace deprecated method
     * to be replaced by Context.createConfigurationContext
     */
    resources.updateConfiguration(configuration, resources.displayMetrics)
    //this.createConfigurationContext(configuration)
}

fun Context.setLocale(locale: Locale, asDefault: Boolean = false) {
    this.setLocale(language = locale.language, asDefault = asDefault)
}

/**
 * Resets the locale to the default! locale. If the default locale was overwritten before with Context.setLocale(..., true),
 * the locale is set to the new one. TODO: Implement function to return to system locale even if default locale was overwritten
 */
fun Context.resetLocale() {
    this.setLocale(this.getDefaultLocale())
}

fun Context.getDefaultLocale(): Locale =
        Locale.getDefault()

// TODO this seems not to be the systemLocale
fun Context.getSystemLocale(): Locale {
    val config = this.resources.configuration
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        config.locales[0]
    } else {
        Locale.getDefault()
    }
}