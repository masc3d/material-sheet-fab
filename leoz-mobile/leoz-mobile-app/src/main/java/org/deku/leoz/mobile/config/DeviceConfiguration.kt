package org.deku.leoz.mobile.config

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.*
import io.reactivex.schedulers.Schedulers
import org.deku.leoz.identity.Identity
import org.deku.leoz.identity.MobileIdentityFactory
import sx.android.Connectivity
import sx.android.Device
import java.util.concurrent.ExecutorService

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

            // Intiial connectivity state updates may take a while to arrive, thus binding eagerly
            bind<Connectivity>() with eagerSingleton {
                Connectivity(context = instance<Context>())
            }
        }
    }
}