package sx.android.aidc

import org.slf4j.LoggerFactory
import rx.Observable
import rx.lang.kotlin.PublishSubject
import rx.lang.kotlin.synchronized

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
    open protected fun onSubscription() { }

    /**
     * On unsubscription of reader eventrs
     */
    open protected fun onUnsubscription() { }

    /**
     * On decoder set
     */
    abstract protected fun onDecoderSet(decoder: Decoder)

    val lifecycle by lazy {
        PublishSubject<Unit>().asObservable().doOnSubscribe {
            this.onSubscription()
        }.doOnUnsubscribe {
            this.onUnsubscription()
        }
    }

    /**
     * Barcode reader event
     */
    val readEvent by lazy { this.readEventSubject.asObservable() }
    protected val readEventSubject by lazy { PublishSubject<ReadEvent>().synchronized() }
}