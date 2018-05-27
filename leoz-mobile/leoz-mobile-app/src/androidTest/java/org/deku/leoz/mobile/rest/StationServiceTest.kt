package org.deku.leoz.mobile.rest

import android.support.test.runner.AndroidJUnit4
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import org.deku.leoz.mobile.WebserviceTest
import org.deku.leoz.service.internal.StationService
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by masc on 06/12/2016.
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