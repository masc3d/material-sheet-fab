package org.deku.leoz.mobile.model

import org.deku.leoz.model.VehicleType

/**
 * Mobile user model class
 * Created by n3 on 27.04.17.
 */
class User(
        val id: Int,
        val email: String,
        val apiKey: String,
        /** The current user's vehicle type. Defaults to CAR */
        var vehicleType: VehicleType = VehicleType.CAR
)