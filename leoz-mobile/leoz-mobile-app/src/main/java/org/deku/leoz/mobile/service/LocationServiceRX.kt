package org.deku.leoz.mobile.service

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v4.content.ContextCompat
import com.patloew.rxlocation.RxLocation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory


@SuppressLint("MissingPermission")
/**
 * Created by 27694066 on 22.09.2017.
 */
class LocationServiceRX:
        BaseLocationService() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val locationRequest = locationServices.locationRequest

    private val disposable = CompositeDisposable()

    private val rxLocation by lazy {
        RxLocation(this)
    }

    private val rxLocationUpdates by lazy {
        rxLocation.location().updates(locationRequest)
    }

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val ret = super.onStartCommand(intent, flags, startId)

        disposable.clear()
        disposable.add(
                rxLocationUpdates
                        .subscribe(
                                {
                                this.log.trace("RxLocation Update [${it}]")
                                this.reportLocation(it)
                                },
                                {
                                    ContextCompat.startForegroundService(this.applicationContext, Intent(applicationContext, LocationServiceAOSP::class.java))
                                    this.stopSelf()
                                }
                        )

        )

        return ret
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

    override fun stopService(name: Intent?): Boolean {
        disposable.clear()
        return super.stopService(name)
    }

}