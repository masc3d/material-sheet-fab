package sx.rs

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature

/**
 * Support for flask filters commonly used in REST calls
 * https://flask-restless.readthedocs.io/en/stable/searchformat.html
 */

/** Shared object mapper used to serialize all flask filters */
private val mapper by lazy {
    ObjectMapper().also {
        it.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        it.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
    }
}

/**
 * Flask operator
 *
 * one of
 * ==, eq, equals, equals_to
 * !=, neq, does_not_equal, not_equal_to
 * >, gt, <, lt
 * >=, ge, gte, geq, <=, le, lte, leq
 * in, not_in
 * is_null, is_not_null
 * like
 * has
 * any
 **/
enum class FlaskOperator(val value: String) {
    EQ("eq"),
    NEQ("neq"),
    GT("gt"),
    LT("lt"),
    GE("ge"),
    LE("le"),
    IN("in"),
    NOT_IN("not_in"),
    IS_NULL("is_null"),
    IS_NOT_NULL("is_not_null"),
    LIKE("like"),
    HAS("has"),
    ANY("any");

    override fun toString(): String {
        return this.value
    }
}

/**
 * Flask filter which can be serialized to json
 */
data class FlaskFilter(
        val filters: List<FlaskExpression>
) {
    /** Convert filter to json */
    fun toJson(): String {
        return mapper.writeValueAsString(this)
    }

    constructor(expression: FlaskExpression) : this(listOf(expression))
}

interface FlaskExpression

/**
 * Flask predicate
 * Created by masc on 26.01.18.
 */
data class FlaskPredicate(
        /** The source field */
        val name: String,
        /** Operator */
        val op: FlaskOperator,

        // Value and field are mutually exclusive

        /** Target value */
        @get:JsonProperty("val")
        val value: Any? = null,
        /** Target field */
        val field: String? = null
) : FlaskExpression {

    init {
        if (value != null && field != null)
            throw IllegalArgumentException("Value and field are mutually exclusive")

        if (value == null && field == null)
            throw IllegalArgumentException("One of value or field has to be provided")
    }
}

/**
 * Boolean flask expression
 */
data class FlaskBooleanExpression(
        val or: List<FlaskExpression>? = null,
        val and: List<FlaskExpression>? = null
) : FlaskExpression {

    init {
        if (or != null && and != null)
            throw IllegalArgumentException("Boolean expression fields are mutually exclusive")

        if (or == null && and == null)
            throw IllegalArgumentException("One of the boolean expression fields has to be provided")
    }
}

// TODO: flask builder