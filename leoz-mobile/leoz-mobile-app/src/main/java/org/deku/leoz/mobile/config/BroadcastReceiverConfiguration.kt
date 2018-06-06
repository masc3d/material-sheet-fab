package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.*
import org.deku.leoz.mobile.receiver.LocationProviderChangedReceiver
import org.slf4j.LoggerFactory
import sx.log.slf4j.debug

/**
 * Broadcast receiver configuration
 * Created by 27694066 on 05.10.2017.
 */
class BroadcastReceiverConfiguration {
    companion object {
        private val log by lazy { LoggerFactory.getLogger(BroadcastReceiverConfiguration::class.java) }

        var module = Kodein.Module {
            log.debug { "Initialize BroadcastReceiverConfiguration" }

            bind<LocationProviderChangedReceiver>() with singleton {
                val receiver = LocationProviderChangedReceiver()
//                val broadcastManager = LocalBroadcastManager.getInstance(instance())
//                val intentFilter = IntentFilter(Intent.ACTION_PROVIDER_CHANGED)
//
//                log.debug("Registering BroadcastReceiver [${receiver::class.java.simpleName}] IntentFilter [$intentFilter]")
//                broadcastManager.registerReceiver(receiver, intentFilter)
                receiver
            }

            bind<BroadcastReceiverConfiguration>() with eagerSingleton {
                log.debug { "Inizialized BroadcastReceiverConfiguration module" }
                val config = BroadcastReceiverConfiguration()
                // Init. all receiver here
                instance<LocationProviderChangedReceiver>()
                config
            }
        }
    }
}