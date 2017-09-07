package sx.android.honeywell.aidc

import android.content.Context
import com.honeywell.aidc.*
import org.slf4j.LoggerFactory
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import sx.LazyInstance
import sx.android.aidc.*
import sx.aidc.SymbologyType
import sx.rx.toHotReplay

/**
 * Honeywell barcode reader implementation
 * @property context Android context
 * Created by masc on 28/02/2017.
 */
class HoneywellAidcReader private constructor(
        private val aidcManager: AidcManager
) : AidcReader(), BarcodeReader.BarcodeListener, BarcodeReader.TriggerListener {

    companion object {
        private val log = LoggerFactory.getLogger(HoneywellAidcReader::class.java)

        /**
         * Creates HoneywellAidcReader instance. This method is asynchronous.
         * @return Hot reply observable emitting AidcReader when it is (or has become) available
         */
        fun create(context: Context): Observable<AidcReader> {
            return Observable.create<AidcReader> { onSubscribe ->
                try {
                    log.debug("Creating AidcManager")
                    AidcManager.create(context) {
                        log.debug("AidcManager created")
                        onSubscribe.onNext(HoneywellAidcReader(it))
                        onSubscribe.onComplete()
                    }
                } catch(e: Throwable) {
                    onSubscribe.onError(e)
                }
            }.toHotReplay()
        }
    }

    private val subscriptions = mutableListOf<Disposable>()

    override fun onBind() {
        log.debug("Claiming reader")
        this.honeywellReader.claim()

        this.subscriptions.add(
                this.enabledProperty
                        .distinctUntilChanged()
                        .subscribe {
                    when(it.value) {
                        false -> {
                            log.debug("Closing")
                            this.honeywellReader.decode(false)
                        }
                    }
                })

        this.subscriptions.add(
                this.decodersUpdatedSubject.subscribe {
                    this.onDecodersUpdated(it)
                })
    }

    override fun onUnbind() {
        log.debug("Releasing reader")
        this.subscriptions.forEach { it.dispose() }
        this.subscriptions.clear()
        this.honeywellReader.release()
    }

    private val honeywellReader: BarcodeReader
            get() { return honeywellReaderInstance.get() }
    /**
     * Honeywell barcode reader
     */
    private val honeywellReaderInstance = LazyInstance<BarcodeReader>({
        val bc = this.aidcManager.createBarcodeReader()
        bc.addBarcodeListener(this)
        bc.addTriggerListener(this)

        // Default settings
        bc.setProperties(mapOf<String, Any>(
                Pair(BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE, BarcodeReader.TRIGGER_CONTROL_MODE_CLIENT_CONTROL),
                Pair(BarcodeReader.PROPERTY_CENTER_DECODE, false),
                // Disable all symbologies
                Pair(BarcodeReader.PROPERTY_AZTEC_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_CHINA_POST_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_CODABAR_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_CODABAR_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_CODE_11_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_CODE_128_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_CODE_39_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_CODE_93_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_CODABLOCK_A_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_CODABLOCK_F_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_COMPOSITE_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_EAN_8_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_UPC_A_ENABLE, false),
                Pair(BarcodeReader.PROPERTY_DATAMATRIX_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_EAN_13_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_GS1_128_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_HAX_XIN_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_IATA_25_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_ISBT_128_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_KOREAN_POST_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_MATRIX_25_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_MAXICODE_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_MICRO_PDF_417_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_MSI_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_PDF_417_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_QR_CODE_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_RSS_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_STANDARD_25_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_TELEPEN_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_TLC_39_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_TRIOPTIC_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_UPC_E_E1_ENABLED, false),
                Pair(BarcodeReader.PROPERTY_UPC_E_ENABLED, false)
        ))

        bc
    })

    var centerDecode: Boolean
        get() {
            return this.honeywellReader.getBooleanProperty(BarcodeReader.PROPERTY_CENTER_DECODE)
        }
        set(value) {
            this.honeywellReader.setProperty(BarcodeReader.PROPERTY_CENTER_DECODE, value)
        }

    private fun onDecodersUpdated(decoders: Array<out Decoder>) {
        decoders.forEach { decoder ->
            log.info("Setting decoder [${decoder}]")

            when (decoder) {
                is BarcodeDecoder -> {
                    when (decoder) {
                        is Ean8Decoder -> {
                            val properties = mutableMapOf<String, Any>()
                            properties.put(BarcodeReader.PROPERTY_EAN_8_ENABLED, decoder.enabled)
                            this.honeywellReader.setProperties(properties)
                        }
                        is Ean13Decoder -> {
                            val properties = mutableMapOf<String, Any>()
                            properties.put(BarcodeReader.PROPERTY_EAN_13_ENABLED, decoder.enabled)
                            this.honeywellReader.setProperties(properties)
                        }
                        is Code128Decoder -> {
                            val properties = mutableMapOf<String, Any>()
                            properties.put(BarcodeReader.PROPERTY_CODE_128_ENABLED, decoder.enabled)
                            if (decoder.minimumLength != null)
                                properties.put(BarcodeReader.PROPERTY_CODE_128_MINIMUM_LENGTH, decoder.minimumLength as Int)
                            if (decoder.maximumLength != null)
                                properties.put(BarcodeReader.PROPERTY_CODE_128_MAXIMUM_LENGTH, decoder.maximumLength as Int)
                            this.honeywellReader.setProperties(properties)
                        }
                        is Code39Decoder -> {
                            val properties = mutableMapOf<String, Any>()
                            properties.put(BarcodeReader.PROPERTY_CODE_39_ENABLED, decoder.enabled)
                            if (decoder.minimumLength != null)
                                properties.put(BarcodeReader.PROPERTY_CODE_39_MINIMUM_LENGTH, decoder.minimumLength as Int)
                            if (decoder.maximumLength != null)
                                properties.put(BarcodeReader.PROPERTY_CODE_39_MAXIMUM_LENGTH, decoder.maximumLength as Int)
                            this.honeywellReader.setProperties(properties)
                        }
                        is Interleaved25Decoder -> {
                            val properties = mutableMapOf<String, Any>()
                            properties.put(BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED, decoder.enabled)
                            if (decoder.minimumLength != null)
                                properties.put(BarcodeReader.PROPERTY_INTERLEAVED_25_MINIMUM_LENGTH, decoder.minimumLength as Int)
                            if (decoder.maximumLength != null)
                                properties.put(BarcodeReader.PROPERTY_INTERLEAVED_25_MAXIMUM_LENGTH, decoder.maximumLength as Int)
                            this.honeywellReader.setProperties(properties)
                        }
                        is DatamatrixDecoder -> {
                            val properties = mutableMapOf<String, Any>()
                            properties.put(BarcodeReader.PROPERTY_DATAMATRIX_ENABLED, decoder.enabled)
                            if (decoder.minimumLength != null)
                                properties.put(BarcodeReader.PROPERTY_DATAMATRIX_MINIMUM_LENGTH, decoder.minimumLength as Int)
                            if (decoder.maximumLength != null)
                                properties.put(BarcodeReader.PROPERTY_DATAMATRIX_MAXIMUM_LENGTH, decoder.maximumLength as Int)
                            this.honeywellReader.setProperties(properties)
                        }
                        is Pdf417Decoder -> {
                            val properties = mutableMapOf<String, Any>()
                            properties.put(BarcodeReader.PROPERTY_PDF_417_ENABLED, decoder.enabled)
                            if (decoder.minimumLength != null)
                                properties.put(BarcodeReader.PROPERTY_PDF_417_MINIMUM_LENGTH, decoder.minimumLength as Int)
                            if (decoder.maximumLength != null)
                                properties.put(BarcodeReader.PROPERTY_PDF_417_MAXIMUM_LENGTH, decoder.maximumLength as Int)
                            this.honeywellReader.setProperties(properties)
                        }
                        is QrCodeDecoder -> {
                            val properties = mutableMapOf<String, Any>()
                            properties.put(BarcodeReader.PROPERTY_QR_CODE_ENABLED, decoder.enabled)
                            if (decoder.minimumLength != null)
                                properties.put(BarcodeReader.PROPERTY_QR_CODE_MINIMUM_LENGTH, decoder.minimumLength as Int)
                            if (decoder.maximumLength != null)
                                properties.put(BarcodeReader.PROPERTY_QR_CODE_MAXIMUM_LENGTH, decoder.maximumLength as Int)
                            this.honeywellReader.setProperties(properties)
                        }
                        else -> throw UnsupportedOperationException("Unsupported barcode type [${decoder.symbologyType}]")
                    }
                }
            }
        }
    }

    enum class CodeId(val value: String, val symbologyType: SymbologyType) {
        Unknown("", SymbologyType.Unknown),

        Code128("j", SymbologyType.Code128),
        Code39("b", SymbologyType.Code39),
        DataMatrix("w", SymbologyType.Datamatrix),
        Ean8("D", SymbologyType.Ean8),
        Ean13("d", SymbologyType.Ean13),
        Interleaved25("e", SymbologyType.Interleaved25),
        Pdf417("r", SymbologyType.Pdf417),
        QrCode("s", SymbologyType.QrCode),
    }

    override fun onBarcodeEvent(evt: BarcodeReadEvent) {
        val barcodeType = CodeId.values()
                .firstOrNull { it.value == evt.codeId }
                ?.symbologyType ?: SymbologyType.Unknown

        this.readEventSubject.onNext(ReadEvent(data = evt.barcodeData, symbologyType = barcodeType))
    }

    override fun onFailureEvent(evt: BarcodeFailureEvent) {
        log.error("Honeywell aidc failure event ${evt}")
    }

    override fun onTriggerEvent(evt: TriggerStateChangeEvent) {
        if (this.enabled) {
            this.honeywellReader.aim(evt.state)
            this.honeywellReader.light(evt.state)
            this.honeywellReader.decode(evt.state)
        }
    }
}