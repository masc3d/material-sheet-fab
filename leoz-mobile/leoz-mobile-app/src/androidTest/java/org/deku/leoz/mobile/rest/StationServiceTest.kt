package org.deku.leoz.mobile.rest

import android.support.test.runner.AndroidJUnit4
import org.junit.Test
import feign.Feign
import feign.jackson.JacksonEncoder
import feign.jackson.JacksonDecoder
import feign.jaxrs.JAXRSContract
import org.deku.leoz.rest.service.internal.v1.StationService
import org.junit.runner.RunWith
import android.support.test.InstrumentationRegistry
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import org.deku.leoz.mobile.WebserviceTest
import org.deku.leoz.rest.entity.internal.v1.Station
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import sx.rx.task

/**
 * Created by n3 on 06/12/2016.
 */
@RunWith(AndroidJUnit4::class)
class StationServiceTest : WebserviceTest() {

    @Test
    fun testGet() {
        val stationService: StationService = Kodein.global.instance()

        Observable.fromCallable { stationService.get() }
                .subscribeBy(
                        onNext = {
                        it.forEach {
                            println(it)
                        }
                        })
    }
}