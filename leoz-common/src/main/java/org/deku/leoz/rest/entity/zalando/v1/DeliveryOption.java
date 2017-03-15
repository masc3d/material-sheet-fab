package org.deku.leoz.rest.entity.zalando.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

@ApiModel(description = "Delivery option for a package. Basic information regarding delivery window, cut off and pic up points.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2017-03-10T11:34:55.297Z")
public class DeliveryOption {

    private String id = null;
    private String cutOff = null;
    private String pickUp = null;
    private String deliveryFrom = null;
    private String deliveryTo = null;

    public DeliveryOption(String id, Date cutOff, Date pickUp, Date deliveryFrom, Date deliveryTo) {
        this.id = id;
        this.cutOff = getISO8601StringForDate(cutOff);
        this.pickUp = getISO8601StringForDate(pickUp);
        this.deliveryFrom = getISO8601StringForDate(deliveryFrom);
        this.deliveryTo = getISO8601StringForDate(deliveryTo);
    }

    public DeliveryOption() {
    }

    /**
     * Delivery option identifier
     **/

    @ApiModelProperty(example = "1", required = true, value = "Delivery option identifier")
    @JsonProperty("id")
    @NotNull
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Cut off time for order modification / cancelation
     **/

    @ApiModelProperty(example = "1730", required = true, value = "Cut off time for order modification / cancelation")
    @JsonProperty("cut_off")
    @NotNull
    public String getCutOff() {
        return cutOff;
    }

    public void setCutOff(String cutOff) {
        this.cutOff = cutOff;
    }

    /**
     * Scheduled pick-up time
     **/

    @ApiModelProperty(example = "1730", required = true, value = "Scheduled pick-up time")
    @JsonProperty("pick_up")
    @NotNull
    public String getPickUp() {
        return pickUp;
    }

    public void setPickUp(String pickUp) {
        this.pickUp = pickUp;
    }

    /**
     * Earliest possible delivery time
     **/

    @ApiModelProperty(example = "1730", required = true, value = "Earliest possible delivery time")
    @JsonProperty("delivery_from")
    @NotNull
    public String getDeliveryFrom() {
        return deliveryFrom;
    }

    public void setDeliveryFrom(String deliveryFrom) {
        this.deliveryFrom = deliveryFrom;
    }

    /**
     * Latest possible delivery time
     **/

    @ApiModelProperty(example = "1730", required = true, value = "Latest possible delivery time")
    @JsonProperty("delivery_to")
    @NotNull
    public String getDeliveryTo() {
        return deliveryTo;
    }

    public void setDeliveryTo(String deliveryTo) {
        this.deliveryTo = deliveryTo;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DeliveryOption deliveryOption = (DeliveryOption) o;
        return Objects.equals(id, deliveryOption.id) &&
                Objects.equals(cutOff, deliveryOption.cutOff) &&
                Objects.equals(pickUp, deliveryOption.pickUp) &&
                Objects.equals(deliveryFrom, deliveryOption.deliveryFrom) &&
                Objects.equals(deliveryTo, deliveryOption.deliveryTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cutOff, pickUp, deliveryFrom, deliveryTo);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class DeliveryOption {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    cutOff: ").append(toIndentedString(cutOff)).append("\n");
        sb.append("    pickUp: ").append(toIndentedString(pickUp)).append("\n");
        sb.append("    deliveryFrom: ").append(toIndentedString(deliveryFrom)).append("\n");
        sb.append("    deliveryTo: ").append(toIndentedString(deliveryTo)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    private String getISO8601StringForDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(date);
    }
}

