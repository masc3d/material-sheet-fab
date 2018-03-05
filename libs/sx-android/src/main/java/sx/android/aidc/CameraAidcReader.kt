package sx.android.aidc

import android.content.Context
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.Intents
import com.journeyapps.barcodescanner.*
import com.trello.rxlifecycle2.android.RxLifecycleAndroid
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.slf4j.LoggerFactory
import org.threeten.bp.Duration
import sx.aidc.SymbologyType
import sx.rx.ObservableRxProperty
import java.lang.Exception
import java.util.*

/**
 * Barcode reader implementation using the internal camera
 * Created by masc on 28/02/2017.
 */
class CameraAidcReader(val context: Context) : AidcReader(), BarcodeCallback {

    /** The duration/threshold for distinct aidc reads */
    private val distinctThreshold = Duration.ofSeconds(2)

    /**
     * Barcode view, tightly coupled to its parent class {@link CameraAidcReader}
     */
    inner class View(context: Context) : DecoratedBarcodeView(context) {
        private val log = LoggerFactory.getLogger(this.javaClass)
        override fun onAttachedToWindow() {
            super.onAttachedToWindow()

            this.setStatusText("")

            // Subscribe to changes in reader and delegate to zxing/decorated barcode view
            this@CameraAidcReader.decodersUpdatedSubject
                    .compose(RxLifecycleAndroid.bindView(this))
                    .subscribe {
                        this.barcodeView.setDecoderFactory(DefaultDecoderFactory(
                                this@CameraAidcReader.mapBarcodeFormats(),
                                null,
                                null,
                                Intents.Scan.NORMAL_SCAN
                        ))
                    }

            this@CameraAidcReader.enabledProperty
                    .compose(RxLifecycleAndroid.bindView(this))
                    .doOnDispose {
                        this.pause()
                    }
                    .subscribe {
                        log.trace("Enabled [${it}]")
                        when (it.value) {
                            true -> {
                                this.resume()
                                this.isSoundEffectsEnabled = true
                                this.decodeContinuous(this@CameraAidcReader)
                            }
                            false -> {
                                this.pause()
                            }
                        }
                    }

            this@CameraAidcReader.torchProperty
                    .compose(RxLifecycleAndroid.bindView(this))
                    .subscribe {
                        this.barcodeView.setTorch(it.value)
                    }
        }
    }

    //region Barcode type/format mapping
    /**
     * Pair mapping of aidc.BarcodeType to zxing.BarcodeFormat
     */
    private val barcodeTypeMapping = listOf(
            Pair(SymbologyType.Code128, BarcodeFormat.CODE_128),
            Pair(SymbologyType.Code39, BarcodeFormat.CODE_39),
            Pair(SymbologyType.Datamatrix, BarcodeFormat.DATA_MATRIX),
            Pair(SymbologyType.Ean13, BarcodeFormat.EAN_13),
            Pair(SymbologyType.Ean8, BarcodeFormat.EAN_8),
            Pair(SymbologyType.Interleaved25, BarcodeFormat.ITF),
            Pair(SymbologyType.Pdf417, BarcodeFormat.PDF_417),
            Pair(SymbologyType.QrCode, BarcodeFormat.QR_CODE)
    )

    private val barcodeFormatByType by lazy {
        barcodeTypeMapping.toMap()
    }

    private val barcodeTypeByFormat by lazy {
        barcodeTypeMapping.map { Pair(it.second, it.first) }.toMap()
    }

    private fun mapBarcodeFormats(): List<BarcodeFormat> {
        return this.decoders.mapNotNull {
            when (it) {
                is BarcodeDecoder -> this.barcodeFormatByType[it.symbologyType]
                else -> null
            }
        }
    }
    //endregion

    val torchProperty = ObservableRxProperty(false)
    var torch: Boolean by torchProperty

    /** Tracks camera state */
    private val isCameraInUseSubject = BehaviorSubject.createDefault(false)
    /** Camera state observable */
    val isCameraInUse = this.isCameraInUseSubject.hide()

    /**
     * View finder
     */
    val view by lazy {
        View(this.context).also {
            it.barcodeView.addStateListener(object : CameraPreview.StateListener {
                override fun previewStarted() {
                    log.trace("CAMERA OPEN")
                    isCameraInUseSubject.onNext(true)
                }

                override fun cameraClosed() {
                    log.trace("CAMERA CLOSED")
                    isCameraInUseSubject.onNext(false)
                }

                override fun cameraError(error: Exception?) {}
                override fun previewStopped() {}
                override fun previewSized() {}
            })
        }
    }

    //region Zxing barcode callback
    private var distinctTimestamp = 0L

    private val filteredReadEventSubject = PublishSubject.create<ReadEvent>()
    private val filteredReadEvent = filteredReadEventSubject
            .hide()
            .distinctUntilChanged { previous, current ->
                if (previous.data == current.data) {
                    // Reset timestamp on first distinct check
                    if (distinctTimestamp == 0L)
                        distinctTimestamp = current.timestamp.time

                    val diff = Duration.ofMillis(current.timestamp.time - distinctTimestamp)

                    // Reset timestamp when threshold was reached
                    if (diff >= distinctThreshold)
                        distinctTimestamp = current.timestamp.time

                    diff < distinctThreshold
                } else {
                    distinctTimestamp = current.timestamp.time
                    false
                }
            }

    override fun barcodeResult(result: BarcodeResult) {
        val barcodeType = barcodeTypeByFormat[result.barcodeFormat] ?: SymbologyType.Unknown

        // Forward to filter
        this.filteredReadEventSubject.onNext(ReadEvent(data = result.text, symbologyType = barcodeType))
    }

    override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>) {
    }
    //endregion

    init {
        this.filteredReadEvent
                .subscribe {
                    this.readEventSubject.onNext(it)
                }
    }
}