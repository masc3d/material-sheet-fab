package sx.android.aidc

import com.trello.rxlifecycle.LifecycleProvider
import com.trello.rxlifecycle.android.ActivityEvent
import com.trello.rxlifecycle.android.FragmentEvent
import com.trello.rxlifecycle.kotlin.bindUntilEvent
import org.slf4j.LoggerFactory
import rx.Observable
import rx.lang.kotlin.PublishSubject
import rx.lang.kotlin.synchronized
import sx.Lifecycle

/**
 * Abstract barcode reader
 * Created by masc on 28/02/2017.
 */
abstract class BarcodeReader {
    private val log = LoggerFactory.getLogger(this.javaClass)

    inner class Decoders : Iterable<Decoder> {
        /**
         * Map of decoders by unique key
         */
        private val decoderMap = mutableMapOf<String, Decoder>()

        /**
         * Set/apply decoder (configuration)
         */
        fun set(vararg decoder: Decoder) {
            decoder.forEach {
                this@BarcodeReader.onDecoderSet(it)
                decoderMap[it.key] = it
            }
        }

        override fun iterator(): Iterator<Decoder> {
            return this.decoderMap.values.iterator()
        }
    }

    data class ReadEvent(val data: String)

    /**
     * Decoders
     */
    val decoders: Decoders = Decoders()

    /**
     * Enable or disable barcode reader
     */
    abstract var enabled: Boolean

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

    /**
     * On decoder set
     */
    abstract protected fun onDecoderSet(decoder: Decoder)

    private var bindRefCount = 0

    /**
     * Observable for binding to rxlifecycle
     */
    private val lifecycle by lazy {
        PublishSubject<Unit>().asObservable()
                .doOnSubscribe {
                    if (bindRefCount == 0)
                        this.onBind()
                    bindRefCount++
                }
                .doOnUnsubscribe {
                    bindRefCount--
                    if (bindRefCount == 0)
                        this.onUnbind()
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

    /**
     * Barcode reader event
     */
    val readEvent by lazy { this.readEventSubject.asObservable() }
    protected val readEventSubject by lazy { PublishSubject<ReadEvent>().synchronized() }
}