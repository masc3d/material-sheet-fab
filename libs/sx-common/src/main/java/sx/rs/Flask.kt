package sx.rs

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.ObjectMapper

/**
 * Support for flask filters commonly used in REST calls
 * https://flask-restless.readthedocs.io/en/stable/searchformat.html
 */

/** Shared object mapper used to serialize all flask filters */
private val mapper by lazy {
    ObjectMapper().also {
        it.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }
}

enum class FlaskOperator(val value: String) {
    eq("eq"),
    neq("neq"),
    gt("gt"),
    lt("lt"),
    ge("ge"),
    le("le"),
    `in`("in"),
    not_in("not_in"),
    is_null("is_null"),
    is_not_null("is_not_null"),
    has("has"),
    any("any")
}

data class FlaskFilter(
        val filters: List<FlaskQuery>
) {
    /** Convert filter to json */
    fun toJson(): String {
        return mapper.writeValueAsString(this)
    }
}

/**
 * A flash filter which can be serialized to json
 * Created by masc on 26.01.18.
 */
data class FlaskQuery(
        /** The source field */
        val name: String,
        /** Operator
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
        val op: FlaskOperator,

        // Value and field are mutually exclusive

        /** Target value */
        @get:JsonProperty("val")
        val value: Any? = null,
        /** Target field */
        val field: String? = null
) {
    init {
        if (value != null && field != null)
            throw IllegalArgumentException("Value and field are mutually exclusive")

        if (value == null && field == null)
            throw IllegalArgumentException("One of value or field has to be provided")
    }
}