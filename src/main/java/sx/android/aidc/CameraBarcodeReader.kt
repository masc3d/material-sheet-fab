package sx.android.aidc

import android.content.Context
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.jakewharton.rxbinding.view.RxView
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.trello.rxlifecycle.RxLifecycle
import com.trello.rxlifecycle.android.RxLifecycleAndroid
import org.slf4j.LoggerFactory
import rx.Subscription
import rx.lang.kotlin.BehaviorSubject
import sx.rx.observableRx

/**
 * Barcode reader implementation using the internal camera
 * Created by masc on 28/02/2017.
 */
class CameraBarcodeReader(val context: Context) : BarcodeReader(), BarcodeCallback {
    /**
     * Barcode view, tightly coupled to its parent class {@link CameraBarcodeReader}
     */
    inner class View(context: Context) : DecoratedBarcodeView(context) {
        private val log = LoggerFactory.getLogger(this.javaClass)
        override fun onAttachedToWindow() {
            super.onAttachedToWindow()

            this.setStatusText("")

            // Subscribe to changes in reader and delegate to zxing/decorated barcode view
            this@CameraBarcodeReader.decodersUpdatedSubject
                    .compose(RxLifecycleAndroid.bindView(this))
                    .subscribe {
                        this.barcodeView.setDecoderFactory(DefaultDecoderFactory(
                                this@CameraBarcodeReader.mapBarcodeFormats(), null, null
                        ))
                    }

            this@CameraBarcodeReader.enabledSubject
                    .compose(RxLifecycleAndroid.bindView(this))
                    .doOnSubscribe {
                        log.trace("VIEW SUBSCRIBED")
                    }
                    .doOnUnsubscribe {
                        log.trace("VIEW UNSUBSCRIBED")
                        this.pause()
                    }
                    .subscribe {
                        when (it) {
                            true -> {
                                log.trace("ENABLING")
                                this.resume()
                                this.decodeContinuous(this@CameraBarcodeReader)
                            }
                            false -> {
                                log.trace("DISABLING")
                                this.pause()
                            }
                        }
                    }

            this@CameraBarcodeReader.torchSubject
                    .compose(RxLifecycleAndroid.bindView(this))
                    .subscribe {
                        this.barcodeView.setTorch(it)
                    }
        }
    }

    //region Barcode type/format mapping
    /**
     * Pair mapping of aidc.BarcodeType to zxing.BarcodeFormat
     */
    private val barcodeTypeMapping = listOf(
            Pair(BarcodeType.Code128, BarcodeFormat.CODE_128),
            Pair(BarcodeType.Code39, BarcodeFormat.CODE_39),
            Pair(BarcodeType.Datamatrix, BarcodeFormat.DATA_MATRIX),
            Pair(BarcodeType.Ean13, BarcodeFormat.EAN_13),
            Pair(BarcodeType.Ean8, BarcodeFormat.EAN_8),
            Pair(BarcodeType.Interleaved25, BarcodeFormat.ITF),
            Pair(BarcodeType.Pdf417, BarcodeFormat.PDF_417),
            Pair(BarcodeType.QrCode, BarcodeFormat.QR_CODE)
    )

    private val barcodeFormatByType by lazy {
        barcodeTypeMapping.toMap()
    }

    private val barcodeTypeByFormat by lazy {
        barcodeTypeMapping.map { Pair(it.second, it.first) }.toMap()
    }
    //endregion

    private fun mapBarcodeFormats(): List<BarcodeFormat> {
        return this.decoders.mapNotNull {
            when (it) {
                is BarcodeDecoder -> this.barcodeFormatByType[it.barcodeType]
                else -> null
            }
        }
    }

    val torchSubject = BehaviorSubject<Boolean>()
    var torch: Boolean by observableRx(false, torchSubject)

    fun createView(context: Context): DecoratedBarcodeView {
        return View(context)
    }

    //region Zxing barcode callback
    override fun barcodeResult(result: BarcodeResult) {
        val barcodeType = barcodeTypeByFormat[result.barcodeFormat] ?: BarcodeType.Unknown
        this.readEventSubject.onNext(ReadEvent(data = result.text, barcodeType = barcodeType))
    }

    override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>) {
    }
    //endregion
}