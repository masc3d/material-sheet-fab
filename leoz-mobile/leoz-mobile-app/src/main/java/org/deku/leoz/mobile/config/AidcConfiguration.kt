package org.deku.leoz.mobile.config

import android.hardware.Camera
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import sx.android.Device
import sx.android.aidc.AidcReader
import sx.android.aidc.CameraAidcReader
import sx.android.aidc.CompositeAidcReader
import sx.android.aidc.SimulatingAidcReader
import sx.android.honeywell.aidc.HoneywellAidcReader
import sx.rx.toHotReplay
import sx.rx.toSingletonObservable
import java.util.concurrent.TimeUnit

/**
 * Honeywell configuration
 * Created by masc on 12/12/2016.
 */
class AidcConfiguration {
    companion object {
        val module = Kodein.Module {
            /**
             * Camera aidc reader
             */
            bind<CameraAidcReader>() with singleton {
                CameraAidcReader(context = instance())
            }

            /**
             * A simulating aidc reader for synthesizing reads
             */
            bind<SimulatingAidcReader>() with singleton {
                SimulatingAidcReader()
            }

            /**
             * Observable providing all available aidc readers
             */
            bind<Observable<out AidcReader>>() with eagerSingleton {
                val device: Device = instance()

                val readers = mutableListOf(
                        Observable.just(instance<CameraAidcReader>()),
                        Observable.just(instance<SimulatingAidcReader>())
                )

                when (device.manufacturer.type) {
                    Device.Manufacturer.Type.Honeywell ->
                        readers.add(HoneywellAidcReader.create(context = instance()))

                    else -> Unit
                }

                Observable.merge(readers).toHotReplay()
            }

            /**
             * Global aidc reader. This is usually a ${link CompositeAidcReader}
             */
            bind<AidcReader>() with singleton {
                val ovAidcReader: Observable<out AidcReader> = instance()

                // Wait for all aidc readers to become available
                val aidcReaders = ovAidcReader
                        .blockingIterable()
                        .toList()

                CompositeAidcReader(readers = *aidcReaders.toTypedArray())
            }
        }
    }
}