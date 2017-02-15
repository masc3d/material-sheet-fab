package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.*
import com.honeywell.aidc.AidcManager
import com.honeywell.aidc.BarcodeReader
import rx.Observable
import rx.lang.kotlin.BehaviorSubject
import rx.lang.kotlin.synchronized
import rx.schedulers.Schedulers

/**
 * Honeywell configuration
 * Created by masc on 12/12/2016.
 */
class HoneywellConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<Observable<AidcManager>>() with eagerSingleton {
                // RX subject which will emit AdicManager once it's been created by underlying service
                val subject = BehaviorSubject<AidcManager>().synchronized()
                AidcManager.create(instance(), {
                    // Emit AidcManager instance
                    subject.onNext(it)
                })
                subject
            }

            bind<AidcManager>() with singleton {
                val observable: Observable<AidcManager> = instance()

                observable
                        // Observe on worker thread to avoid potential deadlock as AidcManager.create callback executes on main/UI thread
                        //.observeOn(Schedulers.from(instance()))
                        // Block and retrieve first item
                        .toBlocking()
                        .first()
            }

            bind<BarcodeReader>() with singleton {
                val aidcManager: AidcManager = instance()
                aidcManager.createBarcodeReader()
            }
        }
    }
}