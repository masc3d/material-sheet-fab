package org.deku.leoz.mobile.prototype.activities

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import kotlinx.android.synthetic.main.activity_proto__main.*

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.prototype.activities.smallsort.Proto_sso_Menue
import org.slf4j.LoggerFactory

class Proto_MainActivity : AppCompatActivity(), View.OnClickListener, ProtoStatusFragment.OnFragmentInteractionListener {

    val log by lazy { LoggerFactory.getLogger(this.javaClass) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proto__main)
        btnProtoHUB.setOnClickListener(this)
        btnProtoSSO.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val mIntent : Intent
        when (v){
            btnProtoHUB -> {
                //mIntent = Intent(applicationContext, this.javaClass)
                //startActivity(mIntent)
                Snackbar.make(v, "Not implemented yet", Snackbar.LENGTH_SHORT).show()
            }
            btnProtoSSO -> {
                mIntent = Intent(applicationContext, Proto_sso_Menue::class.java)
                startActivity(mIntent)
            }
            else -> {
                log.debug("OnClick Event unhandled [$v]")
                Snackbar.make(v, "Not implemented yet", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onFragmentInteraction(uri: Uri?) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
