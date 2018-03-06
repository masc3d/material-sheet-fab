package org.deku.leoz.mobile.ui.process

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_login.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.log.user
import org.deku.leoz.mobile.model.entity.User
import org.deku.leoz.mobile.model.process.Login
import org.deku.leoz.mobile.ui.core.Fragment
import org.deku.leoz.mobile.ui.dialog.PrivacyDisclaimerDialog
import org.jetbrains.anko.inputMethodManager
import org.slf4j.LoggerFactory
import sx.android.hideSoftInput
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress

/**
 * Login fragment
 * Created by n3 on 26/02/2017.
 */
class LoginFragment : Fragment<Any>() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val internalLoginRegex: Regex = Regex(pattern = "^276[0-9]{5}$")
    private val login: Login by Kodein.global.lazy.instance()

    private var privacyDisclaimerAccepted = false

    private val privacyResultSubject: PublishSubject<Date> = PublishSubject.create()

    interface Listener {
        /** Called when it's appropriate to show progress indication */
        fun onLoginPending() {}

        fun onLoginFailed() {}
        fun onLoginSuccessful() {}

        fun onPrivacyRejected() {}
    }

    private val listener by lazy { this.activity as? Listener }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_login, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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

        data class State(
                val pending: Boolean = false,
                val result: User? = null
        )

        // Actions triggering login

        val rxPrivacyTrigger =
                Observable.merge(listOf(
                        RxTextView.editorActions(this.uxPassword)
                                .map { Unit }
                                .replay(1)
                                .refCount()
                                .doOnNext {
                                    this.context.inputMethodManager.hideSoftInput()
                                },
                        this.syntheticLoginSubject
                ))

        val rxLoginTrigger =
                Observable.merge(listOf(
                        this.privacyResultSubject
                ))

        rxPrivacyTrigger
                .observeOn(AndroidSchedulers.mainThread())
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
                .subscribe {
                    queryPrivacyConfirmation()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    {
                                        if (!privacyDisclaimerAccepted)
                                            this.listener?.onPrivacyRejected()
                                        else
                                            privacyResultSubject.onNext(Date())
                                    },
                                    {

                                    }
                            )
                }

        rxLoginTrigger
                .observeOn(AndroidSchedulers.mainThread())
                .switchMap {
                    login.authenticate(
                            email = uxMailaddress.text.toString(),
                            password = uxPassword.text.toString()
                    )
                            .map {
                                // Map success result to state
                                State(pending = false, result = it)
                            }
                            // Merge delayed pending state
                            .mergeWith(Observable
                                    .just(State(pending = true))
                                    .delay(250, TimeUnit.MILLISECONDS)
                            )
                            // Complete this observable on success result
                            .takeUntil {
                                it.pending == false
                            }
                }
                .doOnError {
                    log.error(it.message, it)

                    log.user { "Fails to login [${it.message}]" }

                    this.view?.post {
                        this.listener?.onLoginFailed()
                    }
                }
                // Retrying the entire observable (including required triggers, eg. user input)
                .retry()
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when {
                        it.pending == true -> {
                            this.listener?.onLoginPending()
                        }
                        else -> {
                            log.user { "Logged in successfully [$it]" }
                            this.listener?.onLoginSuccessful()
                        }
                    }
                }
    }

    private fun String.isValidEmailAddress(): Boolean {
        return try {
            InternetAddress(this).validate()
            true
        } catch (e: AddressException) {
            false
        }
    }

    private fun validateMailAddress(): Boolean = when {
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

    private fun validatePassword(): Boolean = when {
        this.uxPassword.text.isEmpty() -> {
            this.uxPasswordLayout.error = getString(R.string.error_empty_field)
            false
        }
        else -> {
            this.uxPasswordLayout.error = null
            true
        }
    }

    private val syntheticLoginSubject = PublishSubject.create<Unit>()

    /**
     * Synthesizes login by adding a user/password into the referring field
     * and triggering the login process
     * @param email User mail address
     * @param password Password
     */
    fun synthesizeLogin(email: String, password: String) {
        this.uxMailaddress.setText(email)
        this.uxPassword.setText(password)
        this.syntheticLoginSubject.onNext(Unit)
    }

    private fun queryPrivacyConfirmation(): Completable {
        return Completable.create {
            val completable = it

            privacyDisclaimerAccepted = false

            MaterialDialog.Builder(this.context).also {
                it.title(R.string.data_protection)
                it.checkBoxPrompt("Ich akzeptiere die Erklärung", false, { _, checked ->
                    privacyDisclaimerAccepted = checked
                })
                it.content(R.string.privacy_disclaimer_text)
                it.cancelable(false)
                it.positiveText(R.string.proceed)
                it.negativeText(R.string.cancel)
                it.onNegative { dialog, which ->
                    completable.onError(IllegalStateException("Policy must be accepted to continue"))
                }
                it.onPositive { dialog, which ->
                    completable.onComplete()
                }
            }.show()
        }
    }
}