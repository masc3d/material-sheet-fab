package org.deku.leoz.mobile.prototype.activities.smallsort

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentTransaction
import android.widget.Button
import android.widget.ImageButton
import kotlinx.android.synthetic.main.proto_fragment_sso.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.prototype.activities.ProtoMainFragment
import org.deku.leoz.mobile.ui.activity.Activity
import org.slf4j.LoggerFactory
import sx.android.fragment.util.withTransaction

class ProtoSsoActivity : Activity() {

    private val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    override fun onCreate(savedInstanceState: Bundle?) {
        log.trace("ONCREATE")
        super.onCreate(savedInstanceState)

        this.supportFragmentManager.withTransaction {
            it.replace(R.id.uxContainer, ProtoSsoFragment())
        }
    }
}
