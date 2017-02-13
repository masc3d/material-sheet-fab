package org.deku.leoz.mobile.prototype.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import com.honeywell.aidc.AidcManager
import com.honeywell.aidc.BarcodeReader
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.activity_proto__main.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.prototype.activities.smallsort.Proto_sso_Menue
import org.slf4j.LoggerFactory

class Proto_MainActivity : RxAppCompatActivity(), View.OnClickListener, Proto_StatusFragment.OnFragmentInteractionListener {

    val log by lazy { LoggerFactory.getLogger(this.javaClass) }
    //var barcodeReader: BarcodeReader? = null

    companion object {
        var manager: AidcManager? = null
        var barcodeReader: BarcodeReader? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proto__main)
        btnProtoHUB.setOnClickListener(this)
        btnProtoSSO.setOnClickListener(this)

        AidcManager.create(applicationContext, object: AidcManager.CreatedCallback {
            override fun onCreated(p0: AidcManager) {
                Proto_MainActivity.manager = p0
                Proto_MainActivity.barcodeReader = manager!!.createBarcodeReader()
            }

        })
    }

    override fun onClick(v: View) {
        val mIntent : Intent
        when (v){
            btnProtoHUB -> {
                Snackbar.make(v, getString(R.string.hint_not_available), Snackbar.LENGTH_SHORT).show()
            }
            btnProtoSSO -> {
                mIntent = Intent(applicationContext, Proto_sso_Menue::class.java)
                startActivity(mIntent)
            }
            else -> {
                log.debug("OnClick Event unhandled [$v]")
                Snackbar.make(v, getString(R.string.hint_not_available), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onFragmentInteraction(uri: Uri?) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
