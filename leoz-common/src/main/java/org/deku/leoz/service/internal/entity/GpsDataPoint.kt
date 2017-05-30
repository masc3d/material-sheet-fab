package org.deku.leoz.service.internal.entity

import sx.io.serialization.Serializable
import java.util.*

/**
 * A generic GPS data point used in leoz prototols
 * Created by 27694066 on 26.05.2017.
 */
@Serializable(0x5af819e313304e)
data class GpsDataPoint(
        val latitude: Double? = null,
        val longitude: Double? = null,
        val time: Date? = null,
        val speed: Float? = null,
        val bearing: Float? = null,
        val altitude: Double? = null,
        val accuracy: Float? = null)