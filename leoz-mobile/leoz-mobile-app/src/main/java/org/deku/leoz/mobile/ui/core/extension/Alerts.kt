package org.deku.leoz.mobile.ui.core.extension

import android.R
import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog

data class AlertButton(val text: Int, val handler: ((DialogInterface) -> Unit) = {})

/**
 * Alert methods
 * Created by n3 on 18/02/2017.
 */
fun Context.showErrorAlert(text: CharSequence,
                           title: CharSequence = this.getText(org.deku.leoz.mobile.R.string.error_fatal),
                           positiveButtonText: Int = R.string.ok,
                           onPositiveButton: ((dialogInterface: DialogInterface) -> Unit)? = null,
                           negativeButtonText: Int = R.string.cancel,
                           onNegativeButton: ((dialogInterface: DialogInterface) -> Unit)? = null) {
    var builder = AlertDialog.Builder(this)
            .setTitle(title)
            .setIcon(org.deku.leoz.mobile.R.drawable.ic_launcher)
    if (onPositiveButton != null) {
        builder = builder.setPositiveButton(positiveButtonText, { dialogInterface, _ ->
            onPositiveButton(dialogInterface)
        })
    }
    if (onNegativeButton != null) {
        builder = builder.setNegativeButton(negativeButtonText, { dialogInterface, _ ->
            onNegativeButton(dialogInterface)
        })
    }
    builder.setMessage(text)
    builder.show()
}

fun Context.showAlert(text: CharSequence,
                      title: CharSequence = this.getText(org.deku.leoz.mobile.R.string.app_name),
                      positiveButton: AlertButton? = null,
                      negativeButton: AlertButton? = null) {
    var builder = AlertDialog.Builder(this)
            .setTitle(title)
            .setIcon(org.deku.leoz.mobile.R.drawable.ic_launcher)

    if (positiveButton != null) {
        builder = builder
                .setPositiveButton(positiveButton.text, { dialogInterface, _ ->
                    positiveButton.handler(dialogInterface)
                })
    }
    if (negativeButton != null) {
        builder = builder.setNegativeButton(negativeButton.text, { dialogInterface, _ ->
            negativeButton.handler(dialogInterface)
        })
    }

    builder.setMessage(text)
    builder.show()
}