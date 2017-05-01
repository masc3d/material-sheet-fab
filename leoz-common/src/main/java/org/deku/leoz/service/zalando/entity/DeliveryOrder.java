package org.deku.leoz.service.zalando.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Generated;
import java.util.Objects;


@Generated(value = "io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2017-03-10T11:34:55.297Z")
public class DeliveryOrder   {
  
  private String incomingId = null;
  private DeliveryOption deliveryOption = null;
  private DeliveryAddress sourceAddress = null;
  private DeliveryAddress targetAddress = null;

  @ApiModel(description="Delivery address, could be either source or target.")
  public class DeliveryAddress   {

    private String contactName = null;
    private String addressLine = null;
    private String zipCode = null;
    private String city = null;
    private String countryCode = null;
    private String comment = null;
    private String phone = null;
    private String email = null;

    /**
     * Contact name
     **/

    @ApiModelProperty(example = "Philipp Prangenberg", required = true, value = "Contact name")
    @JsonProperty("contact_name")
    @NotNull
    public String getContactName() {
      return contactName;
    }
    public void setContactName(String contactName) {
      this.contactName = contactName;
    }

    /**
     * Street name with house number.
     **/

    @ApiModelProperty(example = "Burscheidter Weg 4", required = true, value = "Street name with house number.")
    @JsonProperty("address_line")
    @NotNull
    public String getAddressLine() {
      return addressLine;
    }
    public void setAddressLine(String addressLine) {
      this.addressLine = addressLine;
    }

    /**
     * Zip code
     **/

    @ApiModelProperty(example = "40822", required = true, value = "Zip code")
    @JsonProperty("zip_code")
    @NotNull
    public String getZipCode() {
      return zipCode;
    }
    public void setZipCode(String zipCode) {
      this.zipCode = zipCode;
    }

    /**
     * City
     **/

    @ApiModelProperty(example = "Mettmann", required = true, value = "City")
    @JsonProperty("city")
    @NotNull
    public String getCity() {
      return city;
    }
    public void setCity(String city) {
      this.city = city;
    }

    /**
     * Country Code (e.g. Germany, DE)
     **/

    @ApiModelProperty(example = "DE", required = true, value = "Country Code (e.g. Germany, DE)")
    @JsonProperty("country_code")
    @NotNull
    public String getCountryCode() {
      return countryCode;
    }
    public void setCountryCode(String countryCode) {
      this.countryCode = countryCode;
    }

    /**
     * Address comment, e.g. c/o, additional info or notes
     **/

    @ApiModelProperty(example = "z.Hd. Max Mustermann", value = "Address comment, e.g. c/o, additional info or notes")
    @JsonProperty("comment")
    public String getComment() {
      return comment;
    }
    public void setComment(String comment) {
      this.comment = comment;
    }

    /**
     * Contact phone(s)
     **/

    @ApiModelProperty(example = "0172 1781082", value = "Contact phone(s)")
    @JsonProperty("phone")
    public String getPhone() {
      return phone;
    }
    public void setPhone(String phone) {
      this.phone = phone;
    }

    /**
     * Contact email
     **/

    @ApiModelProperty(example = "philipp.prangenberg@gls-germany.com", value = "Contact email")
    @JsonProperty("email")
    public String getEmail() {
      return email;
    }
    public void setEmail(String email) {
      this.email = email;
    }


    @Override
    public boolean equals(java.lang.Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      DeliveryAddress deliveryAddress = (DeliveryAddress) o;
      return Objects.equals(contactName, deliveryAddress.contactName) &&
              Objects.equals(addressLine, deliveryAddress.addressLine) &&
              Objects.equals(zipCode, deliveryAddress.zipCode) &&
              Objects.equals(city, deliveryAddress.city) &&
              Objects.equals(countryCode, deliveryAddress.countryCode) &&
              Objects.equals(comment, deliveryAddress.comment) &&
              Objects.equals(phone, deliveryAddress.phone) &&
              Objects.equals(email, deliveryAddress.email);
    }

    @Override
    public int hashCode() {
      return Objects.hash(contactName, addressLine, zipCode, city, countryCode, comment, phone, email);
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class DeliveryAddress {\n");

      sb.append("    contactName: ").append(toIndentedString(contactName)).append("\n");
      sb.append("    addressLine: ").append(toIndentedString(addressLine)).append("\n");
      sb.append("    zipCode: ").append(toIndentedString(zipCode)).append("\n");
      sb.append("    city: ").append(toIndentedString(city)).append("\n");
      sb.append("    countryCode: ").append(toIndentedString(countryCode)).append("\n");
      sb.append("    comment: ").append(toIndentedString(comment)).append("\n");
      sb.append("    phone: ").append(toIndentedString(phone)).append("\n");
      sb.append("    email: ").append(toIndentedString(email)).append("\n");
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

  /**
   * Delivery order identifier in carrier's system
   **/
  
  @ApiModelProperty(example = "1040970039465780", required = true, value = "Delivery order identifier in carrier's system")
  @JsonProperty("incoming_id")
  @NotNull
  public String getIncomingId() {
    return incomingId;
  }
  public void setIncomingId(String incomingId) {
    this.incomingId = incomingId;
  }

  /**
   **/
  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("delivery_option")
  @NotNull
  public DeliveryOption getDeliveryOption() {
    return deliveryOption;
  }
  public void setDeliveryOption(DeliveryOption deliveryOption) {
    this.deliveryOption = deliveryOption;
  }

  /**
   **/
  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("source_address")
  @NotNull
  public DeliveryAddress getSourceAddress() {
    return sourceAddress;
  }
  public void setSourceAddress(DeliveryAddress sourceAddress) {
    this.sourceAddress = sourceAddress;
  }

  /**
   **/
  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("target_address")
  @NotNull
  public DeliveryAddress getTargetAddress() {
    return targetAddress;
  }
  public void setTargetAddress(DeliveryAddress targetAddress) {
    this.targetAddress = targetAddress;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeliveryOrder deliveryOrder = (DeliveryOrder) o;
    return Objects.equals(incomingId, deliveryOrder.incomingId) &&
        Objects.equals(deliveryOption, deliveryOrder.deliveryOption) &&
        Objects.equals(sourceAddress, deliveryOrder.sourceAddress) &&
        Objects.equals(targetAddress, deliveryOrder.targetAddress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(incomingId, deliveryOption, sourceAddress, targetAddress);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DeliveryOrder {\n");
    
    sb.append("    incomingId: ").append(toIndentedString(incomingId)).append("\n");
    sb.append("    deliveryOption: ").append(toIndentedString(deliveryOption)).append("\n");
    sb.append("    sourceAddress: ").append(toIndentedString(sourceAddress)).append("\n");
    sb.append("    targetAddress: ").append(toIndentedString(targetAddress)).append("\n");
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

