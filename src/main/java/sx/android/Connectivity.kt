package sx.android

import android.annotation.SuppressLint
import android.content.Context
import android.net.NetworkInfo
import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.strategy.WalledGardenInternetObservingStrategy
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import org.threeten.bp.Duration
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

    @SuppressLint("MissingPermission")
    private fun observeInternetAvailability(): Observable<Boolean> {
        return ReactiveNetwork.observeInternetConnectivity(
                WalledGardenInternetObservingStrategy(),
                // Initial delay
                Duration.ofSeconds(0).toMillis().toInt(),
                // Interval
                Duration.ofSeconds(5).toMillis().toInt(),
                "http://clients3.google.com/generate_204",
                80,
                // Connection timeout
                Duration.ofSeconds(5).toMillis().toInt(),
                // Empty error handler to suppress failed connection message spam
                { _, _ -> }
        )
                .subscribeOn(Schedulers.io())
    }

    private var intenretAvailabilitySubscription: Disposable? = null
    private var lastNetworkConnectivityUpdate: Connectivity = Connectivity.create()

    init {
        ReactiveNetwork.observeNetworkConnectivity(context)
                .subscribeOn(Schedulers.io())
                .subscribeBy(onNext = {
                    log.info("Network connectivity state change [${it}]")
                    this.lastNetworkConnectivityUpdate = it
                    this.network = it

                    when (it.state) {
                        NetworkInfo.State.CONNECTED ->
                            this.intenretAvailabilitySubscription?.dispose()

                        NetworkInfo.State.DISCONNECTED -> {
                            this.intenretAvailabilitySubscription?.dispose()

                            // Fallback to socket connection based observing
                            this.intenretAvailabilitySubscription = this.observeInternetAvailability()
                                    .subscribeBy(onNext = { availability ->
                                        log.info("Internet availability [${availability}]")

                                        // Translate to synthetic state
                                        val state = if (availability)
                                            NetworkInfo.State.CONNECTED
                                        else
                                            NetworkInfo.State.DISCONNECTED

                                        // Emit state on update
                                        if (network.state != state) {
                                            this.network = Connectivity.Builder()
                                                    .state(state)
                                                    .build()
                                        }

                                        if (this.lastNetworkConnectivityUpdate.state != state) {
                                            log.warn("Inconsistent network connectivity state ${this.lastNetworkConnectivityUpdate.state} while internet was ${state}")
                                        }
                                    })
                        }

                        else -> Unit
                    }
                })
    }
}