package org.deku.leoz.mobile.config

import android.content.Context
import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.instacart.library.truetime.TrueTimeRx
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.deku.leoz.mobile.settings.RemoteSettings
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit

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

        //val dateTimeOffsetProperty = PublishSubject.create<Float?>()
        //val dateTimeOffset = dateTimeOffsetProperty.hide()


        /**
         * This observable emits every 5 minutes the current Date/Time offset.
         */
        val trueTimeOffset = Observable.create<Float?> {
            Schedulers.newThread().schedulePeriodicallyDirect(
                    {
                        val offset = getOffset()
                        if (offset == null)
                            it.onError(IllegalStateException("TrueTimeRx not yet initialized"))
                        else
                            it.onNext(offset)
                    },
                    0,
                    5,
                    TimeUnit.MINUTES)
        }
                /* .doOnNext { dateTimeOffsetProperty.onNext(it ?: 0F) }
                .toHotIoObservable(this.log)
                */
                // .subscribe { dateTimeOffsetProperty.onNext(it ?: 0F) }

        init {
            initializeTrueTime()
                    //.retry()
                    .subscribeBy(
                            onComplete = {
                                log.debug("Initializing TrueTime succeeded")
                                //dateTimeOffsetProperty.onNext(getOffset()!!)
                            },

                            onError = {
                                log.warn("Initializing TrueTime failed with error.", it)
                            }
                    )
        }

        private fun initializeTrueTime(): Completable { // Observable<Boolean> {
            return Completable.create {
                val subscriber = it
                        TrueTimeRx.build()
                                .withSharedPreferences(context)
                                .withLoggingEnabled(true)
                                .initializeRx(remoteSettings.ntp.host)
                                .subscribeOn(Schedulers.io())
                                .subscribe({ date ->
                                    run {
                                        log.trace("TrueTime was initialized at [$date]")
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

        private fun getOffset(): Float? = if (!TrueTimeRx.isInitialized()) null else (this.current()!!.time - Date().time) / 1000F
    }
}