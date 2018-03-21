//region TODO: currently missing library / reference. should be moved to dedicated project
//package sx.android.aidc
//
//import com.scandit.barcodepicker.OnScanListener
//import com.scandit.barcodepicker.ScanSession
//import com.scandit.recognition.Barcode
//import io.reactivex.subjects.PublishSubject
//import org.threeten.bp.Duration
//import sx.aidc.SymbologyType
//
///**
// * Created by prangenberg on 08.02.18.
// */
//class ScanditCameraAidcReader : AidcReader(), OnScanListener {
//
//    /** The duration/threshold for distinct aidc reads */
//    private val distinctThreshold = Duration.ofSeconds(2)
//
//    private var distinctTimestamp = 0L
//
//    private val filteredReadEventSubject = PublishSubject.create<ReadEvent>()
//    private val filteredReadEvent = filteredReadEventSubject
//            .hide()
//            .distinctUntilChanged { previous, current ->
//                if (previous.data == current.data) {
//                    // Reset timestamp on first distinct check
//                    if (distinctTimestamp == 0L)
//                        distinctTimestamp = current.timestamp.time
//
//                    val diff = Duration.ofMillis(current.timestamp.time - distinctTimestamp)
//
//                    // Reset timestamp when threshold was reached
//                    if (diff >= distinctThreshold)
//                        distinctTimestamp = current.timestamp.time
//
//                    diff < distinctThreshold
//                } else {
//                    distinctTimestamp = current.timestamp.time
//                    false
//                }
//            }
//
//    /**
//     * Pair mapping of aidc.BarcodeType to scandit.Barcode
//     */
//    private val barcodeTypeMapping = listOf(
//            Pair(SymbologyType.Code128, Barcode.SYMBOLOGY_CODE128),
//            Pair(SymbologyType.Code39, Barcode.SYMBOLOGY_CODE39),
//            Pair(SymbologyType.Datamatrix, Barcode.SYMBOLOGY_DATA_MATRIX),
//            Pair(SymbologyType.Ean13, Barcode.SYMBOLOGY_EAN13),
//            Pair(SymbologyType.Ean8, Barcode.SYMBOLOGY_EAN8),
//            Pair(SymbologyType.Interleaved25, Barcode.SYMBOLOGY_INTERLEAVED_2_OF_5),
//            Pair(SymbologyType.Pdf417, Barcode.SYMBOLOGY_PDF417),
//            Pair(SymbologyType.QrCode, Barcode.SYMBOLOGY_QR)
//    )
//
//    private val barcodeTypeByFormat by lazy {
//        barcodeTypeMapping.map { Pair(it.second, it.first) }.toMap()
//    }
//
//    override fun didScan(p0: ScanSession?) {
//        val result = p0?.newlyRecognizedCodes?.first()
//        val barcodeType = barcodeTypeByFormat[result!!.symbology] ?: SymbologyType.Unknown
//
//        this.filteredReadEventSubject.onNext(ReadEvent(data = result.data, symbologyType = barcodeType))
//    }
//
//}
//endregion