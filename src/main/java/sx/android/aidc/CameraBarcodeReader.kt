package sx.android.aidc

import android.content.Context

/**
 * Barcode reader implementation using the internal camera
 * Created by masc on 28/02/2017.
 */
class CameraBarcodeReader(val context: Context) : BarcodeReader() {
    /**
     * As the camera reader need to be activated explicitly, it's passive tnable state is always true
     */
    override var enabled: Boolean
        get() = true
        set(value) {}

    override fun onDecoderSet(decoder: Decoder) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}