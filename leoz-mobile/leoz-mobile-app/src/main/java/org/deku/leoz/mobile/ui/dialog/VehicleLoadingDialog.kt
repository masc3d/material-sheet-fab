package org.deku.leoz.mobile.ui.dialog

import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import org.deku.leoz.mobile.R
import sx.android.aidc.*
import kotlinx.android.synthetic.main.dialog_vehicleloading.view.*
import org.deku.leoz.mobile.ui.Dialog


/**
 * Created by phpr on 29.05.2017.
 */
class VehicleLoadingDialog : Dialog(dialogLayoutId = R.layout.dialog_vehicleloading) {

    val listener = this.activity as? OnDialogResultListener

    override fun onCreateDialog(savedInstanceState: Bundle?): android.app.Dialog {
        val builder = AlertDialog.Builder(context)

        builder.setView(this.builderView)
                .setPositiveButton(R.string.ok, { dialog, which ->
                    if (this.builderView.uxDeliveryList.text.isNotBlank()) {
                        listener?.onDeliveryListEntered(this.builderView.uxDeliveryList.text.toString())
                    }
                })
                .setNeutralButton(R.string.continue_without, { dialog, which ->
                    //Continue without DeliveryList
                    listener?.onDeliveryListSkipped()
                })
                .setNegativeButton(R.string.cancel, { dialog, which ->
                    //Abort
                    listener?.onCanceled()
                })
                .setCancelable(false)
        return builder.create()
    }

    override fun onResume() {

        aidcReader.decoders.set(
                Ean8Decoder(true),
                Ean13Decoder(true),
                Interleaved25Decoder(true, 11, 12),
                DatamatrixDecoder(true),
                Code128Decoder(true)
        )

        aidcReader.readEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    log.info("Barcode scanned ${it.data}")
                    processScannedData(it.data)
                }

        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    fun processScannedData(data: String) {
        this.builderView.uxDeliveryList.setText(data)
    }

    interface OnDialogResultListener {
        fun onDeliveryListEntered(listId: String)
        fun onDeliveryListSkipped()
        fun onCanceled()
    }
}