package org.deku.leoz.mobile.ui.dialog

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.Editable
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.dialog_vehicle_loading.uxDeliveryList
import org.deku.leoz.mobile.R
import org.slf4j.LoggerFactory
import sx.android.aidc.*
import android.view.LayoutInflater



/**
 * Created by phpr on 29.05.2017.
 */
class VehicleLoadingDialog( /* appContext: Context, */ val listener: OnDialogResultListener): Dialog() {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val aidcReader: AidcReader by Kodein.global.lazy.instance()

    override fun onCreateDialog(savedInstanceState: Bundle?): android.app.Dialog {
        val builder = AlertDialog.Builder(context)
        val inflater = activity.layoutInflater
        builder.setTitle("Specify a delivery list")
                .setView(inflater.inflate(R.layout.dialog_vehicle_loading, null))
                .setPositiveButton(R.string.ok, { dialog, which ->
                    if (this.uxDeliveryList.text.isNotBlank()) {
                        listener.onDeliveryListEntered(this.uxDeliveryList.text.toString())
                    }
                })
                .setNeutralButton(R.string.continue_without, { dialog, which ->
                    //Continue without DeliveryList
                    listener.onDeliveryListSkipped()
                })
                .setNegativeButton(R.string.cancel, { dialog, which ->
                    //Abort
                    listener.onCanceled()
                })
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
        /* this.uxDeliveryList.setText(data) not working yet. (null object reference) */
    }

    interface OnDialogResultListener {
        fun onDeliveryListEntered(listId: String)
        fun onDeliveryListSkipped()
        fun onCanceled()
    }
}