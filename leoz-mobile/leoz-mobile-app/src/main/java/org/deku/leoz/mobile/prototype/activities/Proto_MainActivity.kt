package org.deku.leoz.mobile.prototype.activities

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Button
import com.honeywell.aidc.AidcManager
import com.honeywell.aidc.BarcodeReader
import kotlinx.android.synthetic.main.activity_proto__main.*

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.prototype.activities.smallsort.Proto_sso_Menue
import org.slf4j.LoggerFactory

class Proto_MainActivity : AppCompatActivity(), View.OnClickListener, Proto_StatusFragment.OnFragmentInteractionListener {

    val log by lazy { LoggerFactory.getLogger(this.javaClass) }
    var manager: AidcManager? = null
    var barcodeReader: BarcodeReader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proto__main)
        btnProtoHUB.setOnClickListener(this)
        btnProtoSSO.setOnClickListener(this)
//        AidcManager.create(this) { aidcManager ->
//            manager = aidcManager
//            barcodeReader = manager!!.createBarcodeReader()
//        }
        AidcManager.create(applicationContext, object: AidcManager.CreatedCallback {
            override fun onCreated(p0: AidcManager) {
                manager = p0
                barcodeReader = manager!!.createBarcodeReader()
            }

        })
    }

    override fun onClick(v: View) {
        val mIntent : Intent
        when (v){
            btnProtoHUB -> {
                //mIntent = Intent(applicationContext, this.javaClass)
                //startActivity(mIntent)
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

    fun getBarcodeObject(): BarcodeReader {
        return barcodeReader!!
    }
}
