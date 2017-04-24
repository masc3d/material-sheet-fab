package org.deku.leoz.mobile.ui.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.components.support.RxAppCompatDialogFragment
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.main_content.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.config.RestClientConfiguration
import org.deku.leoz.mobile.device.Tone
import org.slf4j.LoggerFactory
import sx.android.Device
import sx.android.aidc.*
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress

/**
 * Login fragment
 * Created by n3 on 26/02/2017.
 */
class LoginFragment : Fragment() {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val aidcReader: AidcReader by Kodein.global.lazy.instance()
    private val tone: Tone by Kodein.global.lazy.instance()
    private val internalLoginRegex: Regex = Regex(pattern = "^276[0-9]{5}$")
    private lateinit var listenerCallback: OnLoginSuccessfulListener

    interface OnLoginSuccessfulListener {
        fun onLoginSuccessful(userAlias: String, userStation: String)
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)

        try {
            listenerCallback = activity as OnLoginSuccessfulListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement OnLoginSuccessfulListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_login, container, false)
        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val device: Device = Kodein.global.instance()

        this.uxLogin.setOnClickListener(onClickListener)
        this.uxPassword.setOnEditorActionListener { v, actionId, event -> login() }

        //Check fot temporary saved credentials
    }

    override fun onResume() {
        super.onResume()

        aidcReader.decoders.set(
                Ean8Decoder(true),
                Ean13Decoder(true),
                Interleaved25Decoder(true, 11, 12),
                DatamatrixDecoder(true),
                Code128Decoder(true)
        )

        aidcReader.readEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    log.info("Barcode scanned ${it.data}")
                }
    }

    val onClickListener: View.OnClickListener = View.OnClickListener {
        when (it.id) {
            R.id.uxLogin -> {
                log.debug("ONCLICK [$it]")
                login()
            }
            else -> log.warn("ONCLICK unhandled [$it]")
        }
    }

    /**
     *
     */
    fun login(): Boolean {
        var loginSuccessfull = false
        val mailAddress: String
        val password: String

        this.uxMailaddress.error = null
        this.uxPassword.error = null

        if (this.uxMailaddress.text.isEmpty()) {
            this.uxMailaddress.error = getString(R.string.error_empty_field)
        } else if (!isValidEmailAddress(this.uxMailaddress.text.toString()) && !this.uxMailaddress.text.matches(internalLoginRegex)) {
            this.uxMailaddress.error = getString(R.string.error_format_mail)
        } else if (this.uxPassword.text.isEmpty()) {
            this.uxPassword.error = getString(R.string.error_empty_field)
        } else {
            mailAddress = this.uxMailaddress.text.toString()
            password = this.uxPassword.text.toString()

            //Try to login
            if (loginSuccessfull) {
                //Login OK, process to next step

            } else {
                //Login failed.
                this.uxPassword.error = getString(R.string.error_invalid_credentials)
                tone.beep()
            }
        }

        return loginSuccessfull
    }

    fun isValidEmailAddress(email: String): Boolean {
        var result = true
        try {
            val emailAddr = InternetAddress(email)
            emailAddr.validate()
        } catch (ex: AddressException) {
            result = false
        }

        return result
    }
}