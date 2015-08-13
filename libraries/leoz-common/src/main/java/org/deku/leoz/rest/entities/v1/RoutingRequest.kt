package org.deku.leoz.rest.entities.v1

import com.wordnik.swagger.annotations.ApiModel
import com.wordnik.swagger.annotations.ApiModelProperty
import org.deku.leoz.rest.entities.ShortDate

/**
 * Created by masc on 23.06.15.
 */
ApiModel(value = "RoutingRequest", subTypes = arrayOf(RoutingRequest.RequestParticipant::class), description = "Routing request")
public class RoutingRequest {
    /**
     * Sender or consignee attributes
     * Created by masc on 23.06.15.
     */
    @ApiModel(value = "RequestParticipant", description = "Request Participant. Delivery or consignee")
    public class RequestParticipant {
        @ApiModelProperty(dataType = "string", example = "DE", value = "Country two-letter ISO-3166", position = 10, required = true)
        public var country: String? = null
        @ApiModelProperty(dataType = "string", example = "36286", value = "Zip code accordant to country spezification", position = 20, required = true)
        public var zip: String? = null
        ApiModelProperty(dataType = "string", example = "09:00", position = 40, required = false, value = "Time window (from)", allowableValues = "00:00 - 23:59")
        public var timeFrom: String? = null
        ApiModelProperty(dataType = "string", example = "12:00", position = 50, required = false, value = "Time window (to)", allowableValues = "00:00 - 23:59")
        public var timeTo: String? = null
        ApiModelProperty(dataType = "string", example = "020", position = 60, required = false, value = "Desired Stationnumber", allowableValues = "010 - 999")
        public var desiredStation: String? = null

        public constructor() {  }

        public constructor(timeFrom: String, timeTo: String, country: String, zip: String) {
            //mDate = date;
            this.timeFrom = timeFrom
            this.timeTo = timeTo
            this.country = country
            this.zip = zip
        }
    }

    ApiModelProperty(dataType = "date", example = "2015-06-01", position = 10, required = true, value = "Senddate", allowableValues = "2015-06-01")
    public var sendDate: ShortDate? = null
    ApiModelProperty(dataType = "date", example = "2015-06-02", position = 20, required = false, value = "Desired Deliverydate", allowableValues = "2015-06-02")
    public var desiredDeliveryDate: ShortDate? = null
    ApiModelProperty(value = "Sum of DeKu Servicvalues", position = 22, required = false)
    public var services: Int? = null
    ApiModelProperty(value = "Real weight", position = 24, required = false)
    public var weight: Float? = null
    ApiModelProperty(value = "Sender", position = 30, required = false)
    public var sender: RequestParticipant? = null
    ApiModelProperty(value = "Consignee", position = 40, required = false)
    public var consignee: RequestParticipant? = null
}
