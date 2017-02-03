package org.deku.leoz.rest

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import org.deku.leoz.config.RestFeignClientConfiguration
import org.deku.leoz.rest.service.internal.v1.StationService
import org.junit.Test
import org.slf4j.LoggerFactory
import rx.Observable
import rx.lang.kotlin.subscribeWith

/**
 * Created by n3 on 10/12/2016.
 */
class StationServiceTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        init {
            Kodein.global.addImport(RestFeignClientConfiguration.module)
        }
    }

    @Test
    fun testGet() {
        val stationService: StationService = Kodein.global.instance()

        Observable.fromCallable { stationService.get() }
                .subscribeWith {
                    onNext {
                        log.info("Received ${it.count()} statinos")
                    }
                }
    }
}