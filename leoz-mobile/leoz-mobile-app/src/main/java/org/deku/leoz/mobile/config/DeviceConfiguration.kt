package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.*
import sx.android.Connectivity
import sx.android.Device

/**
 * Created by n3 on 26/02/2017.
 */
class DeviceConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<Device>() with singleton {
                Device(context = instance())
            }

            bind<Connectivity>() with singleton {
                Connectivity(context = instance())
            }
        }
    }
}