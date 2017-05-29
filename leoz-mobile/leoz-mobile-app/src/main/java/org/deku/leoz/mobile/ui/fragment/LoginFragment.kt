package org.deku.leoz.mobile.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_login.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.device.Tone
import org.deku.leoz.mobile.model.Login
import org.slf4j.LoggerFactory
import sx.android.aidc.*
import sx.rx.toSingletonObservable
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_login, container, false)
        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.uxMailaddress.requestFocus()

        // On-the-fly validations

        RxView.focusChanges(this.uxMailaddress)
                .filter { it == false }
                .subscribe {
                    this.validateMailAddress()
                }

        // Clear error condition on typing

        RxTextView.textChanges(uxMailaddress).subscribe {
            this.uxMailaddressLayout.error = null
        }

        RxTextView.textChanges(uxPassword).subscribe {
            this.uxPasswordLayout.error = null
        }
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

        // Actions triggering login

        val rxLoginTrigger =
                Observable.merge(listOf(
                        RxView.clicks(this.uxLogin)
                                .map { Unit },
                        RxTextView.editorActions(this.uxPassword)
                                .map { Unit }
                                .replay(1).refCount()
                ))

        rxLoginTrigger
                .switchMap {
                    Observable.fromCallable {
                        // Verify all fields
                        if (listOf(
                                validateMailAddress(),
                                validatePassword()
                        ).any { it == false }) {
                            throw IllegalArgumentException("Validation failed")
                        }
                    }
                }
                .switchMap {
                    login.authenticate(
                            email = uxMailaddress.text.toString(),
                            password = uxPassword.text.toString()
                    )
                }
                .doOnError {
                    tone.beep()
                    log.error(it.message)
                }
                .retry()
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    log.info("Login successful $it")
                }
    }

    private fun String.isValidEmailAddress(): Boolean {
        return try {
            InternetAddress(this).validate()
            true
        } catch (ex: AddressException) {
            false
        }
    }

    private fun validateMailAddress(): Boolean {
        return when {
            uxMailaddress.text.isEmpty() -> {
                this.uxMailaddressLayout.error = getString(R.string.error_empty_field)
                false
            }
            !uxMailaddress.text.toString().isValidEmailAddress() && !uxMailaddress.text.matches(internalLoginRegex) -> {
                this.uxMailaddressLayout.error = getString(R.string.error_format_mail)
                false
            }
            else -> {
                this.uxMailaddressLayout.error = null
                true
            }
        }
    }

    private fun validatePassword(): Boolean {
        return when {
            this.uxPassword.text.isEmpty() -> {
                this.uxPasswordLayout.error = getString(R.string.error_empty_field)
                false
            }
            else -> {
                this.uxPasswordLayout.error = null
                true
            }
        }
    }
}