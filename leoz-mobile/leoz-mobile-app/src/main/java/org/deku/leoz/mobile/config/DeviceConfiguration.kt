package org.deku.leoz.mobile.config

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.*
import com.tnt.innight.mobile.Sounds
import org.deku.leoz.identity.Identity
import org.deku.leoz.identity.MobileIdentityFactory
import org.deku.leoz.mobile.Storage
import org.deku.leoz.mobile.device.HardwareTones
import org.deku.leoz.mobile.device.DeviceManagement
import org.deku.leoz.mobile.device.MutedTones
import org.deku.leoz.mobile.device.Tones
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

            bind<DeviceManagement>() with singleton {
                val storage = instance<Storage>()
                val identity = instance<Identity>()

                DeviceManagement(
                        path = storage.deviceMgmtDir,
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

            bind<Sounds>() with eagerSingleton {
                Sounds(context = instance<Context>())
            }
        }
    }
}