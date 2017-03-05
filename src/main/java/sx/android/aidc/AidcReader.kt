package sx.android.aidc

import com.google.zxing.BarcodeFormat
import com.trello.rxlifecycle.LifecycleProvider
import com.trello.rxlifecycle.android.ActivityEvent
import com.trello.rxlifecycle.android.FragmentEvent
import com.trello.rxlifecycle.kotlin.bindUntilEvent
import org.slf4j.LoggerFactory
import rx.Observable
import rx.lang.kotlin.BehaviorSubject
import rx.lang.kotlin.PublishSubject
import rx.lang.kotlin.synchronized
import sx.Lifecycle
import sx.rx.observableRx

/**
 * Abstract barcode reader
 * Created by masc on 28/02/2017.
 */
abstract class AidcReader {
    private val log = LoggerFactory.getLogger(this.javaClass)

    inner class Decoders : Iterable<Decoder> {
        /**
         * Map of decoders by unique key
         */
        private val decoderMap = mutableMapOf<String, Decoder>()

        /**
         * Set/apply decoder (configuration)
         */
        fun set(vararg decoders: Decoder) {
            decoders.forEach {
                decoderMap[it.key] = it
            }
            this@AidcReader.decodersUpdatedSubject.onNext(this.decoderMap.values.toTypedArray())
        }

        override fun iterator(): Iterator<Decoder> {
            return this.decoderMap.values.iterator()
        }
    }

    data class ReadEvent(val data: String, val barcodeType: BarcodeType)


    val enabledSubject = BehaviorSubject<Boolean>()
    /**
     * Enable or disable barcode reader
     */
    var enabled: Boolean by observableRx(true, enabledSubject)

    /**
     * Decoders
     */
    val decoders: Decoders = Decoders()
    val decodersUpdatedSubject = BehaviorSubject<Array<out Decoder>>()

    /**
     * On subscription of reader events
     */
    open protected fun onBind() {
    }

    /**
     * On unsubscription of reader eventrs
     */
    open protected fun onUnbind() {
    }

    private var bindRefCount = 0

    /**
     * Barcode reader event
     */
    val readEvent by lazy { this.readEventSubject.asObservable() }
    protected val readEventSubject by lazy { PublishSubject<ReadEvent>().synchronized() }

    //region Lifecycle support
    /**
     * Observable for binding to rxlifecycle
     */
    private val lifecycle by lazy {
        PublishSubject<Unit>().asObservable()
                .doOnSubscribe {
                    if (bindRefCount == 0)
                        this.onBind()
                    bindRefCount++
                    log.trace("Bind ref count [${bindRefCount}]")
                }
                .doOnUnsubscribe {
                    bindRefCount--
                    if (bindRefCount == 0)
                        this.onUnbind()
                    log.trace("Bind ref count [${bindRefCount}]")
                }
    }

    /**
     * Bind lifecycle to fragment
     */
    fun bindFragment(fragment: LifecycleProvider<FragmentEvent>) {
        this.lifecycle.bindUntilEvent(fragment, FragmentEvent.PAUSE).subscribe()
    }

    /**
     * Bind lifecycle to activity
     */
    fun bindActivity(activity: LifecycleProvider<ActivityEvent>) {
        this.lifecycle.bindUntilEvent(activity, ActivityEvent.PAUSE).subscribe()
    }
    //endregion
}