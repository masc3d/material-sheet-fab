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
import sx.android.aidc.CompositeAidcReader
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
            bind<CameraAidcReader>() with erasedSingleton {
                CameraAidcReader(context = erasedInstance())
            }

            bind<Observable<out AidcReader>>() with eagerSingleton {
                val device: Device = instance()

                val ovCameraReader: Observable<out AidcReader> = instance<CameraAidcReader>()
                        .toSingletonObservable()
                        .toHotReplay()

                when (device.manufacturer.type) {
                    Device.Manufacturer.Type.Honeywell ->
                        HoneywellAidcReader
                                .create(context = instance())
                                .mergeWith(ovCameraReader)
                    else ->
                        ovCameraReader
                }
            }

            bind<AidcReader>() with erasedSingleton {
                val ovAidcReader: Observable<out AidcReader> = instance()

                val aidcReaders = ovAidcReader
                        .toBlocking()
                        .toIterable()
                        .toList()

                CompositeAidcReader(readers = *aidcReaders.toTypedArray())
            }
        }
    }
}