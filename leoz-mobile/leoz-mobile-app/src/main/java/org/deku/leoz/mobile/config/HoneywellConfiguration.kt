package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.*
import rx.Observable
import rx.lang.kotlin.BehaviorSubject
import rx.lang.kotlin.firstOrNull
import rx.lang.kotlin.synchronized
import rx.schedulers.Schedulers
import sx.android.aidc.BarcodeReader
import sx.android.honeywell.aidc.HoneywellBarcodeReader
import java.util.concurrent.TimeUnit

/**
 * Honeywell configuration
 * Created by masc on 12/12/2016.
 */
class HoneywellConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<Observable<BarcodeReader>>() with eagerSingleton {
                HoneywellBarcodeReader.create(context = instance())
            }

            bind<BarcodeReader>() with singleton {
                val ovBarodeReader: Observable<BarcodeReader> = instance()

                ovBarodeReader
                        .take(0, TimeUnit.SECONDS)
                        .toBlocking()
                        .firstOrNull()
                        ?: throw IllegalStateException("BarcodeReader not ready yet. Do not inject this instance without giving main loop opportunity to cycle at least once after importing module.")
            }
        }
    }
}