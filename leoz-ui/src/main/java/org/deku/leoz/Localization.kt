package org.deku.leoz

import sx.util.Utf8ResourceBundleControl
import java.util.*

/**
 * JavaFX application localization
 * Created by masc on 19/08/16.
 * @property baseName Resource package base name
 * @param locale Desired primary locale
 * @param defaultLocale Fallback default locale
 */
class Localization(val baseName: String,
                   locale: Locale,
                   defaultLocale: Locale) {

    /** Locale */
    val locale: Locale
    /** Localized resource bundle */
    val resources: ResourceBundle

    /**
     * c'tor
     */
    init {
        var finalLocale = locale
        try {
            this.resources = this.getLanguageResourceBundle(finalLocale)
        } catch (e: MissingResourceException) {
            // Reverting to fallback language (eg. english)
            finalLocale = defaultLocale
            this.resources = this.getLanguageResourceBundle(finalLocale)
        }

        this.locale = finalLocale
        Locale.setDefault(this.locale)
    }

    /**
     * Helper for getting resource bundle
     */
    private fun getLanguageResourceBundle(locale: Locale): ResourceBundle {
        return ResourceBundle.getBundle(this.baseName, locale, Utf8ResourceBundleControl())
    }
}