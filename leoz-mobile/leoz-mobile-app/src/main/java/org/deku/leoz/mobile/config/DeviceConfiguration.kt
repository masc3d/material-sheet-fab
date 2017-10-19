package org.deku.leoz.mobile.config

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.bind
import com.github.salomonbrys.kodein.erased.eagerSingleton
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.erased.singleton
import org.deku.leoz.identity.Identity
import org.deku.leoz.identity.MobileIdentityFactory
import org.deku.leoz.mobile.Storage
import org.deku.leoz.mobile.device.*
import sx.android.Connectivity
import sx.android.Device

/**
 * Device related configuration
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

            bind<DeviceManagement>() with singleton {
                val storage = instance<Storage>()
                val identity = instance<Identity>()

                DeviceManagement(
                        path = storage.deviceManagementDir,
                        identity = identity
                )
            }

            // Intiial connectivity state updates may take a while to arrive, thus binding eagerly
            bind<Connectivity>() with eagerSingleton {
                Connectivity(context = instance<Context>())
            }

            bind<Tones>() with singleton {
                val device: Device = instance()

                when (device.isEmulator) {
                    false -> HardwareTones()
                    true -> MutedTones()
                }
            }

            bind<Response>() with singleton {
                Response()
            }

            bind<Sounds>() with eagerSingleton {
                Sounds(context = instance<Context>())
            }

            bind<Vibrator>() with singleton {
                Vibrator(context = instance())
            }
        }
    }
}