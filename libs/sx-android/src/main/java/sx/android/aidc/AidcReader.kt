package sx.android.aidc

import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import org.slf4j.LoggerFactory
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import sx.rx.ObservableRxProperty
import sx.aidc.SymbologyType

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

    data class ReadEvent(val data: String, val symbologyType: SymbologyType)

    val enabledProperty = ObservableRxProperty(false)
    /**
     * Enable or disable barcode reader
     */
    var enabled: Boolean by enabledProperty

    protected val decodersUpdatedSubject = BehaviorSubject.create<Array<out Decoder>>()
    /**
     * Decoders
     */
    val decoders: Decoders = Decoders()

    /**
     * On subscription of reader events
     */
    protected open fun onBind() { }
    internal fun onBindInternal() { this.onBind() }

    /**
     * On unsubscription of reader eventrs
     */
    protected open fun onUnbind() { }
    internal fun onUnbindInternal() { this.onUnbind() }

    private var bindRefCount = 0

    /**
     * Barcode reader event
     */
    val readEvent by lazy { this.readEventSubject.hide() }
    protected val readEventSubject by lazy { PublishSubject.create<ReadEvent>().toSerialized() }

    //region Lifecycle support
    /**
     * Observable for binding to rxlifecycle
     */
    private val lifecycle by lazy {
        PublishSubject.create<Unit>().hide()
                .doOnSubscribe {
                    if (bindRefCount == 0)
                        this.onBind()
                    bindRefCount++
                    log.trace("Bind ref count [${bindRefCount}]")
                }
                .doOnDispose {
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