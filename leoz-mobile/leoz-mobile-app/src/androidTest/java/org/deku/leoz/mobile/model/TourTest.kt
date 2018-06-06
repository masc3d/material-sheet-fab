package org.deku.leoz.mobile.model

import android.support.test.runner.AndroidJUnit4
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.model.process.Tour
import org.deku.leoz.mobile.model.process.VehicleLoading
import org.deku.leoz.mobile.model.repository.ParcelRepository
import org.deku.leoz.model.DekuUnitNumber
import org.deku.leoz.rest.RestClientFactory
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import sx.android.rx.observeOnMainThread
import sx.log.slf4j.trace
import sx.mq.mqtt.MqttDispatcher

/**
 * Created by masc on 05.05.18.
 */
@RunWith(AndroidJUnit4::class)
class TourTest {
    companion object {
        init {
        }
    }

    private val log = LoggerFactory.getLogger(this.javaClass)

    val tour: Tour by Kodein.global.lazy.instance()
    val vehicleLoading: VehicleLoading by Kodein.global.lazy.instance()
    val parcelRepository: ParcelRepository by Kodein.global.lazy.instance()
    val db: Database by Kodein.global.lazy.instance()

    @Test
    fun testLoadingConcurrency() {
        // TODO: there's still recursion issues
        val mqttDispatcher: MqttDispatcher = Kodein.global.instance()

        val restConfiguration: RestClientFactory by Kodein.global.lazy.instance()
        restConfiguration.apiKey = "3e900c96-4d99-4548-8e92-294d86189659"

        (0..30).map {
            listOf("84057716465")
        }
                .flatten()
                .shuffled()
                .toObservable()
                .subscribeOn(Schedulers.computation())
                .concatMap { parcelNumber ->
                    // No corresponding order (yet)
                    val unitNumber = DekuUnitNumber.parse(parcelNumber).value

                    this.tour.retrieveOrder(unitNumber)
                            .concatMap { order ->
                                this.tour
                                        .mergeOrder(order)
                                        .toSingleDefault(Unit).toObservable()
                                        .concatMap {
                                            this.parcelRepository
                                                    .findByNumber(unitNumber.value)
                                                    .toObservable()
                                        }
                                        .concatMap {
                                            this.vehicleLoading.load(it)
                                                    .toObservable<Unit>()
                                        }
                                        .subscribeOn(db.scheduler)
                            }
                }
                .observeOnMainThread()
                .blockingSubscribe({
                    log.trace { "DONE" }
                }, {
                    log.error("Merging order failed. ${it.message}", it)
                })
    }
}