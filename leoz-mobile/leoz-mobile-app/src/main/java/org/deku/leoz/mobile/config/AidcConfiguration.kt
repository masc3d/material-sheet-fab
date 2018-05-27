package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.bind
import com.github.salomonbrys.kodein.erased.eagerSingleton
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.erased.singleton
import io.reactivex.Observable
import sx.android.aidc.AidcReader
import sx.android.aidc.CameraAidcReader
import sx.android.aidc.CompositeAidcReader
import sx.android.aidc.SimulatingAidcReader
import sx.android.hardware.Device
import sx.android.honeywell.aidc.HoneywellAidcReader
import sx.rx.toHotReplay

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
                        readers.add(
                                HoneywellAidcReader.create(context = instance())
                        )

                    else -> Unit
                }

                Observable.merge(readers)
                        .toHotReplay()
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

                // Honeywell specific configuration
                aidcReaders.firstOrNull { it is HoneywellAidcReader }?.also {
                    // Bind exclusively to avoid honeywell issue where claiming the reader mail fail sporadically
                    it.bindExclusively = true
                }

                CompositeAidcReader(readers = *aidcReaders.toTypedArray())
            }
        }
    }
}