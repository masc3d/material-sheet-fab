package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import org.deku.leoz.mobile.Notifications

/**
 * Created by prangenberg on 08.11.17.
 */
class NotificationConfiguration {
    companion object {
        var module = Kodein.Module {
            bind<Notifications>() with singleton {
                Notifications(context = instance())
            }
        }
    }
}