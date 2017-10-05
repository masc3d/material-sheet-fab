package org.deku.leoz.mobile.config

import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.eagerSingleton
import com.github.salomonbrys.kodein.instance
import org.deku.leoz.mobile.receiver.LocationProviderChangedReceiver
import org.slf4j.LoggerFactory

/**
 * Created by 27694066 on 05.10.2017.
 */
class BroadcastReceiverConfiguration {
    companion object {
        private val log by lazy { LoggerFactory.getLogger(this.javaClass) }

        var module = Kodein.Module {
            Log.d(BroadcastReceiverConfiguration::class.java.simpleName, "Initialize BroadcastReceiverConfiguration")
            bind<LocationProviderChangedReceiver>() with eagerSingleton {
                val receiver = LocationProviderChangedReceiver()
                val broadcastManager = LocalBroadcastManager.getInstance(instance())
                val intentFilter = IntentFilter(Intent.ACTION_PROVIDER_CHANGED)

                log.debug("Registering BroadcastReceiver [${receiver::class.java.simpleName}] IntentFilter [$intentFilter]")
                broadcastManager.registerReceiver(receiver, intentFilter)
                receiver
            }

            bind<BroadcastReceiverConfiguration>() with eagerSingleton {
                log.debug("Inizialized BroadcastReceiverConfiguration module")
                val config = BroadcastReceiverConfiguration()
                // Init. all receiver here
                instance<LocationProviderChangedReceiver>()
                config
            }
        }
    }
}