package org.deku.leoz.mobile.config

import android.hardware.Camera
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.*
import rx.Observable
import rx.lang.kotlin.BehaviorSubject
import rx.lang.kotlin.firstOrNull
import rx.lang.kotlin.synchronized
import rx.lang.kotlin.toSingletonObservable
import rx.schedulers.Schedulers
import sx.android.Device
import sx.android.aidc.AidcReader
import sx.android.aidc.CameraAidcReader
import sx.android.honeywell.aidc.HoneywellAidcReader
import sx.rx.toHotReplay
import java.util.concurrent.TimeUnit

/**
 * Honeywell configuration
 * Created by masc on 12/12/2016.
 */
class AidcConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<Observable<out AidcReader>>() with eagerSingleton {
                val device: Device = instance()
                when (device.manufacturer.type) {
                    Device.Manufacturer.Type.Honeywell ->
                        HoneywellAidcReader.create(context = instance())
                    else ->
                        CameraAidcReader(context = instance())
                                .toSingletonObservable()
                                .toHotReplay()
                }
            }

            bind<AidcReader>() with erasedSingleton {
                val ovAidcReader: Observable<out AidcReader> = instance()

                ovAidcReader
                        .take(0, TimeUnit.SECONDS)
                        .toBlocking()
                        .firstOrNull()
                        ?: throw IllegalStateException("AidcReader not ready yet. Do not inject this instance without giving main loop opportunity to cycle at least once after importing module.")
            }

            bind<CameraAidcReader>() with erasedSingleton {
                CameraAidcReader(context = erasedInstance())
            }
        }
    }
}