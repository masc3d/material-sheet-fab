package org.deku.leoz.mobile.prototype.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import kotlinx.android.synthetic.main.proto_fragment_main.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.prototype.activities.smallsort.ProtoSsoActivity
import org.deku.leoz.mobile.ui.activity.Activity
import org.deku.leoz.mobile.ui.fragment.MainFragment
import org.slf4j.LoggerFactory
import sx.android.fragment.util.withTransaction

class ProtoMainActivity : Activity() {
    private val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.supportFragmentManager.withTransaction {
            it.replace(R.id.uxContainer, ProtoMainFragment())
        }
    }
}
