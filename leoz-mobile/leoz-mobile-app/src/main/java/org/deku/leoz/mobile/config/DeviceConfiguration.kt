package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.*
import org.deku.leoz.identity.Identity
import org.deku.leoz.identity.MobileIdentityFactory
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

            bind<Identity>() with singleton {
                val device = instance<Device>()
                MobileIdentityFactory(
                        serial = device.serial,
                        imei = device.imei
                ).create()
            }

            bind<Connectivity>() with singleton {
                Connectivity(context = instance())
            }
        }
    }
}