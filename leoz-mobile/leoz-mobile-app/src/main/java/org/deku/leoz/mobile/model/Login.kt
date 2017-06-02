package org.deku.leoz.mobile.model

import android.net.NetworkInfo
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.*
import com.github.salomonbrys.kodein.genericInstance
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
import sx.text.parseHex
import java.util.*

/**
 * Login model
 * Created by n3 on 27.04.17.
 */
class Login {
    private val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        val DEV_EMAIL = "dev@leoz"
        val DEV_PASSWORD = "password"
    }

    private val connectivity: Connectivity by Kodein.global.lazy.instance()
    private val device: Device = Kodein.global.instance()
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
     */
    fun authenticate(email: String, password: String): Observable<User> {
        val task = Observable.fromCallable {
            val user: User

            val hashedPassword = hashUserPassword(
                    salt = SALT,
                    email = email,
                    password = password
            )

            // Debug/dev supoort for development login
            if (debugSettings.enabled && email == DEV_EMAIL && password == DEV_PASSWORD) {
                user = User(
                        email = DEV_EMAIL,
                        apiKey = UUID.randomUUID().toString())
            } else {
                if (connectivity.network.state == NetworkInfo.State.CONNECTED) {
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

                    user = User(
                            email = email,
                            apiKey = authResponse.key
                    )

                    // Store user in database
                    val rUser = UserEntity()
                    rUser.email = email
                    rUser.password = hashedPassword
                    rUser.apiKey = authResponse.key
                    db.store.upsert(rUser).blockingGet()

                } else {
                    log.info("Authorizing user [${email}] offline")

                    val rUser = db.store.select(UserEntity::class)
                            .where(UserEntity.EMAIL.eq(email))
                            .get()
                            .firstOrNull()

                    if (rUser == null)
                        throw NoSuchElementException("User [${email}] not found, offline login not applicable")

                    user = User(email = rUser.email, apiKey = rUser.apiKey)
                }
            }

            user
        }
                .cache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

        // Subscribing to task will actually start it
        task.subscribeBy(
                onNext = {
                    // Store authenticated user in property
                    this.authenticatedUser = it
                }, onError = {})

        // Return task to consumer for optionally subscribing to running authentication task as well
        return task
    }

    fun logout() {
        this.authenticatedUser = null
    }
}