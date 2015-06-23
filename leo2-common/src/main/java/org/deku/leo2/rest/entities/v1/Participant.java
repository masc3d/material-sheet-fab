package org.deku.leo2.rest.entities.v1;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.deku.leo2.rest.entities.ShortDate;

import java.util.Date;

/**
 * Sender or consignee attributes
 * Created by masc on 23.06.15.
 */
@ApiModel(description = "")
public class Participant {
    private ShortDate mDate;
    private String mFrom;
    private String mTo;
    private String mCountry;
    private String mZip;

    public Participant() { }

    @ApiModelProperty(value="Country two-letter ISO-3166", position = 10, required = true)
    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry = country;
    }

    @ApiModelProperty(value="Zip code accordant to country spezification", position = 20, required = true)
    public String getZip() {
        return mZip;
    }

    public void setZip(String zip) {
        mZip = zip;
    }

    @ApiModelProperty(dataType = "date", example = "2015-06-01", position = 30, required = true, value = "Delivery or pickup date", allowableValues = "00:00 - 23:59")
    public ShortDate getDate() {
        return mDate;
    }

    public void setDate(ShortDate date) {
        mDate = date;
    }

    @ApiModelProperty(dataType = "string", example = "10:00", position = 40, required = true, value = "Time window (from)", allowableValues = "00:00 - 23:59")
    public String getFrom() {
        return mFrom;
    }

    public void setFrom(String from) {
        mFrom = from;
    }

    @ApiModelProperty(dataType = "string", example = "10:00", position = 50, required = true, value = "Time window (to)", allowableValues = "00:00 - 23:59")
    public String getTo() {
        return mTo;
    }

    public void setTo(String to) {
        mTo = to;
    }
}
