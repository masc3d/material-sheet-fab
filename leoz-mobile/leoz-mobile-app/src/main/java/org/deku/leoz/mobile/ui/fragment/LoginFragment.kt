package org.deku.leoz.mobile.ui.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.fragment_login.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.device.Tone
import org.deku.leoz.mobile.model.Login
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
    private val login: Login by Kodein.global.lazy.instance()

    interface OnLoginSuccessfulListener {
        fun onLoginSuccessful(userAlias: String, userStation: String)
    }

    //TODO: To be extracted / needs to be generic
    private fun showProgress(show: Boolean) {
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)

        val view = this.formContainer

        view.visibility = if (show) View.GONE else View.VISIBLE
        view.animate().setDuration(shortAnimTime.toLong()).alpha(
                (if (show) 0 else 1).toFloat()).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                view.visibility = if (show) View.GONE else View.VISIBLE
            }
        })

        this.progress.visibility = if (show) View.VISIBLE else View.GONE
        this.progress.animate().setDuration(shortAnimTime.toLong()).alpha(
                (if (show) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                progress.visibility = if (show) View.VISIBLE else View.GONE
            }
        })
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_login, container, false)
        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.uxLogin.setOnClickListener(onClickListener)
        this.uxPassword.setOnEditorActionListener { v, actionId, event -> login()}

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
            showProgress(true)

            login.authenticate(
                    email = mailAddress,
                    password = password
            )
                    .bindUntilEvent(this, FragmentEvent.PAUSE)
                    .subscribe {
                        showProgress(false)
                        if (it != null && it.hash.isNotBlank()) {
                            //Login succeeded
                        } else {
                            //Login failed
                            tone.beep()
                        }
                    }
        }

        return true
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