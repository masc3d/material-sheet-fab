package org.deku.leoz.model

import sx.io.serialization.Serializable
import java.util.*

/**
 * Tour / route related domain models
 * Created by masc on 08.02.18.
 */

/**
 * Tour route (optimization) meta data
 */
@Serializable(0x156645e5b698b1)
data class TourRouteMeta(
        /** Route start time */
        var start: Date? = null,
        /** Route end time (window start) */
        var targetFrom: Date? = null,
        /** Route end time (window end) */
        var targetTo: Date? = null,
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
        /** Estimated time of arrival (window start) */
        var etaFrom: Date? = null,
        /** Estimated time of arrival (window end) */
        var etaTo: Date? = null,
        // TODO document how target differs from eta
        /** Target time (window start) */
        var targetFrom: Date? = null,
        /** Target time (window end) */
        var targetTo: Date? = null,
        // TODO document what driver / delivery time is
        /** Driver time (window start) */
        var driverFrom: Date? = null,
        /** Driver time (window end) */
        var driverTo: Date? = null,
        /** Estimated length of stay in minutes */
        var estimatedStayLength: Int? = null,
        /** Actual stay length in minutes */
        var stayLengtH: Int? = null
)