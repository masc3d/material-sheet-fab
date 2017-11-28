package org.deku.leoz.service.zalando.entity

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

import java.text.SimpleDateFormat
import java.util.*
import javax.annotation.Generated

@ApiModel(description = "Delivery option for a package. Basic information regarding delivery window, cut off and pic up points.")
@Generated(value = ["io.swagger.codegen.languages.JavaResteasyServerCodegen"], date = "2017-03-10T11:34:55.297Z")
data class DeliveryOption(
        /**
         * Delivery option identifier
         */
        @get:ApiModelProperty(example = "228", required = true, value = "Delivery option identifier")
        @get:JsonProperty("id")
        var id: String? = null,

        /**
         * Cut off time for order modification / cancelation
         */
        @get:ApiModelProperty(example = "2017-03-16T16:30:00.000Z", required = true, value = "Cut off time for order modification / cancelation")
        @get:JsonProperty("cut_off")
        var cutOff: Date? = null,

        /**
         * Scheduled pick-up time
         */
        @get:ApiModelProperty(example = "2017-03-16T17:00:00.000Z", required = true, value = "Scheduled pick-up time")
        @get:JsonProperty("pick_up")
        var pickUp: Date? = null,

        /**
         * Earliest possible delivery time
         */
        @get:ApiModelProperty(example = "2017-03-16T17:30:00.000Z", required = true, value = "Earliest possible delivery time")
        @get:JsonProperty("delivery_from")
        var deliveryFrom: Date? = null,

        /**
         * Latest possible delivery time
         */
        @get:ApiModelProperty(example = "2017-03-16T21:30:00.000Z", required = true, value = "Latest possible delivery time")
        @get:JsonProperty("delivery_to")
        var deliveryTo: Date? = null
) {
    private fun getISO8601StringForDate(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat.format(date)
    }

}
