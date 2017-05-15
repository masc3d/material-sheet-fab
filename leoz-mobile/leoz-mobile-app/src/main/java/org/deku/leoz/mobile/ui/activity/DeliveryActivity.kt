package org.deku.leoz.mobile.ui.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_main.uxHead
import kotlinx.android.synthetic.main.main_content.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.ui.fragment.DeliveryFragment
import org.deku.leoz.mobile.ui.fragment.SignatureFragment
import sx.android.fragment.util.withTransaction

/**
 * Created by 27694066 on 09.05.2017.
 */
class DeliveryActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(savedInstanceState == null) {
            supportFragmentManager.withTransaction {
                it.replace(this.uxContainer.id, DeliveryFragment())
            }

            this.supportActionBar?.setTitle(R.string.delivery)
        }
    }
}