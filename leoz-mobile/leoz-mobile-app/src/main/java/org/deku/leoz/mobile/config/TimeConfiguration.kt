package org.deku.leoz.mobile.config

import android.content.Context
import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.instacart.library.truetime.InvalidNtpServerResponseException
import com.instacart.library.truetime.TrueTimeRx
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BooleanSupplier
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.deku.leoz.mobile.settings.RemoteSettings
import org.slf4j.LoggerFactory
import org.threeten.bp.Duration
import java.util.*

/**
 * Created by prangenberg on 04.11.17.
 */
class TimeConfiguration {
    companion object {
        var module = Kodein.Module {
            bind<Time>() with eagerSingleton {
                Time(instance())
            }
        }
    }

    class Time(val context: Context) {

        private val remoteSettings: RemoteSettings by Kodein.global.lazy.instance()

        private val log = LoggerFactory.getLogger(this.javaClass)
        private var initCounter: Int = 0

        val dateTimeOffsetProperty = PublishSubject.create<Float>()
        val dateTimeOffset = dateTimeOffsetProperty.hide()

        init {
            initializeTrueTime()
                    //.retry()
                    .subscribeBy(
                            onComplete = {
                                log.debug("Initializing TrueTime succeeded")
                                val offset = (this.current()!!.time - Date().time) / 1000F
                                dateTimeOffsetProperty.onNext(offset)
                            },

                            onError = {
                                log.warn("Initializing TrueTime failed with error.", it)
                            }
                    )
        }

        private fun initializeTrueTime(): Observable<Boolean> {
            return Observable.create {
                val subscriber = it
                TrueTimeRx.build()
                        .withSharedPreferences(context)
                        .withLoggingEnabled(true)
                        .initializeRx(remoteSettings.ntp.host)
                        .subscribeOn(Schedulers.io())
                        .subscribe({ date ->
                            run {
                                log.trace("TrueTime was initialized at [$date]")
                                subscriber.onNext(true)
                                subscriber.onComplete()
                            }
                        }) { throwable ->
                            run {
                                log.error("TrueTime init failed. Counter [$initCounter]", throwable)
                                initCounter++
                                subscriber.onError(throwable)
                            }
                        }
            }
        }

        fun current(): Date? {
            if (!TrueTimeRx.isInitialized()) {
                return null
            }

            return TrueTimeRx.now()
        }
    }
}