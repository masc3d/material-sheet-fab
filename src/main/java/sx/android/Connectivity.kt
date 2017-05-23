package sx.android

import android.content.Context
import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import sx.rx.ObservableRxProperty

/**
 * Connectivity singleton
 * Created by n3 on 03.05.17.
 */
class Connectivity(
        private val context: Context) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Network state, observable property
     */
    val networkProperty = ObservableRxProperty<Connectivity>(Connectivity.create())
    /**
     * Current network state
     */
    var network by networkProperty
        private set

    init {
        ReactiveNetwork.observeNetworkConnectivity(context)
                .subscribeOn(Schedulers.io())
                .subscribeBy(onNext = {
                    log.info("Network connectivity state change [${it}]")
                    this.network = it
                })
    }
}