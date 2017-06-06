package org.deku.leoz.mobile.ui.dialog

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import kotlinx.android.synthetic.main.main_content.*
import org.deku.leoz.mobile.model.Stop
import org.deku.leoz.mobile.ui.fragment.DeliveryProcessFragment
import sx.android.fragment.util.withTransaction

/**
 * Created by phpr on 29.05.2017.
 */
class StopListDialog(val selectedStop: Stop): Dialog() {
    override fun onCreateDialog(savedInstanceState: Bundle?): android.app.Dialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose an action")
                .setItems(arrayOf("Navigation", "Contact", "Open"), DialogInterface.OnClickListener { dialog, which ->
                    when(which) {
                        //Navigation
                        0 -> {
                            val intent: Intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("google.navigation:q=${selectedStop.address.street}+${selectedStop.address.streetNo}+${selectedStop.address.city}+${selectedStop.address.zipCode}&mode=d")
                            )
                            startActivity(intent)
                        }
                        //Contact
                        1 -> {

                        }
                        //Open
                        2 -> {
                            activity.supportFragmentManager.withTransaction {
                                it.replace(activity.uxContainer.id, DeliveryProcessFragment(selectedStop))
                            }
                        }
                    }

                })
        return builder.create()
    }
}