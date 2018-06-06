package org.deku.leoz.model

import sx.Result
import sx.io.serialization.Serializable
import sx.time.TimeSpan
import sx.time.plusMinutes
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
 * Calculates median of this time range
 */
fun TimeRange.median(): Date? {
    val etaFrom = this.from
    val etaTo = this.to

    return if (etaFrom != null && etaTo != null)
        etaFrom.plusMinutes(
                TimeSpan.between(etaFrom, etaTo).absMinutes
        )
    else
        null
}

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
 *
 * CSV encoded tour identification following the format
 * DK;TR;<tour-id>;<tour-uid in upper case>
 *
 * @param id tour ids
 * @param uid tour uid
 */
@Serializable(0x987f80204941b9)
data class TourIdentification(
        val id: Int? = null,
        val uid: UUID? = null
) {
    companion object {
        private val DELIMITER = ";"

        private val OWNER = "DK"
        private val TYPE = "TR"

        private val PREFIX by lazy { listOf(OWNER, TYPE).joinToString(DELIMITER) }
        /**
         * Parse tour identification from label.
         * The expected format is
         */
        fun parseLabel(label: String): Result<TourIdentification> {
            return try {
                if (label.startsWith(PREFIX)) {
                    val parts = label.split(DELIMITER)
                    Result(
                            TourIdentification(
                                    id = parts[2].toInt(),
                                    uid = UUID.fromString(parts[3])
                            )
                    )
                } else {
                    Result(
                            IllegalArgumentException("Invalid tour ident prefix [${label}]")
                    )
                }
            } catch (t: Throwable) {
                Result(
                        IllegalArgumentException("Invalid tour ident [${label}]", t)
                )
            }
        }
    }

    val label by lazy {
        listOf(OWNER, TYPE, id, uid.toString().toUpperCase())
                .joinToString(DELIMITER)
    }
}