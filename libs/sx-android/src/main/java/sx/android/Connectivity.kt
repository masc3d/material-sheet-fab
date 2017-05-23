package sx.android

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy
import org.slf4j.LoggerFactory
import sx.rx.ObservableRxProperty

/**
 * Connectivity singleton
 * Created by n3 on 03.05.17.
 */
class Connectivity(
        private val context: Context,
        private val scheduler: Scheduler) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    val stateProperty = ObservableRxProperty<Connectivity>(Connectivity.create())
    var state by stateProperty
        private set

    init {
        ReactiveNetwork.observeNetworkConnectivity(context)
                .subscribeOn(scheduler)
                .subscribeBy(onNext = {
                    log.info("Network connectivity state change [${it}]")
                    state = it
                })
    }
}