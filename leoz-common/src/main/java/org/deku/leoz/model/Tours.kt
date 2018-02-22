package org.deku.leoz.model

import sx.io.serialization.Serializable
import java.util.*

/**
 * Tour / route related domain models
 * Created by masc on 08.02.18.
 */

/**
 * Time interval
 */
@Serializable(0xeb180b0d9471bd)
data class Interval(
        var from: Date? = null,
        var to: Date? = null
)

/**
 * Tour route (optimization) meta data
 */
@Serializable(0x156645e5b698b1)
data class TourRouteMeta(
        /** Route start time */
        var start: Date? = null,
        /** Route end time */
        var target: Interval? = null,
        /** Distance in kilometers */
        var distance: Double? = null,
        /** Route duration in minutes */
        var totalDuration: Int? = null,
        /** Driving time in minutes */
        var drivingTime: Int? = null
)

/**
 * Tour route (optimization) stop meta data
 */
@Serializable(0xb830a899e51513)
data class TourStopRouteMeta(
        // TODO document what (delivery) time is compared to eta
        var delivery: Interval? = null,
        /** Estimated time of arrival */
        var eta: Interval? = null,
        // TODO document how target differs from eta
        /** Target time */
        var target: Interval? = null,
        // TODO document what driver / delivery time is
        /** Driver time */
        var driver: Interval? = null,
        /** Estimated length of stay in minutes */
        var estimatedStayLength: Int? = null,
        /** Actual stay length in minutes */
        var stayLengtH: Int? = null
)