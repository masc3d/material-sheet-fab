package org.deku.leoz.mobile.model

import android.net.NetworkInfo
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.deku.leoz.hashUserPassword
import org.deku.leoz.identity.Identity
import org.deku.leoz.mobile.BuildConfig
import org.deku.leoz.service.internal.AuthorizationService
import org.slf4j.LoggerFactory
import sx.android.Connectivity
import sx.android.Device
import sx.rx.ObservableRxProperty
import sx.text.parseHex
import java.lang.IllegalStateException
import java.lang.UnsupportedOperationException

/**
 * Login model
 * Created by n3 on 27.04.17.
 */
class Login {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val connectivity: Connectivity by Kodein.global.lazy.instance()
    private val device: Device = Kodein.global.instance()
    private val authService: AuthorizationService by Kodein.global.lazy.instance()
    private val storage: Storage by Kodein.global.lazy.instance()

    private val SALT = "f169bf5444f57fbc4abdd5d089c8395e".parseHex()

    // Consumers can observe this property for changes
    val authenticatedUserProperty = ObservableRxProperty<User?>(null)
    // Delegated property for convenient access
    var authenticatedUser: User? by authenticatedUserProperty

    /**
     * Authenticate user (asnychronously)
     *
     * @param email User email
     * @param password User password
     */
    fun authenticate(email: String, password: String): Observable<User> {
        val task = Observable.fromCallable {
            val authResponse: AuthorizationService.MobileResponse
            val user: User

            val hashedPassword = hashUserPassword(
                    salt = SALT,
                    email = email,
                    password = password
            )

            // Debug/dev supoort for development login
            if (BuildConfig.DEBUG && email == "foo@bar" && password == "foobar") {
                user = User(
                        name = "dev@leoz",
                        hash = "abcdef")
            } else {
                if (connectivity.network.state == NetworkInfo.State.CONNECTED) {
                    log.debug("Connection established, login online.")
                    val request = AuthorizationService.MobileRequest(
                            user = AuthorizationService.Credentials(
                                    email = email,
                                    password = password
                            ),
                            mobile = AuthorizationService.Mobile(
                                    model = device.model.name,
                                    serial = device.serial,
                                    imei = device.imei
                            )
                    )

                    authResponse = authService.authorizeMobile(request)

                    if (authResponse.key.isBlank())
                        throw IllegalStateException()

                    user = User(
                            name = email,
                            hash = hashedPassword
                    )

                    // TODO: needs rework, store in db
                    // Storing credentials (including API-Key) as Identity.
                    // Filename of the stored identity depends on username 'USERNAME.ident'.
                    // Identity.name = hashed password
                    // Identity.key = API key
//                        Identity(authResponse.key, hashedPassword)
//                                .save(storage.dataDir.resolve("$email.ident"))
                } else {
                    throw UnsupportedOperationException()

                    // TODO: needs rework
//                    log.debug("Connectivity not established. Trying offline login.")
//
//                    if (storage.dataDir.resolve("$email.ident").exists()) {
//                        val identity = Identity.load(storage.dataDir.resolve("$email.ident"))
//                        if (hashedPassword == identity.name) {
//                            User(
//                                    name = email,
//                                    hash = hashedPassword
//                            )
//                        }
//                    } else {
//                        throw IllegalArgumentException("Offline login failed: Unknown user.")
//                    }
                }
            }

            user
        }
                .cache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

        // Subscribing to task will actually start it
        task.subscribe {
            // Store authenticated user in property
            this.authenticatedUser = it
        }

        // Return task to consumer for optionally subscribing to running authentication task as well
        return task
    }

    fun logout() {
        this.authenticatedUser = null
    }
}