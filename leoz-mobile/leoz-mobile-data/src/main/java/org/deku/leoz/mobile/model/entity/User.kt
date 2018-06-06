package org.deku.leoz.mobile.model.entity

import android.databinding.Bindable
import android.databinding.Observable
import io.requery.*
import org.deku.leoz.mobile.data.BR
import org.deku.leoz.model.VehicleType
import sx.android.databinding.BaseRxObservable
import java.util.*

/**
 * Mobile user entity
 * Created by masc on 18.07.17.
 */
@Entity
@Table(name = "user_")
abstract class User : BaseRxObservable(), Persistable, Observable {

    companion object

    @get:Key
    abstract var id: Int
    @get:Column(nullable = false)
    abstract var email: String
    @get:Column(nullable = false)
    abstract var password: String
    @get:Column(nullable = false)
    abstract var apiKey: String

    /** The current user's vehicle type. Defaults to CAR */
    @get:Bindable
    @get:Column
    abstract var vehicleType: VehicleType?
    val vehicleTypeProperty by lazy { ObservableRxField(BR.vehicleType, { this.vehicleType }) }

    @get:Bindable
    @get:Column
    abstract var stationNo: Int?
    val stationNoProperty by lazy { ObservableRxField(BR.stationNo, { this.stationNo }) }

    @get:Column
    abstract var lastLoginTime: Date?

    /** The host this user belongs to, as the same user may exist across systems with diverging id */
    @get:Column
    abstract var host: String?
}

fun User.Companion.create(
        id: Int,
        email: String,
        password: String,
        apiKey: String,
        vehicleType: VehicleType? = null,
        host: String? = null
): UserEntity {
    return UserEntity().also {
        it.id = id
        it.email = email
        it.password = password
        it.apiKey = apiKey
        it.vehicleType = vehicleType
        it.host = host
    }
}