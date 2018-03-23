package org.deku.leoz.mobile

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import java.util.*
import android.annotation.TargetApi
import android.os.Build
import com.neovisionaries.i18n.CountryCode

/**
 * Set locale
 */
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
}

/**
 * Set locale
 * @param locale locale
 * @param asDefault Set as default locale
 */
fun Context.setLocale(locale: Locale, asDefault: Boolean = false) {
    this.setLocale(language = locale.language, asDefault = asDefault)
}

/**
 * Set locale based on country code
 * @param countryCode country code
 */
fun Context.setLocale(countryCode: CountryCode) {
    this.setLocale(countryCode.toLocale())
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