package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.*
import com.honeywell.aidc.AidcManager
import com.honeywell.aidc.BarcodeReader
import rx.Observable
import rx.lang.kotlin.BehaviorSubject
import rx.lang.kotlin.synchronized

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
                AidcManager.create(instance(), object: AidcManager.CreatedCallback {
                    override fun onCreated(aidcManager: AidcManager) {
                        // Emit event
                        subject.onNext(aidcManager)
                    }
                })
                subject
            }

            bind<AidcManager>() with singleton {
                val observable: Observable<AidcManager> = instance()
                // Block and retrieve first item
                observable.toBlocking().first()
            }

            bind<BarcodeReader>() with singleton {
                val aidcManager: AidcManager = instance()
                aidcManager.createBarcodeReader()
            }
        }
    }
}