package sx.android.honeywell.aidc

import android.content.Context
import com.honeywell.aidc.AidcManager
import com.honeywell.aidc.BarcodeFailureEvent
import com.honeywell.aidc.BarcodeReadEvent
import com.honeywell.aidc.TriggerStateChangeEvent
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Subscription
import sx.LazyInstance
import sx.android.aidc.*
import sx.rx.toHotReplay

/**
 * Honeywell barcode reader implementation
 * @property context Android context
 * Created by masc on 28/02/2017.
 */
class HoneywellBarcodeReader private constructor(
        private val aidcManager: AidcManager
) : BarcodeReader(), com.honeywell.aidc.BarcodeReader.BarcodeListener, com.honeywell.aidc.BarcodeReader.TriggerListener {

    companion object {
        private val log = LoggerFactory.getLogger(HoneywellBarcodeReader::class.java)

        /**
         * Creates HoneywellBarcodeReader instance. This method is asynchronous.
         * @return Hot reply observable emitting BarcodeReader when it is (or has become) available
         */
        fun create(context: Context): Observable<BarcodeReader> {
            return Observable.create<BarcodeReader> { onSubscribe ->
                try {
                    onSubscribe.onStart()
                    log.debug("Creating AidcManager")
                    AidcManager.create(context) {
                        log.debug("AidcManager created")
                        onSubscribe.onNext(HoneywellBarcodeReader(it))
                        onSubscribe.onCompleted()
                    }
                } catch(e: Throwable) {
                    onSubscribe.onError(e)
                }
            }.toHotReplay()
        }
    }

    private val subscriptions = mutableListOf<Subscription>()

    override fun onBind() {
        log.debug("Claiming reader")
        this.honeywellReader.claim()

        this.subscriptions.add(
                this.enabledSubject.subscribe {
                    log.debug("ENABLED ${it}")
                    this.honeywellReader.decode(it)
                })

        this.subscriptions.add(
                this.decodersUpdatedSubject.subscribe {
                    this.onDecodersUpdated(it)
                })
    }

    override fun onUnbind() {
        log.debug("Releasing reader")
        this.subscriptions.forEach { it.unsubscribe() }
        this.subscriptions.clear()
        this.honeywellReader.release()
    }

    private val honeywellReader: com.honeywell.aidc.BarcodeReader
            get() { return honeywellReaderInstance.get() }
    /**
     * Honeywell barcode reader
     */
    private val honeywellReaderInstance = LazyInstance<com.honeywell.aidc.BarcodeReader>({
        val bc = this.aidcManager.createBarcodeReader()
        bc.addBarcodeListener(this)
        bc.addTriggerListener(this)

        // Default settings
        bc.setProperty(
                com.honeywell.aidc.BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED,
                false)
        bc.setProperty(
                com.honeywell.aidc.BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
                com.honeywell.aidc.BarcodeReader.TRIGGER_CONTROL_MODE_AUTO_CONTROL)

        // TODO: disable all symbologies on initialization

        bc
    })

    init {
    }

    var centerDecode: Boolean
        get() {
            return this.honeywellReader.getBooleanProperty(com.honeywell.aidc.BarcodeReader.PROPERTY_CENTER_DECODE)
        }
        set(value) {
            this.honeywellReader.setProperty(com.honeywell.aidc.BarcodeReader.PROPERTY_CENTER_DECODE, value)
        }

    private fun onDecodersUpdated(decoders: Array<out Decoder>) {
        decoders.forEach { decoder ->
            log.info("Setting decoder [${decoder}]")

            when (decoder) {
                is BarcodeDecoder -> {
                    when (decoder) {
                        is Ean8Decoder -> {
                            val properties = mutableMapOf<String, Any>()
                            properties.put(com.honeywell.aidc.BarcodeReader.PROPERTY_EAN_8_ENABLED, decoder.enabled)
                            this.honeywellReader.setProperties(properties)
                        }
                        is Ean13Decoder -> {
                            val properties = mutableMapOf<String, Any>()
                            properties.put(com.honeywell.aidc.BarcodeReader.PROPERTY_EAN_13_ENABLED, decoder.enabled)
                            this.honeywellReader.setProperties(properties)
                        }
                        is Code128Decoder -> {
                            val properties = mutableMapOf<String, Any>()
                            properties.put(com.honeywell.aidc.BarcodeReader.PROPERTY_CODE_128_ENABLED, decoder.enabled)
                            if (decoder.minimumLength != null)
                                properties.put(com.honeywell.aidc.BarcodeReader.PROPERTY_CODE_128_MINIMUM_LENGTH, decoder.minimumLength as Int)
                            if (decoder.maximumLength != null)
                                properties.put(com.honeywell.aidc.BarcodeReader.PROPERTY_CODE_128_MAXIMUM_LENGTH, decoder.maximumLength as Int)
                            this.honeywellReader.setProperties(properties)
                        }
                        is Code39Decoder -> {
                            val properties = mutableMapOf<String, Any>()
                            properties.put(com.honeywell.aidc.BarcodeReader.PROPERTY_CODE_39_ENABLED, decoder.enabled)
                            if (decoder.minimumLength != null)
                                properties.put(com.honeywell.aidc.BarcodeReader.PROPERTY_CODE_39_MINIMUM_LENGTH, decoder.minimumLength as Int)
                            if (decoder.maximumLength != null)
                                properties.put(com.honeywell.aidc.BarcodeReader.PROPERTY_CODE_39_MAXIMUM_LENGTH, decoder.maximumLength as Int)
                            this.honeywellReader.setProperties(properties)
                        }
                        is Interleaved25Decoder -> {
                            val properties = mutableMapOf<String, Any>()
                            properties.put(com.honeywell.aidc.BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED, decoder.enabled)
                            if (decoder.minimumLength != null)
                                properties.put(com.honeywell.aidc.BarcodeReader.PROPERTY_INTERLEAVED_25_MINIMUM_LENGTH, decoder.minimumLength as Int)
                            if (decoder.maximumLength != null)
                                properties.put(com.honeywell.aidc.BarcodeReader.PROPERTY_INTERLEAVED_25_MAXIMUM_LENGTH, decoder.maximumLength as Int)
                            this.honeywellReader.setProperties(properties)
                        }
                        is DatamatrixDecoder -> {
                            val properties = mutableMapOf<String, Any>()
                            properties.put(com.honeywell.aidc.BarcodeReader.PROPERTY_DATAMATRIX_ENABLED, decoder.enabled)
                            if (decoder.minimumLength != null)
                                properties.put(com.honeywell.aidc.BarcodeReader.PROPERTY_DATAMATRIX_MINIMUM_LENGTH, decoder.minimumLength as Int)
                            if (decoder.maximumLength != null)
                                properties.put(com.honeywell.aidc.BarcodeReader.PROPERTY_DATAMATRIX_MAXIMUM_LENGTH, decoder.maximumLength as Int)
                            this.honeywellReader.setProperties(properties)
                        }
                        is Pdf417Decoder -> {
                            val properties = mutableMapOf<String, Any>()
                            properties.put(com.honeywell.aidc.BarcodeReader.PROPERTY_PDF_417_ENABLED, decoder.enabled)
                            if (decoder.minimumLength != null)
                                properties.put(com.honeywell.aidc.BarcodeReader.PROPERTY_PDF_417_MINIMUM_LENGTH, decoder.minimumLength as Int)
                            if (decoder.maximumLength != null)
                                properties.put(com.honeywell.aidc.BarcodeReader.PROPERTY_PDF_417_MAXIMUM_LENGTH, decoder.maximumLength as Int)
                            this.honeywellReader.setProperties(properties)
                        }
                        is QrCodeDecoder -> {
                            val properties = mutableMapOf<String, Any>()
                            properties.put(com.honeywell.aidc.BarcodeReader.PROPERTY_QR_CODE_ENABLED, decoder.enabled)
                            if (decoder.minimumLength != null)
                                properties.put(com.honeywell.aidc.BarcodeReader.PROPERTY_QR_CODE_MINIMUM_LENGTH, decoder.minimumLength as Int)
                            if (decoder.maximumLength != null)
                                properties.put(com.honeywell.aidc.BarcodeReader.PROPERTY_QR_CODE_MAXIMUM_LENGTH, decoder.maximumLength as Int)
                            this.honeywellReader.setProperties(properties)
                        }
                        else -> throw UnsupportedOperationException("Unsupported barcode type [${decoder.barcodeType}]")
                    }
                }
            }
        }
    }

    enum class CodeId(val value: String, val barcodeType: BarcodeType) {
        Unknown("", BarcodeType.Unknown),

        Code128("j", BarcodeType.Code128),
        Code39("b", BarcodeType.Code39),
        DataMatrix("w", BarcodeType.Datamatrix),
        Ean8("D", BarcodeType.Ean8),
        Ean13("d", BarcodeType.Ean13),
        Interleaved25("e", BarcodeType.Interleaved25),
        Pdf417("r", BarcodeType.Pdf417),
        QrCode("s", BarcodeType.QrCode),
    }

    override fun onBarcodeEvent(evt: BarcodeReadEvent) {
        val barcodeType = CodeId.values()
                .firstOrNull { it.value == evt.codeId }
                ?.barcodeType ?: BarcodeType.Unknown

        this.readEventSubject.onNext(ReadEvent(data = evt.barcodeData, barcodeType = barcodeType))
    }

    override fun onFailureEvent(evt: BarcodeFailureEvent) {
        log.error(evt.toString())
    }

    override fun onTriggerEvent(p0: TriggerStateChangeEvent?) {
        log.trace("Trigger event")
    }
}