package org.deku.leoz.mobile

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog

/**
 * Alert methods
 * Created by n3 on 18/02/2017.
 */
fun Context.showErrorAlert(text: String,
                           title: Int = R.string.error_fatal,
                           positiveButtonText: Int = android.R.string.ok,
                           onPositiveButton: ((dialogInterface: DialogInterface) -> Unit) = {}) {
    AlertDialog.Builder(this)
            .setTitle(title)
            .setIcon(R.mipmap.ic_launcher)
            .setPositiveButton(positiveButtonText, { dialogInterface, i ->
                onPositiveButton(dialogInterface)
            })
            .setMessage(text)
            .show()
}