package org.deku.leoz.rest.entities.v1

import com.google.common.base.Strings
import com.wordnik.swagger.annotations.ApiModel
import com.wordnik.swagger.annotations.ApiModelProperty
import org.deku.leoz.rest.entities.ShortDate
import org.deku.leoz.rest.entities.ShortTime

import java.time.LocalDate


/**
 * Routing service request response
 * Created by JT on 23.06.15.
 */
ApiModel(value = "Routing", subTypes = arrayOf(Routing.Participant::class), description = "Routing response")
public class Routing {
    /**
     * Routing service request response member
     * Created by JT on 23.06.15.
     */
    ApiModel(value = "Participant", description = "Response participant. Sender or consignee")
    public class Participant {
        ApiModelProperty(dataType = "string", example = "020", position = 10, required = true, value = "Station number", allowableValues = "010 - 999")
        public var station: Int? = 0
        public val stationFormatted: String?
            get() = if (station != null) Strings.padStart(station.toString(), 3, '0') else null
        ApiModelProperty(dataType = "string", example = "PL", position = 20, required = true, value = "Country two-letter ISO-3166")
        public var country: String = ""
        ApiModelProperty(dataType = "string", example = "01-1010", position = 30, required = true, value = "Zipcode contry conform")
        public var zipCode: String = ""
        ApiModelProperty(dataType = "string", example = "WR", position = 40, required = true, value = "Zone", allowableValues = "A,B,C,D,WR,UL")
        public var zone: String = ""
        ApiModelProperty(dataType = "string", example = "X", position = 50, required = true, value = "Sector", allowableValues = "A-Z")
        public var sector: String? = ""
        ApiModelProperty(dataType = "string", example = "Workday", position = 60, required = true, value = "Type of day")
        public var dayType: String = DayType.Workday.toString()
        ApiModelProperty(example = "false", position = 70, required = true, value = "Is island")
        public var island: Boolean? = false
        ApiModelProperty(dataType = "integer", example = "1", position = 80, required = true, value = "Termtime in days", allowableValues = ">=1")
        public var term: Int? = 1
        ApiModelProperty(dataType = "string", example = "08:01", position = 90, required = true, value = "Earliest time of delivery", allowableValues = "00:00 - 23:59")
        public var earliestTimeOfDelivery: ShortTime? = ShortTime()
        ApiModelProperty(dataType = "string", example = "12:00", position = 120, required = true, value = "Delivery time until on saturday", allowableValues = "00:00 - 23:59")
        public var saturdayDeliveryUntil: ShortTime = ShortTime()
        ApiModelProperty(dataType = "string", example = "12:00", position = 130, required = true, value = "Delivery time until on sunday", allowableValues = "00:00 - 23:59")
        public var sundayDeliveryUntil: ShortTime = ShortTime()
        ApiModelProperty(dataType = "string", example = "16:00", position = 140, required = true, value = "Pick up time until", allowableValues = "00:00 - 23:59")
        public var pickupUntil: ShortTime = ShortTime()
        ApiModelProperty(dataType = "string", example = "AH", position = 150, required = true, value = "Partner manager")
        public var partnerManager: String = ""

        ApiModelProperty(hidden = true)
        public var date: LocalDate? = null
        ApiModelProperty(hidden = true)
        public var message: String? = ""
    }

    ApiModelProperty(position = 30)
    public var sender: Routing.Participant? = Participant()
    ApiModelProperty(position = 40)
    public var consignee: Routing.Participant? = Participant()
    ApiModelProperty(dataType = "string", example = "F,N", position = 50, required = true, value = "Used via hubs: \"F,N\"")
    public var viaHubs: Array<String>? = null
    ApiModelProperty(dataType = "string", example = "F,N 100", position = 60, required = true, value = "Routing label content:  \"F,N 100\"")
    public var labelContent: String = ""
    ApiModelProperty(dataType = "string", example = "OK", position = 70, required = true, value = "Info message: \"OK\"")
    public var message: String = ""
    ApiModelProperty(dataType = "date", example = "2015-06-01", position = 10, required = true, value = "Send date", allowableValues = "2015-06-01")
    public var sendDate: ShortDate? = null
    ApiModelProperty(dataType = "date", example = "2015-06-02", position = 20, required = false, value = "Delivery date", allowableValues = "2015-06-01")
    public var deliveryDate: ShortDate? = null

    public constructor() {  }
}
