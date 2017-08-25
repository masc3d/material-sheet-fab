package org.deku.leoz.mobile.config

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.*
import com.tnt.innight.mobile.Sounds
import org.deku.leoz.mobile.device.Tones
import org.deku.leoz.mobile.device.MutedTones
import org.deku.leoz.mobile.device.HardwareTones
import sx.android.Device

/**
 * Created by n3 on 06/03/2017.
 */
class SoundConfiguration {
    companion object {
        val module = Kodein.Module {
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