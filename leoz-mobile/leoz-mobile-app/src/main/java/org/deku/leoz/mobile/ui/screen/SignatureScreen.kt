package org.deku.leoz.mobile.ui.screen

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.github.gcacace.signaturepad.views.SignaturePad
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.screen_signature.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.entity.Stop
import org.deku.leoz.mobile.ui.Fragment
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.inflateMenu
import org.deku.leoz.mobile.ui.view.ActionItem
import org.deku.leoz.model.EventDeliveredReason
import org.slf4j.LoggerFactory

/**
 * A simple [Fragment] subclass.
 */
class SignatureScreen
    :
        ScreenFragment(),
        SignaturePad.OnSignedListener {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val listener by lazy { this.activity as? Listener }
    private var descriptionText: String = ""
    private var recipient: String = ""
    private var deliveryReason: EventDeliveredReason = EventDeliveredReason.Normal
    private var stop: Stop? = null

    companion object {
        fun create(deliveryReason: EventDeliveredReason, stop: Stop, recipient: String = ""): SignatureScreen {
            val s = SignatureScreen()
            s.deliveryReason = deliveryReason
            s.stop = stop
            s.recipient = recipient
            return s
        }

        enum class SaveInstanceBundleKeys(val key: String) {
            DESCRIPTION_TEXT("DESCRIPTION_TEXT")
        }
    }

    interface Listener {
        fun onSignatureCancelled()
        fun onSignatureSubmitted()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        this.hideActionBar = true
        this.retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.screen_signature, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null)
            if (savedInstanceState.containsKey(SaveInstanceBundleKeys.DESCRIPTION_TEXT.key)) {
                this.descriptionText = savedInstanceState.getString(SaveInstanceBundleKeys.DESCRIPTION_TEXT.key)
            }

        setText()

        this.uxSignaturePad.setOnSignedListener(this)

        this.actionItems = listOf(
                ActionItem(
                        id = R.id.action_signature_submit,
                        colorRes = R.color.colorGreen,
                        iconRes = R.drawable.ic_check_circle
                ),
                ActionItem(
                        id = R.id.action_signature_clear,
                        colorRes = R.color.colorGrey,
                        iconRes = R.drawable.ic_circle_cancel
                ),
                ActionItem(
                        id = R.id.action_signature_cancel,
                        colorRes = R.color.colorRed,
                        iconRes = R.drawable.ic_cancel_black,
                        menu = this.activity.inflateMenu(R.menu.menu_signature_exception)
                )
        )

        if (recipient.isNullOrEmpty()) {
            val dialog = MaterialDialog.Builder(context)
                    .title("Recipient")
                    .cancelable(false)
                    .content("Wer hat die Sendung(en) angenommen?")
                    .inputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME)
                    .input("Max Mustermann", null, false, MaterialDialog.InputCallback { materialDialog, charSequence ->
                        this.recipient = charSequence.toString()
                        this.setText()
                    })
                    .show()
        }
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
                                // TODO
//                                stop!!.deliver(reason = deliveryReason, recipient = recipient, signature = this.uxSignaturePad.signatureBitmap)
                                this.listener?.onSignatureSubmitted()
                            }
                        }
                        R.id.action_signature_clear -> {
                            //Clear signature pad
                            this.uxSignaturePad.clear()
                        }
                        R.id.ux_action_signature_cancel -> {
                            //Cancel process
                            this.listener?.onSignatureCancelled()
                        }
                    }
                }
    }

    private fun setText() {
        this.descriptionText = "Aufträge: ${stop!!.tasks.map { it.order }.distinct().count()}\nPakete: X\nEmpfänger: ${stop!!.address.line1}\nAngenommen von: $recipient"
        this.uxConclusion.text = descriptionText
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(SaveInstanceBundleKeys.DESCRIPTION_TEXT.key, this.descriptionText)
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
