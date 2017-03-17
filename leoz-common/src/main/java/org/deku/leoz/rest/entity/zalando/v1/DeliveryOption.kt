package org.deku.leoz.rest.entity.zalando.v1

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.deku.leoz.rest.entity.ShortTime

import java.text.SimpleDateFormat
import java.util.*

@ApiModel(description = "Delivery option for a package. Basic information regarding delivery window, cut off and pic up points.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2017-03-10T11:34:55.297Z")
class DeliveryOption {

    /**
     * Delivery option identifier
     */

    @get:ApiModelProperty(example = "228", required = true, value = "Delivery option identifier")
    @get:JsonProperty("id")
    var id: String? = null
    /**
     * Cut off time for order modification / cancelation
     */

    @get:ApiModelProperty(example = "2017-03-16T16:30:00.000Z", required = true, value = "Cut off time for order modification / cancelation")
    @get:JsonProperty("cut_off")
    var cutOff: ShortTime? = null
    /**
     * Scheduled pick-up time
     */

    @get:ApiModelProperty(example = "2017-03-16T17:00:00.000Z", required = true, value = "Scheduled pick-up time")
    @get:JsonProperty("pick_up")
    var pickUp: ShortTime? = null
    /**
     * Earliest possible delivery time
     */

    @get:ApiModelProperty(example = "2017-03-16T17:30:00.000Z", required = true, value = "Earliest possible delivery time")
    @get:JsonProperty("delivery_from")
    var deliveryFrom: ShortTime? = null
    /**
     * Latest possible delivery time
     */

    @get:ApiModelProperty(example = "2017-03-16T21:30:00.000Z", required = true, value = "Latest possible delivery time")
    @get:JsonProperty("delivery_to")
    var deliveryTo: ShortTime? = null

    constructor(id: String, cutOff: ShortTime, pickUp: ShortTime, deliveryFrom: ShortTime, deliveryTo: ShortTime) {
        this.id = id
        this.cutOff = (cutOff)
        this.pickUp = (pickUp)
        this.deliveryFrom = (deliveryFrom)
        this.deliveryTo = (deliveryTo)
    }

    constructor() {}


    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }
        val deliveryOption = o as DeliveryOption?
        return id == deliveryOption!!.id &&
                cutOff == deliveryOption.cutOff &&
                pickUp == deliveryOption.pickUp &&
                deliveryFrom == deliveryOption.deliveryFrom &&
                deliveryTo == deliveryOption.deliveryTo
    }

    override fun hashCode(): Int {
        return Objects.hash(id, cutOff, pickUp, deliveryFrom, deliveryTo)
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("class DeliveryOption {\n")

        sb.append("    id: ").append(toIndentedString(id)).append("\n")
        sb.append("    cutOff: ").append(toIndentedString(cutOff)).append("\n")
        sb.append("    pickUp: ").append(toIndentedString(pickUp)).append("\n")
        sb.append("    deliveryFrom: ").append(toIndentedString(deliveryFrom)).append("\n")
        sb.append("    deliveryTo: ").append(toIndentedString(deliveryTo)).append("\n")
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

    private fun getISO8601StringForDate(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat.format(date)
    }
}

