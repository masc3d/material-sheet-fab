package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import org.deku.leoz.mobile.settings.RemoteSettings
import sx.android.hardware.Device
import sx.android.net.NtpTime

/**
 * Created by prangenberg on 04.11.17.
 */
class TimeConfiguration {
    companion object {
        private val remoteSettings: RemoteSettings by Kodein.global.lazy.instance()
        private val device: Device by Kodein.global.lazy.instance()

        var module = Kodein.Module {
            bind<NtpTime>() with eagerSingleton {
                NtpTime(
                        context = instance(),
                        ntpHost = if (device.isM2MConnected) remoteSettings.ntp.hostInternal else remoteSettings.host,
                        maxRetryCount = 100,
                        trueTimeInternalLoggingEnabled = false
                )
            }
        }
    }
}