package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.*
import org.deku.leoz.mobile.device.Tone

/**
 * Created by n3 on 06/03/2017.
 */
class ToneConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<Tone>() with singleton {
                Tone()
            }
        }
    }
}