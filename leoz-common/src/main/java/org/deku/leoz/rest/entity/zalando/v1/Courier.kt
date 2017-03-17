package org.deku.leoz.rest.entity.zalando.v1

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

import java.util.Objects

@ApiModel(description = "Defines the driver who will pick up a tour and deliver all orders related to the tour.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2017-03-10T11:34:55.297Z")
class Courier {

    /**
     * Unique identifier of the courier.
     */

    @get:ApiModelProperty(example = "null", required = true, value = "Unique identifier of the courier.")
    @get:JsonProperty("id")
    lateinit var id: String
    /**
     * The name of the courier.
     */

    @get:ApiModelProperty(example = "null", value = "The name of the courier.")
    @get:JsonProperty("name")
    lateinit var name: String


    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }
        val courier = o as Courier?
        return id == courier!!.id && name == courier.name
    }

    override fun hashCode(): Int {
        return Objects.hash(id, name)
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("class Courier {\n")

        sb.append("    id: ").append(toIndentedString(id)).append("\n")
        sb.append("    name: ").append(toIndentedString(name)).append("\n")
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

