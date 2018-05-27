package org.deku.leoz.service.zalando.entity

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.annotation.Generated

@ApiModel(description = "Problem details description (see https://tools.ietf.org/html/rfc7807)")
@Generated(value = ["io.swagger.codegen.languages.JavaResteasyServerCodegen"], date = "2017-03-10T11:34:55.297Z")
class Problem(
        type: String = "",
        instance: String = "",
        title: String = "",
        status: java.math.BigDecimal = java.math.BigDecimal.ZERO,
        details: String = "") {

    constructor(title: String, details: String) : this(type = "", title = title, details = details) {

    }

    /**
     * Problem type
     */
    @get:ApiModelProperty(example = "null", required = true, value = "Problem type")
    @get:JsonProperty("type")
    var type: String? = null

    /**
     * Problem instance
     */
    @get:ApiModelProperty(example = "null", value = "Problem instance")
    @get:JsonProperty("instance")
    var instance: String? = null

    /**
     * Problem title
     */
    @get:ApiModelProperty(example = "null", required = true, value = "Problem title")
    @get:JsonProperty("title")
    var title: String? = null
    /**
     * Problem status
     */

    @get:ApiModelProperty(example = "null", required = true, value = "Problem status")
    @get:JsonProperty("status")
    var status: java.math.BigDecimal? = null

    /**
     * Problem detail
     */
    @get:ApiModelProperty(example = "null", value = "Problem detail")
    @get:JsonProperty("details")
    var details: String? = null

    init {
        this.type = type
        this.instance = instance
        this.title = title
        this.status = status
        this.details = details
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val problem = other as org.deku.leoz.service.zalando.entity.Problem?
        return type == problem!!.type &&
                instance == problem.instance &&
                title == problem.title &&
                status == problem.status &&
                details == problem.details
    }

    override fun hashCode(): Int {
        return java.util.Objects.hash(type, instance, title, status, details)
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("class Problem {\n")

        sb.append("    type: ").append(toIndentedString(type)).append("\n")
        sb.append("    instance: ").append(toIndentedString(instance)).append("\n")
        sb.append("    title: ").append(toIndentedString(title)).append("\n")
        sb.append("    status: ").append(toIndentedString(status)).append("\n")
        sb.append("    details: ").append(toIndentedString(details)).append("\n")
        sb.append("}")
        return sb.toString()
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private fun toIndentedString(o: Any?): String {
        if (o == null) {
            return "null"
        }
        return o.toString().replace("\n", "\n    ")
    }
}

