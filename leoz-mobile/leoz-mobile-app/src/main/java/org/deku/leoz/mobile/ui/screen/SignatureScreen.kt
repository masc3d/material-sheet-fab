package org.deku.leoz.mobile.ui.screen

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import org.deku.leoz.mobile.model.entity.address
import org.deku.leoz.mobile.model.process.Delivery
import org.deku.leoz.mobile.model.repository.StopRepository
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.view.ActionItem
import org.deku.leoz.model.EventDeliveredReason
import org.parceler.Parcel
import org.parceler.ParcelConstructor
import org.slf4j.LoggerFactory

/**
 * Signature screen
 */
class SignatureScreen
    :
        ScreenFragment<SignatureScreen.Parameters>(),
        SignaturePad.OnSignedListener,
        BaseCameraScreen.Listener
{
    interface Listener {
        fun onSignatureSubmitted(signatureSvg: String)
        fun onSignatureImageSubmitted(signatureJpeg: ByteArray)
    }

    @Parcel(Parcel.Serialization.BEAN)
    class Parameters @ParcelConstructor constructor(
            var stopId: Int,
            var deliveryReason: EventDeliveredReason = EventDeliveredReason.NORMAL,
            var recipient: String
    )

    private val listener by lazy {
        this.targetFragment as? Listener
                ?: this.parentFragment as? Listener
                ?: this.activity as? Listener
    }

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val db: Database by Kodein.global.lazy.instance()
    private val stopRepository: StopRepository by Kodein.global.lazy.instance()
    private val delivery: Delivery by Kodein.global.lazy.instance()

    private val descriptionText: String by lazy {
        this@SignatureScreen.getString(R.string.signature_conclusion,
                stop.tasks.map { it.order }.distinct().count().toString(),
                delivery.activeStop?.deliveredParcelAmount?.blockingFirst().toString(),
                stop.address.line1)
    }

    private val stop: Stop by lazy {
        stopRepository
                .findById(this.parameters.stopId)
                .blockingGet()
                ?: throw IllegalArgumentException("Illegal stop id [${this.parameters.stopId}]")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        this.toolbarHidden = true
        this.flipScreen = true
        this.lockNavigationDrawer = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            // Inflate the layout for this fragment
            inflater.inflate(R.layout.screen_signature, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.uxConclusion.text = descriptionText
        this.uxRecipient.text = this.getString(R.string.signature_signed_by_name, this.parameters.recipient)
        this.uxSignaturePad.setOnSignedListener(this)

        this.actionItems = listOf(
                ActionItem(
                        id = R.id.action_signature_submit,
                        colorRes = R.color.colorPrimary,
                        iconRes = R.drawable.ic_finish,
                        iconTintRes = android.R.color.white,
                        visible = false
                ),
                ActionItem(
                        id = R.id.action_signature_paper,
                        colorRes = R.color.colorPrimary,
                        iconRes = R.drawable.ic_menu_camera,
                        iconTintRes = android.R.color.white
                ),
                ActionItem(
                        id = R.id.action_signature_clear,
                        colorRes = R.color.colorAccent,
                        alignEnd = false,
                        iconRes = R.drawable.ic_circle_cancel,
                        iconTintRes = android.R.color.black,
                        visible = false
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
                            this.listener?.onSignatureSubmitted(this.uxSignaturePad.signatureSvg)
                        }
                        R.id.action_signature_clear -> {
                            this.uxSignaturePad.clear()
                        }
                        R.id.action_signature_paper -> {
                            this.activity.showScreen(SignOnPaperCameraScreen().also {
                                it.setTargetFragment(this, 0)
                                it.parameters = SignOnPaperCameraScreen.Parameters(
                                        name = this.parameters.recipient
                                )
                            })
                        }
                    }
                }
    }

    // SignaturePad listeners
    override fun onStartSigning() {
        log.debug("ONSTARTSIGNING")
        this.update()
    }

    override fun onClear() {
        log.debug("ONCLEAR")
        this.update()
    }

    override fun onSigned() {
        log.debug("ONSIGNED")
        this.update()
    }

    override fun onCameraScreenImageSubmitted(sender: Any, jpeg: ByteArray) {
        this.listener?.onSignatureImageSubmitted(jpeg)
    }

    /**
     * Update UI according to current state
     */
    private fun update() {
        val hasValidSignature = !this.uxSignaturePad.isEmpty

        this.actionItems = this.actionItems.apply {
            first { it.id == R.id.action_signature_submit }
                    .visible = hasValidSignature

            first { it.id == R.id.action_signature_paper }
                    .visible = !hasValidSignature

            first { it.id == R.id.action_signature_clear }
                    .visible = hasValidSignature
        }
    }
}
