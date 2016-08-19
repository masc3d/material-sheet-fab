package org.deku.leoz.ui.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.singleton
import org.deku.leoz.ui.Localization
import org.deku.leoz.ui.Settings
import java.util.*

/**
 * Created by masc on 18/08/16.
 */
object Configurations {
    val application = Kodein.Module {
        /** Application side settings */
        bind<Settings>() with singleton { Settings() }
        /** Localization & internationalization */
        bind<org.deku.leoz.ui.Localization>() with singleton {
            org.deku.leoz.ui.Localization(
                    baseName = "i18n.leoz",
                    locale = Locale.GERMAN,
                    defaultLocale = Locale.ENGLISH)
        }
    }

    val messenging = Kodein.Module {
    }
}
