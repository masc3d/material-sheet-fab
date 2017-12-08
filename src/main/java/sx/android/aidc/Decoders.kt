package sx.android.aidc

import sx.aidc.SymbologyType

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
 * Barcode reader decoder
 */
abstract class BarcodeDecoder(
        enabled: Boolean,
        val symbologyType: SymbologyType,
        val minimumLength: Int? = null,
        val maximumLength: Int? = null
) : Decoder(enabled = enabled) {

    override val key: String
        get() = this.symbologyType.name
}

class Code128Decoder(enabled: Boolean,
                     minimumLength: Int? = null,
                     maximumLength: Int? = null)
    : BarcodeDecoder(
        enabled = enabled,
        symbologyType = SymbologyType.Code128,
        minimumLength = minimumLength,
        maximumLength = maximumLength)

class Code39Decoder(enabled: Boolean,
                    minimumLength: Int? = null,
                    maximumLength: Int? = null)
    : BarcodeDecoder(
        enabled = enabled,
        symbologyType = SymbologyType.Code39,
        minimumLength = minimumLength,
        maximumLength = maximumLength)

class Ean8Decoder(enabled: Boolean,
                  minimumLength: Int? = null,
                  maximumLength: Int? = null)
    : BarcodeDecoder(
        enabled = enabled,
        symbologyType = SymbologyType.Ean8,
        minimumLength = minimumLength,
        maximumLength = maximumLength)

class Ean13Decoder(enabled: Boolean,
                   minimumLength: Int? = null,
                   maximumLength: Int? = null)
    : BarcodeDecoder(
        enabled = enabled,
        symbologyType = SymbologyType.Ean13,
        minimumLength = minimumLength,
        maximumLength = maximumLength)

class DatamatrixDecoder(enabled: Boolean,
                        minimumLength: Int? = null,
                        maximumLength: Int? = null)
    : BarcodeDecoder(
        enabled = enabled,
        symbologyType = SymbologyType.Datamatrix,
        minimumLength = minimumLength,
        maximumLength = maximumLength)

class QrCodeDecoder(enabled: Boolean,
                    minimumLength: Int? = null,
                    maximumLength: Int? = null)
    : BarcodeDecoder(
        enabled = enabled,
        symbologyType = SymbologyType.QrCode,
        minimumLength = minimumLength,
        maximumLength = maximumLength)

class Interleaved25Decoder(enabled: Boolean,
                           minimumLength: Int? = null,
                           maximumLength: Int? = null)
    : BarcodeDecoder(
        enabled = enabled,
        symbologyType = SymbologyType.Interleaved25,
        minimumLength = minimumLength,
        maximumLength = maximumLength)

class Pdf417Decoder(enabled: Boolean,
                           minimumLength: Int? = null,
                           maximumLength: Int? = null)
    : BarcodeDecoder(
        enabled = enabled,
        symbologyType = SymbologyType.Pdf417,
        minimumLength = minimumLength,
        maximumLength = maximumLength)