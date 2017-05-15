package org.deku.leoz.mobile.ui.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
import org.deku.leoz.mobile.ui.activity.MainActivity
import org.slf4j.LoggerFactory
import sx.android.Device
import sx.android.aidc.*
import sx.android.fragment.util.withTransaction
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
        this.uxPassword.setOnEditorActionListener { v, actionId, event -> login() }

        val arrayAdapter = ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, arrayOf("foo@bar"))

        this.uxMailaddress.setAdapter(arrayAdapter)
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

        when {
            this.uxMailaddress.text.isEmpty() -> {
                this.uxMailaddress.error = getString(R.string.error_empty_field)
            }
            !this.uxMailaddress.text.toString().isValidEmailAddress() && !this.uxMailaddress.text.matches(internalLoginRegex) -> {
                this.uxMailaddress.error = getString(R.string.error_format_mail)
            }
            this.uxPassword.text.isEmpty() -> {
                this.uxPassword.error = getString(R.string.error_empty_field)
            }
            else -> {
                mailAddress = this.uxMailaddress.text.toString()
                password = this.uxPassword.text.toString()

                login.authenticate(
                        email = mailAddress,
                        password = password
                )
                        .bindUntilEvent(this, FragmentEvent.PAUSE)
                        .subscribe {
                            if (it != null && it.hash.isNotBlank()) {
                                //Login succeeded

                            } else {
                                //Login failed
                                tone.beep()
                            }
                        }
            }
        }

        return true
    }

    fun String.isValidEmailAddress(): Boolean {
        return try {
            InternetAddress(this).validate()
            true
        } catch (ex: AddressException) {
            false
        }
    }
}