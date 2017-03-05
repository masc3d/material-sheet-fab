package org.deku.leoz.mobile.prototype.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import kotlinx.android.synthetic.main.proto_activity_main.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.prototype.activities.smallsort.ProtoSsoActivity
import org.deku.leoz.mobile.ui.activity.Activity
import org.slf4j.LoggerFactory

class ProtoMainActivity : Activity(), View.OnClickListener {

    private val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.proto_activity_main)
        uxHubButton.setOnClickListener(this)
        uxSsoButton.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val mIntent: Intent
        when (v) {
            uxHubButton -> {
                Snackbar.make(v, getString(R.string.hint_not_available), Snackbar.LENGTH_SHORT).show()
            }
            uxSsoButton -> {
                mIntent = Intent(applicationContext, ProtoSsoActivity::class.java)
                startActivity(mIntent)
            }
            else -> {
                log.debug("OnClick Event unhandled [$v]")
                Snackbar.make(v, getString(R.string.hint_not_available), Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}
