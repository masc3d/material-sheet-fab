package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.*
import rx.Observable
import rx.lang.kotlin.BehaviorSubject
import rx.lang.kotlin.firstOrNull
import rx.lang.kotlin.synchronized
import rx.lang.kotlin.toSingletonObservable
import rx.schedulers.Schedulers
import sx.android.Device
import sx.android.aidc.BarcodeReader
import sx.android.aidc.CameraBarcodeReader
import sx.android.honeywell.aidc.HoneywellBarcodeReader
import java.util.concurrent.TimeUnit

/**
 * Honeywell configuration
 * Created by masc on 12/12/2016.
 */
class AidcConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<Observable<out BarcodeReader>>() with eagerSingleton {
                val device: Device = instance()
                when (device.manufacturer.type) {
                    Device.Manufacturer.Type.Honeywell ->
                        HoneywellBarcodeReader.create(context = instance())
                    else ->
                        CameraBarcodeReader(context = instance())
                                .toSingletonObservable()
                }
            }

            bind<BarcodeReader>() with singleton {
                val ovBarodeReader: Observable<out BarcodeReader> = instance()

                ovBarodeReader
                        .take(0, TimeUnit.SECONDS)
                        .toBlocking()
                        .firstOrNull()
                        ?: throw IllegalStateException("BarcodeReader not ready yet. Do not inject this instance without giving main loop opportunity to cycle at least once after importing module.")
            }
        }
    }
}