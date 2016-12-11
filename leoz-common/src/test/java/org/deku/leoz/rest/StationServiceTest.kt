package org.deku.leoz.rest

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import feign.Feign
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.jaxrs.JAXRSContract
import org.deku.leoz.config.RestConfiguration
import org.deku.leoz.rest.service.internal.v1.StationService
import org.junit.Test
import rx.Observable
import rx.lang.kotlin.subscribeWith

/**
 * Created by n3 on 10/12/2016.
 */
class StationServiceTest {
    companion object {
        init {
            Kodein.global.addImport(RestConfiguration.module)
        }
    }

    @Test
    fun testGet() {
        val stationService: StationService = Kodein.global.instance()

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