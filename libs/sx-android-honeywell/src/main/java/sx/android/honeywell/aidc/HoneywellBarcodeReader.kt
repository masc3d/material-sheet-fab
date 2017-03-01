package sx.android.honeywell.aidc

import android.content.Context
import com.honeywell.aidc.AidcManager
import com.honeywell.aidc.BarcodeFailureEvent
import com.honeywell.aidc.BarcodeReadEvent
import com.honeywell.aidc.TriggerStateChangeEvent
import org.slf4j.LoggerFactory
import rx.Observable
import sx.android.aidc.BarcodeDecoder
import sx.android.aidc.BarcodeReader
import sx.android.aidc.BarcodeType
import sx.android.aidc.Decoder
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

    override fun onBind() {
        log.debug("Claiming reader")
        this.honeywellReader.claim()
    }

    override fun onUnbind() {
        log.debug("Releasing reader")
        this.honeywellReader.release()
    }

    /**
     * Honeywell barcode reader
     */
    private val honeywellReader: com.honeywell.aidc.BarcodeReader by lazy {
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

        bc
    }

    override var enabled: Boolean = false
        set(value) {
            field = true
            this.honeywellReader.decode(field)
        }

    var centerDecode: Boolean
        get() { return this.honeywellReader.getBooleanProperty(com.honeywell.aidc.BarcodeReader.PROPERTY_CENTER_DECODE) }
        set(value) { this.honeywellReader.setProperty(com.honeywell.aidc.BarcodeReader.PROPERTY_CENTER_DECODE, value) }

    override fun onDecoderSet(decoder: Decoder) {
        log.info("Setting decoder [${decoder}]")

        when (decoder) {
            is BarcodeDecoder -> {
                // TODO. add support for barcode lengths
                when (decoder.barcodeType) {
                    BarcodeType.Ean8 -> {
                        this.honeywellReader.setProperty(
                                com.honeywell.aidc.BarcodeReader.PROPERTY_EAN_8_ENABLED,
                                decoder.enabled)
                    }
                    BarcodeType.Ean13 -> {
                        this.honeywellReader.setProperty(
                                com.honeywell.aidc.BarcodeReader.PROPERTY_EAN_13_ENABLED,
                                decoder.enabled)
                    }
                    BarcodeType.Code128 -> {
                        this.honeywellReader.setProperty(
                                com.honeywell.aidc.BarcodeReader.PROPERTY_CODE_128_ENABLED,
                                decoder.enabled)
                    }
                    BarcodeType.Code39 -> {
                        this.honeywellReader.setProperty(
                                com.honeywell.aidc.BarcodeReader.PROPERTY_CODE_39_ENABLED,
                                decoder.enabled)
                    }
                    BarcodeType.Interleaved25 -> {
                        this.honeywellReader.setProperty(
                                com.honeywell.aidc.BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED,
                                decoder.enabled)
                    }
                    BarcodeType.Datamatrix -> {
                        this.honeywellReader.setProperty(
                                com.honeywell.aidc.BarcodeReader.PROPERTY_DATAMATRIX_ENABLED,
                                decoder.enabled)
                    }
                    BarcodeType.Pdf417 -> {
                        this.honeywellReader.setProperty(
                                com.honeywell.aidc.BarcodeReader.PROPERTY_PDF_417_ENABLED,
                                decoder.enabled)
                    }
                    BarcodeType.QrCode -> {
                        this.honeywellReader.setProperty(
                                com.honeywell.aidc.BarcodeReader.PROPERTY_QR_CODE_ENABLED,
                                decoder.enabled)
                    }
                    else -> throw UnsupportedOperationException("Unsupported barcode type [${decoder.barcodeType}]")
                }
            }
        }
    }

    override fun onBarcodeEvent(evt: BarcodeReadEvent) {
        this.readEventSubject.onNext(ReadEvent(data = evt.barcodeData))
    }

    override fun onFailureEvent(evt: BarcodeFailureEvent) {
        log.error(evt.toString())
    }

    override fun onTriggerEvent(p0: TriggerStateChangeEvent?) {
        log.trace("Trigger event")
    }
}