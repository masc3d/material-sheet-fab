package org.deku.leoz.mobile.ui.screen

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.github.gcacace.signaturepad.views.SignaturePad
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.screen_signature.*
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.entity.address
import org.deku.leoz.mobile.model.entity.Stop
import org.deku.leoz.mobile.model.process.Delivery
import org.deku.leoz.mobile.model.repository.StopRepository
import org.deku.leoz.mobile.ui.Fragment
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.extension.inflateMenu
import org.deku.leoz.mobile.ui.view.ActionItem
import org.deku.leoz.model.EventDeliveredReason
import org.parceler.Parcel
import org.parceler.ParcelConstructor
import org.slf4j.LoggerFactory
import sx.android.toBase64
import sx.android.toBitmap

/**
 * A simple [Fragment] subclass.
 */
class SignatureScreen
    :
        ScreenFragment<SignatureScreen.Parameters>(),
        SignaturePad.OnSignedListener {

    @Parcel(Parcel.Serialization.BEAN)
    class Parameters @ParcelConstructor constructor(
            var stopId: Int,
            var deliveryReason: EventDeliveredReason = EventDeliveredReason.NORMAL,
            var recipient: String
    )

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val db: Database by Kodein.global.lazy.instance()
    private val stopRepository: StopRepository by Kodein.global.lazy.instance()
    private val delivery: Delivery by Kodein.global.lazy.instance()

    private val listener by lazy { this.activity as? Listener }

    private val descriptionText: String by lazy {
        this@SignatureScreen.getString(R.string.signature_conclusion, stop.tasks.map { it.order }.distinct().count(), delivery.activeStop?.deliveredParcelAmount?.blockingFirst(), stop.address.line1)
    }

    private val stop: Stop by lazy {
        stopRepository.findById(this.parameters.stopId)
                ?: throw IllegalArgumentException("Illegal stop id [${this.parameters.stopId}]")
    }

    interface Listener {
        fun onSignatureCancelled()
        fun onSignatureSubmitted(signatureSvg: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        this.toolbarHidden = true
        this.flipScreen = true
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.screen_signature, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.uxConclusion.text = descriptionText
        this.uxRecipient.text = this.getString(R.string.signature_signed_by_name, this.parameters.recipient)
        this.uxSignaturePad.setOnSignedListener(this)

        if (savedInstanceState != null) {
            this.uxSignaturePad.signatureBitmap = savedInstanceState.getString("BITMAP").toBitmap()
        }

        this.actionItems = listOf(
                ActionItem(
                        id = R.id.action_signature_submit,
                        colorRes = R.color.colorGreen,
                        iconRes = R.drawable.ic_check_circle
                ),
                ActionItem(
                        id = R.id.action_signature_cancel,
                        colorRes = R.color.colorRed,
                        alignEnd = false,
                        iconRes = R.drawable.ic_cancel_black,
                        menu = this.activity.inflateMenu(R.menu.menu_signature_exception)
                ),
                ActionItem(
                        id = R.id.action_signature_clear,
                        colorRes = R.color.colorLightGrey,
                        alignEnd = false,
                        iconRes = R.drawable.ic_circle_cancel
                ),
                ActionItem(
                        id = R.id.action_signature_paper,
                        colorRes = R.color.colorAccent,
                        alignEnd = false,
                        iconRes = R.drawable.ic_menu_camera
                )
        )
    }

    override fun onResume() {
        super.onResume()

        this.activity.actionEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    when (it) {
                        R.id.action_signature_submit -> {
                            //Submit signature, finish process (delivery only)
                            if (this.uxSignaturePad.isEmpty) {
                                val dialog = MaterialDialog.Builder(context)
                                        .title(getString(R.string.title_missing_signature))
                                        .content(getString(R.string.dialog_text_missing_signature))
                                        .negativeText(getString(R.string.action_retry))
                                        .positiveText(getString(R.string.signed_on_paper))
                                        .cancelable(false)
                                        .onPositive { materialDialog, dialogAction ->
                                            //TODO go to "Paper signature" process
                                        }
                                dialog.show()
                            } else {
                                this.listener?.onSignatureSubmitted(this.uxSignaturePad.signatureSvg)
                            }
                        }
                        R.id.action_signature_clear -> {
                            //Clear signature pad
                            this.uxSignaturePad.clear()
                        }
                        R.id.action_signature_cancel -> {
                            //Cancel process
                            this.listener?.onSignatureCancelled()
                        }
                    }
                }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putString("BITMAP", this.uxSignaturePad.signatureBitmap.toBase64())
        super.onSaveInstanceState(outState)
    }

    // SignaturePad listeners
    override fun onStartSigning() {
        log.debug("ONSTARTSIGNING")
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClear() {
        log.debug("ONCLEAR")
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSigned() {
        log.debug("ONSIGNED")
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}// Required empty public constructor
