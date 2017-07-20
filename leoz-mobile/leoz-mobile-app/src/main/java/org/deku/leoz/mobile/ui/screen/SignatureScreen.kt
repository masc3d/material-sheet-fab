package org.deku.leoz.mobile.ui.screen

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.InputType
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
import org.deku.leoz.mobile.model.entity.Stop
import org.deku.leoz.mobile.model.entity.StopEntity
import org.deku.leoz.mobile.ui.Fragment
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.inflateMenu
import org.deku.leoz.mobile.ui.view.ActionItem
import org.deku.leoz.model.EventDeliveredReason
import org.parceler.Parcel
import org.parceler.ParcelConstructor
import org.slf4j.LoggerFactory
import kotlin.properties.Delegates

/**
 * A simple [Fragment] subclass.
 */
class SignatureScreen
    :
        ScreenFragment(),
        SignaturePad.OnSignedListener {

    @Parcel(Parcel.Serialization.BEAN)
    class Parameters @ParcelConstructor constructor (
        var stopId: Int,
        var deliveryReason: EventDeliveredReason = EventDeliveredReason.Normal,
        var recipient: String
    )

    companion object {
        /**
         * Factory method
         */
        fun create(parameters: Parameters): SignatureScreen {
            val s = SignatureScreen()
            s.parameters = parameters
            return s
        }
    }

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val db: Database by Kodein.global.lazy.instance()

    private val listener by lazy { this.activity as? Listener }

    var parameters by fragmentParameters<Parameters>()

    private val descriptionText: String by lazy {
        "Aufträge: ${stop.tasks.map { it.order }.distinct().count()}\nPakete: X\nEmpfänger: ${stop!!.address.line1}\nAngenommen von: ${this.parameters.recipient}"
    }

    private val stop: Stop by lazy {
        db.store.select(StopEntity::class)
                .where(StopEntity.ID.eq(this.parameters.stopId))
                .get()
                .first()
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


        this.uxConclusion.text = descriptionText

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

        if (this.parameters.recipient.isNullOrEmpty()) {
            val dialog = MaterialDialog.Builder(context)
                    .title("Recipient")
                    .cancelable(false)
                    .content("Wer hat die Sendung(en) angenommen?")
                    .inputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME)
                    .input("Max Mustermann", null, false, { _, charSequence ->
                        this.parameters.recipient = charSequence.toString()
                        this.uxConclusion.text = descriptionText
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
