package org.deku.leoz.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.singleton
import org.deku.leoz.Localization
import org.deku.leoz.Settings
import java.util.*

/**
 * Created by masc on 18/08/16.
 */
object Configurations {
    val application = Kodein.Module {
        /** Application side settings */
        bind<Settings>() with singleton { Settings() }
        /** Localization & internationalization */
        bind<Localization>() with singleton { Localization(
                baseName = "i18n.leoz",
                locale = Locale.GERMAN,
                defaultLocale = Locale.ENGLISH)}
    }

    val messenging = Kodein.Module {
    }
}
