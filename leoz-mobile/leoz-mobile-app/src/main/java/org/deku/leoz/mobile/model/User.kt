package org.deku.leoz.mobile.model

import org.deku.leoz.mobile.model.requery.IUser
import org.deku.leoz.mobile.model.requery.UserEntity
import org.deku.leoz.model.VehicleType

/**
 * Mobile user model class
 * Created by n3 on 27.04.17.
 */
class User(
        val entity: UserEntity = UserEntity()
) : IUser by entity {

    constructor(
            id: Int,
            email: String,
            apiKey: String,
            password: String = "",
            vehicleType: VehicleType = VehicleType.CAR
    ): this()
    {
        this.id = id
        this.email = email
        this.apiKey = apiKey
        this.password = password
        this.vehicleType = vehicleType
    }
}