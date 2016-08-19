package org.deku.leoz

import sx.util.Utf8ResourceBundleControl
import java.util.*

/**
 * Created by masc on 19/08/16.
 */
class Localization(val baseName: String,
                   locale: Locale,
                   defaultLocale: Locale) {
    val locale: Locale
    val resourceBundle: ResourceBundle

    /**
     * c'tor
     */
    init {
        var finalLocale = locale
        try {
            this.resourceBundle = this.getLanguageResourceBundle(finalLocale)
        } catch (e: MissingResourceException) {
            // Reverting to fallback language (eg. english)
            finalLocale = defaultLocale
            this.resourceBundle = this.getLanguageResourceBundle(finalLocale)
        }

        this.locale = finalLocale
        Locale.setDefault(this.locale)
    }

    private fun getLanguageResourceBundle(locale: Locale): ResourceBundle {
        return ResourceBundle.getBundle(this.baseName, locale, Utf8ResourceBundleControl())
    }
}