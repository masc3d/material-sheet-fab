package org.deku.leoz.service.zalando.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Generated;
import java.util.Objects;

@ApiModel(description="Notified delivery order")
@Generated(value = "io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2017-03-10T11:34:55.297Z")
public class NotifiedDeliveryOrder   {
  
  private String id = null;
  private String trackingUrl = null;

  public NotifiedDeliveryOrder(String id) {
    this.id = id;
  }

  public NotifiedDeliveryOrder(String id, String trackingUrl) {
    this.id = id;
    this.trackingUrl = trackingUrl;
  }

  /**
   * Delivery order identifier in carrier's system
   **/
  
  @ApiModelProperty(example = "null", required = true, value = "Delivery order identifier in carrier's system")
  @JsonProperty("id")
  @NotNull
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Link to delivery order tracking page
   **/
  
  @ApiModelProperty(example = "null", value = "Link to delivery order tracking page")
  @JsonProperty("tracking_url")
  public String getTrackingUrl() {
    return trackingUrl;
  }
  public void setTrackingUrl(String trackingUrl) {
    this.trackingUrl = trackingUrl;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NotifiedDeliveryOrder notifiedDeliveryOrder = (NotifiedDeliveryOrder) o;
    return Objects.equals(id, notifiedDeliveryOrder.id) &&
        Objects.equals(trackingUrl, notifiedDeliveryOrder.trackingUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, trackingUrl);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NotifiedDeliveryOrder {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    trackingUrl: ").append(toIndentedString(trackingUrl)).append("\n");
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
}

