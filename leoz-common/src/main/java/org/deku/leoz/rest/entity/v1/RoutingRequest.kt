package org.deku.leoz.rest.entity.v1

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.deku.leoz.rest.entity.ShortDate

/**
 * Created by masc on 23.06.15.
 */
@ApiModel(value = "RoutingRequest", subTypes = arrayOf(RoutingRequest.RequestParticipant::class), description = "Routing request")
class RoutingRequest {
    /**
     * Sender or consignee attributes
     * Created by masc on 23.06.15.
     */
    @ApiModel(value = "RequestParticipant", description = "Request participant. Sender or consignee")
    class RequestParticipant {
        @ApiModelProperty(dataType = "string", example = "DE", value = "Country two-letter ISO-3166", position = 10, required = true)
        var country: String? = null

        @ApiModelProperty(dataType = "string", example = "36286", value = "Zip code accordant to country specification", position = 20, required = true)
        var zip: String? = null

        @ApiModelProperty(dataType = "string", example = "09:00", position = 40, required = false, value = "Time window (from)", allowableValues = "00:00 - 23:59")
        var timeFrom: String? = null

        @ApiModelProperty(dataType = "string", example = "12:00", position = 50, required = false, value = "Time window (to)", allowableValues = "00:00 - 23:59")
        var timeTo: String? = null

        @ApiModelProperty(dataType = "string", example = "020", position = 60, required = false, value = "Desired station number", allowableValues = "010 - 999")
        var desiredStation: String? = null

        constructor() {  }

        constructor(country: String? = null, zip: String? = null, timeFrom: String? = null, timeTo: String? = null, desiredStation: String? = null) {
            this.country = country
            this.zip = zip
            this.timeFrom = timeFrom
            this.timeTo = timeTo
            this.desiredStation = desiredStation
        }
    }

    @ApiModelProperty(dataType = "date", example = "2015-06-01", position = 10, required = true, value = "Send date", allowableValues = "2015-06-01")
    var sendDate: ShortDate? = null

    @ApiModelProperty(dataType = "date", example = "2015-06-02", position = 20, required = false, value = "Desired delivery date", allowableValues = "2015-06-02")
    var desiredDeliveryDate: ShortDate? = null

    @ApiModelProperty(value = "Sum of DeKu servic values", position = 22, required = false)
    var services: Int? = null

    @ApiModelProperty(value = "Real weight", position = 24, required = false)
    var weight: Float? = null

    @ApiModelProperty(value = "Sender", position = 30, required = false)
    var sender: RequestParticipant? = null

    @ApiModelProperty(value = "Consignee", position = 40, required = false)
    var consignee: RequestParticipant? = null

    constructor() { }

    constructor(sendDate: ShortDate? = null, desiredDeliveryDate: ShortDate? = null, services: Int? = null, weight: Float? = null, sender: RequestParticipant? = null, consignee: RequestParticipant? = null) {
        this.sendDate = sendDate
        this.desiredDeliveryDate = desiredDeliveryDate
        this.services = services
        this.weight = weight
        this.sender = sender
        this.consignee = consignee
    }
}
