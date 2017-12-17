package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.bind
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.erased.singleton
import org.deku.leoz.mobile.service.LocationServices

/**
 * Created by 27694066 on 05.10.2017.
 */
class LocationServicesConfiguration {
    companion object {
        var module = Kodein.Module {
            bind<LocationServices>() with singleton {
                LocationServices(context = instance())
            }
        }
    }
}