package org.deku.leoz.mobile.model.process

import android.content.SharedPreferences
import android.net.NetworkInfo
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.*
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import org.deku.leoz.hashUserPassword
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.SharedPreference
import org.deku.leoz.mobile.model.entity.User
import org.deku.leoz.mobile.model.entity.UserEntity
import org.deku.leoz.mobile.model.entity.create
import org.deku.leoz.mobile.model.repository.OrderRepository
import org.deku.leoz.mobile.model.service.create
import org.deku.leoz.mobile.rx.toHotIoObservable
import org.deku.leoz.service.internal.AuthorizationService
import org.slf4j.LoggerFactory
import sx.android.Connectivity
import sx.android.Device
import sx.android.rx.observeOnMainThread
import sx.rx.ObservableRxProperty
import sx.text.parseHex
import java.net.ConnectException
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
    private val sharedPrefs: SharedPreferences by Kodein.global.lazy.instance()

    private val authService: AuthorizationService by Kodein.global.lazy.instance()

    private val db: Database by Kodein.global.lazy.instance()
    private val orderRepository: OrderRepository by Kodein.global.lazy.instance()

    private val restConfiguration: org.deku.leoz.config.RestClientConfiguration by Kodein.global.lazy.instance()

    /**
     * SALT for hashing passwords locally
     */
    private val SALT = "f169bf5444f57fbc4abdd5d089c8395e".parseHex()

    // Consumers can observe this property for changes
    val authenticatedUserProperty = ObservableRxProperty<User?>(null)

    // Delegated property for convenient access
    var authenticatedUser: User? by authenticatedUserProperty


    /** c'tor */
    init {
        this.authenticatedUserProperty
                .observeOnMainThread()
                .subscribe { user ->
                    // Update rest configuration / API key
                    restConfiguration.apiKey = user.value?.apiKey

                    // Persistently store authenticated user id
                    sharedPrefs.edit().also {
                        it.putInt(SharedPreference.AUTHENTICATED_USER_ID.key, user.value?.id ?: 0)
                        it.apply()
                    }
                }

        // Restore model state
        val store = db.store.toBlocking()

        val authenticatedUserId = sharedPrefs.getInt(SharedPreference.AUTHENTICATED_USER_ID.key, 0)

        if (authenticatedUserId != 0) {
            this.authenticatedUser = store.select(UserEntity::class)
                    .where(UserEntity.ID.eq(authenticatedUserId))
                    .get()
                    .firstOrNull()
        }
    }

    /**
     * Authenticate user (asnychronously)
     *
     * @param email User email
     * @param password User password
     * @return Hot observable
     */
    fun authenticate(email: String, password: String): Observable<User> {

        // Authorization task
        val task = Observable.fromCallable {

            val store = db.store.toBlocking()

            /**
             * Authorize online
             */
            fun authorizeOnline(): User {
                log.info("Authorizing user [${email}] online")

                val request = AuthorizationService.MobileRequest(
                        user = AuthorizationService.Credentials(
                                email = email,
                                password = password
                        ),
                        mobile = AuthorizationService.Mobile.create(device)
                )

                val authResponse = authService.authorizeMobile(request)

                val user = User.create(
                        id = authResponse.user?.id!!,
                        email = email,
                        password = hashUserPassword(
                                salt = SALT,
                                email = email,
                                password = password),
                        apiKey = authResponse.key
                )

                // Store user in database
                return store.upsert(user)
            }

            /**
             * Authorize offline
             */
            fun authorizeOffline(): User {
                log.info("Authorizing user [${email}] offline")

                val user = store.select(UserEntity::class)
                        .where(UserEntity.EMAIL.eq(email))
                        .get()
                        .firstOrNull()

                if (user == null)
                    throw NoSuchElementException("User [${email}] not found, offline login not applicable")

                return user
            }

            // Actual authorization logic
            val user = when {
                (connectivity.network.state == NetworkInfo.State.CONNECTED) -> {
                    try {
                        authorizeOnline()
                    } catch (e: Throwable) {
                        // Falling back to offline login on specific conditions, like network timeouts
                        // There should not be a generic fallback, as the exception may also indicate
                        // a user cannot authorize due to being disabled or non-existent.
                        when (e.cause) {
                            is SocketTimeoutException,
                            is ConnectException -> {
                                log.warn(e.message)
                                authorizeOffline()
                            }
                            else -> throw e
                        }
                    }
                }
                else -> {
                    authorizeOffline()
                }
            }

            // Check for user switch
            val lastActiveUser = store.select(UserEntity::class)
                    .orderBy(UserEntity.LAST_LOGIN_TIME.desc())
                    .get()
                    .first()

            if (lastActiveUser.id != user.id) {
                log.warn("Previously active user [${lastActiveUser.email}], current user [${user.email}]. Removing all data.")

                // Remove all data on user switch
                this.orderRepository.removeAll()
                        .blockingAwait()
            }

            // Update last login time
            user.lastLoginTime = Date()
            store.update(user)

            user
        }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    // Store authenticated user in property
                    this.authenticatedUser = it
                }
                .toHotIoObservable(this.log)

        // Return task to consumer for optionally subscribing to running authentication task as well
        return task
    }

    fun logout() {
        this.authenticatedUser = null
    }
}