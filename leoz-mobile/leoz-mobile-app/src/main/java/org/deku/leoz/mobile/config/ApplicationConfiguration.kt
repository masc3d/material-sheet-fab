package org.deku.leoz.mobile.config

import android.app.Application
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.bind
import com.github.salomonbrys.kodein.erased.eagerSingleton
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.erased.singleton
import sx.android.ApplicationStateMonitor

/**
 * Application configuration
 * Created by masc on 12/12/2016.
 */
class ApplicationConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<ApplicationStateMonitor>() with eagerSingleton {
                ApplicationStateMonitor(instance<Application>())
            }
        }
    }
}