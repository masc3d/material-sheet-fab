package org.deku.leoz.mobile.model

import android.net.NetworkInfo
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.*
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import org.deku.leoz.hashUserPassword
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.DebugSettings
import org.deku.leoz.mobile.data.requery.UserEntity
import org.deku.leoz.service.internal.AuthorizationService
import org.slf4j.LoggerFactory
import sx.android.Connectivity
import sx.android.Device
import sx.rx.ObservableRxProperty
import sx.rx.toHotReplay
import sx.text.parseHex
import java.net.SocketTimeoutException
import java.util.*

/**
 * Login model
 * Created by n3 on 27.04.17.
 */
class Login {
    private val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        val DEV_ID = 0
        val DEV_EMAIL = "dev@leoz"
        val DEV_PASSWORD = "password"
    }

    private val connectivity: Connectivity by Kodein.global.lazy.instance()
    private val device: Device by Kodein.global.lazy.instance()
    private val debugSettings: DebugSettings by Kodein.global.lazy.instance()

    private val db: Database by Kodein.global.lazy.instance()

    private val authService: AuthorizationService by Kodein.global.lazy.instance()

    /**
     * SALT for hashing passwords locally
     */
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
     * @return Hot observable
     */
    fun authenticate(email: String, password: String): Observable<User> {
        val task = Observable.fromCallable {
            val hashedPassword = hashUserPassword(
                    salt = SALT,
                    email = email,
                    password = password
            )

            fun authorizeOnline(): User {
                log.info("Authorizing user [${email}] online")

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

                val authResponse = authService.authorizeMobile(request)

                val user = User(
                        id = authResponse.user?.id!!,
                        email = email,
                        apiKey = authResponse.key
                )

                // Store user in database
                val rUser = UserEntity()
                rUser.id = user.id
                rUser.email = user.email
                rUser.password = hashedPassword
                rUser.apiKey = authResponse.key
                db.store.upsert(rUser).blockingGet()

                return user
            }

            fun authorizeOffline(): User {
                log.info("Authorizing user [${email}] offline")

                val rUser = db.store.select(UserEntity::class)
                        .where(UserEntity.EMAIL.eq(email))
                        .get()
                        .firstOrNull()

                if (rUser == null)
                    throw NoSuchElementException("User [${email}] not found, offline login not applicable")

                return User(
                        id = rUser.id,
                        email = rUser.email,
                        apiKey = rUser.apiKey)
            }

            if (connectivity.network.state == NetworkInfo.State.CONNECTED) {
                try {
                    authorizeOnline()
                } catch(e: Throwable) {
                    // Falling back to offline login on specific conditions, like network timeouts
                    // There should not be a generic fallback, as the exception may also indicate
                    // a user cannot authorize due to being disabled or non-existent.
                    when (e.cause) {
                        is SocketTimeoutException -> {
                            authorizeOffline()
                        }
                        else -> throw e
                    }
                }
            } else {
                authorizeOffline()
            }
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    // Store authenticated user in property
                    this.authenticatedUser = it
                }
                .doOnError {
                    log.error(it.message, it)
                }
                .toHotReplay()

        // Return task to consumer for optionally subscribing to running authentication task as well
        return task
    }

    fun logout() {
        this.authenticatedUser = null
    }
}