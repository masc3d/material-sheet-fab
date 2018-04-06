package org.deku.leoz.model

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import sx.Result
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
data class TimeRange(
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
        var target: TimeRange? = null,
        /** Distance in kilometers */
        var distance: Double? = null,
        /** Route duration in minutes */
        var totalDuration: Int? = null,
        /** Driving time in minutes */
        var drivingTime: Int? = null,
        /** Route optimization quality indication in percentage */
        var quality: Double? = null
)

/**
 * Tour route (optimization) stop meta data
 */
@Serializable(0xb830a899e51513)
data class TourStopRouteMeta(
        // TODO document what (delivery) time is compared to eta
        var delivery: TimeRange? = null,
        /** Estimated time of arrival */
        var eta: TimeRange? = null,
        // TODO document how target differs from eta
        /** Target time */
        var target: TimeRange? = null,
        // TODO document what driver / delivery time is
        /** Driver time */
        var driver: TimeRange? = null,
        /** Estimated length of stay in minutes */
        var estimatedStayLength: Int? = null,
        /** Actual stay length in minutes */
        var stayLengtH: Int? = null
)

/**
 * Tour identification
 * @param id tour ids
 * @param uid tour uid
 */
@Serializable(0x987f80204941b9)
data class TourIdentification(
        var id: Int? = null,
        var uid: UUID? = null
) {
    companion object {
        /**
         * Parse tour identification from label.
         * The expected format is
         */
        fun parseLabel(label: String): Result<TourIdentification> {
            return try {
                if (label.startsWith("<deku-tour")) {
                    Result(
                            XmlMapper().readValue(label, TourIdentification::class.java)
                    )
                } else {
                    Result(
                            IllegalArgumentException()
                    )
                }
            } catch (t: Throwable) {
                Result(t)
            }
        }
    }
}