package org.deku.leoz.ui

import java.util.*

/**
 * JavaFX application localization
 * Created by masc on 19/08/16.
 * @property baseName Resource package base name
 * @param locale Desired primary locale
 * @param defaultLocale Fallback default locale
 */
class Localization(val baseName: String,
                   locale: java.util.Locale,
                   defaultLocale: java.util.Locale) {

    /** Locale */
    val locale: java.util.Locale
    /** Localized resource bundle */
    val resources: java.util.ResourceBundle

    /**
     * c'tor
     */
    init {
        var finalLocale = locale
        var finalResources: ResourceBundle
        try {
            java.util.Locale.setDefault(finalLocale)
            finalResources = this.getLanguageResourceBundle(finalLocale)
        } catch (e: java.util.MissingResourceException) {
            // Reverting to fallback language (eg. english)
            finalLocale = defaultLocale
            java.util.Locale.setDefault(finalLocale)
            finalResources = this.getLanguageResourceBundle(finalLocale)
        }

        this.resources = finalResources
        this.locale = finalLocale
        java.util.Locale.setDefault(this.locale)
    }

    /**
     * Helper for getting resource bundle
     */
    private fun getLanguageResourceBundle(locale: java.util.Locale): java.util.ResourceBundle {
        return java.util.ResourceBundle.getBundle(this.baseName, locale, sx.util.Utf8ResourceBundleControl())
    }
}