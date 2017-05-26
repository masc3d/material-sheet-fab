package org.deku.leoz.service.entity

/**
 * Created by 27694066 on 26.05.2017.
 */
data class Position (val latitude: Double, val longitude: Double, val time: Long, val speed: Float? = null, val bearing: Float? = null, val altitude: Double? = null, val accuracy: Float? = null) {
    override fun toString(): String {
        return "Time [${this.time}] Lat [${this.latitude}] Lng [${this.longitude}] Accuracy [${this.accuracy}] Altitude [${this.altitude}] Bearing [${this.bearing}] Speed [${this.speed}]"
    }
}