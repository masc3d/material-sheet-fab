package org.deku.leoz.mobile.prototype.activities.smallsort

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.activity_proto__main.*
import kotlinx.android.synthetic.main.activity_proto_sso_menue.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.prototype.activities.Proto_StatusFragment
import org.slf4j.LoggerFactory

class Proto_sso_Menue : RxAppCompatActivity(), View.OnClickListener {

    val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proto_sso_menue)
        btnBagDifference.setOnClickListener(this)
        btnBagIncoming.setOnClickListener(this)
        btnBagInitialize.setOnClickListener(this)
        btnBagOutgoing.setOnClickListener(this)
        btnBagSort.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val mIntent : Intent
        when (v){
            btnBagDifference -> {
                Snackbar.make(v, getString(R.string.hint_not_available), Snackbar.LENGTH_SHORT).show()
            }
            btnBagIncoming -> {
                Snackbar.make(v, getString(R.string.hint_not_available), Snackbar.LENGTH_SHORT).show()
            }
            btnBagInitialize -> {
                Snackbar.make(v, getString(R.string.hint_not_available), Snackbar.LENGTH_SHORT).show()
            }
            btnBagOutgoing -> {
                mIntent = Intent(applicationContext, Proto_sso_Outgoing::class.java)
                startActivity(mIntent)
            }
            btnBagSort -> {
                Snackbar.make(v, getString(R.string.hint_not_available), Snackbar.LENGTH_SHORT).show()
            }
            else -> {
                log.debug("OnClick Event unhandled [$v]")
                Snackbar.make(v, getString(R.string.hint_not_available), Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}
