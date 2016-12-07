package org.deku.leoz.android.rest

import android.support.test.runner.AndroidJUnit4
import org.junit.Test
import feign.Feign
import feign.jackson.JacksonEncoder
import feign.jackson.JacksonDecoder
import feign.jaxrs.JAXRSContract
import org.deku.leoz.rest.service.internal.v1.StationService
import org.junit.runner.RunWith
import android.support.test.InstrumentationRegistry
import org.deku.leoz.rest.entity.internal.v1.Station
import rx.Observable
import rx.lang.kotlin.subscribeWith
import sx.rx.task

/**
 * Created by n3 on 06/12/2016.
 */
@RunWith(AndroidJUnit4::class)
class StationServiceTest {
    @Test
    fun testGet() {
        val stationService = Feign.builder()
                .encoder(JacksonEncoder())
                .decoder(JacksonDecoder())
                .contract(JAXRSContract())
                .target(StationService::class.java, "http://10.0.2.2:13000/rs/api")

        Observable.fromCallable { stationService.get() }
                .subscribeWith {
                    onNext {
                        it.forEach {
                            println(it)
                        }
                    }
                }
    }
}