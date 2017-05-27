package org.deku.leoz.service.entity

import sx.io.serialization.Serializable

/**
 * Created by 27694066 on 26.05.2017.
 */
@Serializable(0x5af819e313304e)
data class Position (
        val latitude: Double? = null,
        val longitude: Double? = null,
        val time: Long? = null,
        val speed: Float? = null,
        val meh: String? = "",
        val bearing: Float? = null,
        val altitude: Double? = null,
        val accuracy: Float? = null)