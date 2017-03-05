package sx.android.aidc


/**
 * Generic decoder
 * @property enabled Decoder is enabled or not
 * Created by masc on 28/02/2017.
 */
abstract class Decoder(
        val enabled: Boolean) {
    /**
     * Unique key of this decoder
     */
    abstract val key: String
}

/**
 * Barcode type
 * Created by masc on 28/02/2017.
 */
enum class BarcodeType {
    Unknown,

    Code128,
    Code39,
    Datamatrix,
    Ean8,
    Ean13,
    Interleaved25,
    Pdf417,
    QrCode,
}

/**
 * Barcode reader decoder
 */
abstract class BarcodeDecoder(
        enabled: Boolean,
        val barcodeType: BarcodeType,
        val minimumLength: Int? = null,
        val maximumLength: Int? = null
) : Decoder(enabled = enabled) {

    override val key: String
        get() = this.barcodeType.name
}

class Code128Decoder(enabled: Boolean,
                     minimumLength: Int? = null,
                     maximumLength: Int? = null)
    : BarcodeDecoder(
        enabled = enabled,
        barcodeType = BarcodeType.Code128,
        minimumLength = minimumLength,
        maximumLength = maximumLength)

class Code39Decoder(enabled: Boolean,
                    minimumLength: Int? = null,
                    maximumLength: Int? = null)
    : BarcodeDecoder(
        enabled = enabled,
        barcodeType = BarcodeType.Code39,
        minimumLength = minimumLength,
        maximumLength = maximumLength)

class Ean8Decoder(enabled: Boolean,
                  minimumLength: Int? = null,
                  maximumLength: Int? = null)
    : BarcodeDecoder(
        enabled = enabled,
        barcodeType = BarcodeType.Ean8,
        minimumLength = minimumLength,
        maximumLength = maximumLength)

class Ean13Decoder(enabled: Boolean,
                   minimumLength: Int? = null,
                   maximumLength: Int? = null)
    : BarcodeDecoder(
        enabled = enabled,
        barcodeType = BarcodeType.Ean13,
        minimumLength = minimumLength,
        maximumLength = maximumLength)

class DatamatrixDecoder(enabled: Boolean,
                        minimumLength: Int? = null,
                        maximumLength: Int? = null)
    : BarcodeDecoder(
        enabled = enabled,
        barcodeType = BarcodeType.Datamatrix,
        minimumLength = minimumLength,
        maximumLength = maximumLength)

class QrCodeDecoder(enabled: Boolean,
                    minimumLength: Int? = null,
                    maximumLength: Int? = null)
    : BarcodeDecoder(
        enabled = enabled,
        barcodeType = BarcodeType.QrCode,
        minimumLength = minimumLength,
        maximumLength = maximumLength)

class Interleaved25Decoder(enabled: Boolean,
                           minimumLength: Int? = null,
                           maximumLength: Int? = null)
    : BarcodeDecoder(
        enabled = enabled,
        barcodeType = BarcodeType.Interleaved25,
        minimumLength = minimumLength,
        maximumLength = maximumLength)

class Pdf417Decoder(enabled: Boolean,
                           minimumLength: Int? = null,
                           maximumLength: Int? = null)
    : BarcodeDecoder(
        enabled = enabled,
        barcodeType = BarcodeType.Pdf417,
        minimumLength = minimumLength,
        maximumLength = maximumLength)