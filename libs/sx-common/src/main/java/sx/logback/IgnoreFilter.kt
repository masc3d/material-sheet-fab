package sx.logback

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.turbo.TurboFilter
import ch.qos.logback.core.filter.Filter
import ch.qos.logback.core.spi.FilterReply
import org.slf4j.Marker

/**
 * Logback ignore filter
 * Created by masc on 21.02.18.
 */

/** Condition */
data class IgnoreFilterCondition(
        val name: String,
        val level: Level,
        /** Matches start of message. Ignored by turbo filters */
        val message: String? = null
)

/**
 * Logback filter for ignoring specific messages
 */
class IgnoreFilter(
        val conditions: List<IgnoreFilterCondition>
) : Filter<ILoggingEvent>() {

    override fun decide(event: ILoggingEvent): FilterReply {

        fun ILoggingEvent.matches(condition: IgnoreFilterCondition): Boolean =
                this.level == condition.level &&
                        this.loggerName == condition.name &&
                        condition.message?.let { this.message.startsWith(it) } ?: true

        return when {
            conditions.any { event.matches(it) } -> FilterReply.DENY
            else -> FilterReply.NEUTRAL
        }
    }
}

/**
 * Logback turbo filter for ignoring specific messages
 */
class IgnoreTurboFilter(
        val conditions: List<IgnoreFilterCondition>
) : TurboFilter() {

    override fun decide(marker: Marker?, logger: Logger?, level: Level?, format: String?, params: Array<out Any>?, t: Throwable?): FilterReply {
        fun matches(condition: IgnoreFilterCondition): Boolean =
                level == condition.level &&
                        logger?.name == condition.name

        return when {
            conditions.any { matches(it) } -> FilterReply.DENY
            else -> FilterReply.NEUTRAL
        }
    }
}