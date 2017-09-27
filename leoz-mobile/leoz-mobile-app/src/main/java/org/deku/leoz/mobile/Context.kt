package org.deku.leoz.mobile

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import java.util.*

/**
 * Created by 27694066 on 27.09.2017.
 */
fun Context.setLocale(language: String) {
    val locale = Locale(language)
    Locale.setDefault(locale)

    val resources: Resources = this.resources

    val configuration: Configuration = resources.configuration;
    configuration.setLocale(locale)

    /**
     * TODO Replace deprecated method
     * to be replaced by Context.createConfigurationContext
     */
    resources.updateConfiguration(configuration, resources.displayMetrics)
    //this.createConfigurationContext(configuration)
}