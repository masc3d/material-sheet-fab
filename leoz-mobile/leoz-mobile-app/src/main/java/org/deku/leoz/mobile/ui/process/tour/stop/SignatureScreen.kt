package org.deku.leoz.mobile.ui.process.tour.stop

import android.content.Context
import android.content.pm.ActivityInfo
import android.databinding.BaseObservable
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.gcacace.signaturepad.views.SignaturePad
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.neovisionaries.i18n.CountryCode
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.screen_tour_stop_signature.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.databinding.ScreenTourStopSignatureBinding
import org.deku.leoz.mobile.model.entity.address
import org.deku.leoz.mobile.model.process.Tour
import org.deku.leoz.mobile.model.process.TourStop
import org.deku.leoz.mobile.resetLocale
import org.deku.leoz.mobile.setLocale
import org.deku.leoz.mobile.ui.core.BaseCameraScreen
import org.deku.leoz.mobile.ui.core.ScreenFragment
import org.deku.leoz.mobile.ui.core.view.ActionItem
import org.deku.leoz.mobile.ui.vm.CounterViewModel
import org.slf4j.LoggerFactory

/**
 * Signature screen
 */
class SignatureScreen
    :
        ScreenFragment<Any>(),
        SignaturePad.OnSignedListener,
        BaseCameraScreen.Listener {

    private val log = LoggerFactory.getLogger(this.javaClass)

    //region Listener
    interface Listener {
        fun onSignatureSubmitted(signatureSvg: String)
        fun onSignatureImageSubmitted(signatureJpeg: ByteArray)
    }

    private val listener by listenerDelegate<Listener>()
    //endregion

    /**
     * Referring stop
     *
     * NOTE: Signaature screen needs to rely on injected tour / active stop, as not all tour stop
     * attributes are stored persistently.
     */
    private val tour: Tour by Kodein.global.lazy.instance()

    private val stop: TourStop by lazy {
        tour.activeStop
                ?: throw IllegalArgumentException("No active tour stop")
    }

    /**
     * Created by masc on 10.07.17.
     */
    class ViewModel(
            val context: Context,
            val stop: TourStop,
            val recipient: String
    ) : BaseObservable() {

        val signatureText by lazy {
            context.getString(R.string.signature_signed_by_name, recipient)
        }

        // masc20180503. context must be injected for temporary locale switch to work
        val deliveredCounter = CounterViewModel(
                context = this.context,
                iconRes = R.drawable.ic_package_variant_closed,
                amount = this.stop.deliveredParcels.map { it.count() }.cast(Number::class.java),
                titleRes = R.string.parcel,
                titlePluralRes = R.string.parcels
        )

        val orderCounter = CounterViewModel(
                context = this.context,
                iconRes = R.drawable.ic_order,
                amount = this.stop.orders.map { it.count() }.cast(Number::class.java),
                titleRes = R.string.order,
                titlePluralRes = R.string.orders
        )

        val damagedCounter = CounterViewModel(
                context = this.context,
                iconRes = R.drawable.ic_damaged,
                iconTintRes = R.color.colorAccent,
                iconAlpha = 0.4F,
                amount = this.stop.damagedParcels.map { it.count() }.cast(Number::class.java),
                titleRes = R.string.damaged
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        this.toolbarHidden = true
        this.statusBarHidden = true
        this.flipScreen = true
        this.lockNavigationDrawer = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        this.context.setLocale(CountryCode.valueOf(this.stop.entity.address.countryCode))

        val binding: ScreenTourStopSignatureBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.screen_tour_stop_signature,
                container, false)

        // Setup bindings
        binding.vm = ViewModel(
                context = this.context,
                stop = this.stop,
                recipient = this.stop.recipientName ?: ""
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                        colorRes = R.color.colorGrey,
                        iconRes = R.drawable.ic_circle_cancel,
                        iconTintRes = android.R.color.white,
                        alignEnd = false,
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
                            this.context.resetLocale()
                            this.listener?.onSignatureSubmitted(this.uxSignaturePad.signatureSvg)
                        }

                        R.id.action_signature_clear -> {
                            this.uxSignaturePad.clear()
                        }

                        R.id.action_signature_paper -> {
                            this.context.resetLocale()
                            this.activity.showScreen(SignOnPaperCameraScreen().also {
                                it.setTargetFragment(this, 0)
                                it.parameters = SignOnPaperCameraScreen.Parameters(
                                        name = this.stop.recipientName ?: ""
                                )
                            })
                        }
                    }
                }
    }

    override fun onPause() {
        super.onPause()
        this.context.resetLocale()
    }

    // SignaturePad listeners
    override fun onStartSigning() {
        this.update()
    }

    override fun onClear() {
        this.update()
    }

    override fun onSigned() {
        this.update()
    }

    override fun onCameraScreenImageSubmitted(sender: Any, jpeg: ByteArray) {
        this.context.resetLocale()
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
