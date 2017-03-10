package org.deku.leoz.rest.entity.zalando.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Objects;

@ApiModel(description="Problem details description (see https://tools.ietf.org/html/rfc7807)")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2017-03-10T11:34:55.297Z")
public class Problem   {
  
  private String type = null;
  private String instance = null;
  private String title = null;
  private BigDecimal status = null;
  private String details = null;

  public Problem(String type, String instance, String title, BigDecimal status, String details) {
    this.type = type;
    this.instance = instance;
    this.title = title;
    this.status = status;
    this.details = details;
  }

  /**
   * Problem type
   **/
  
  @ApiModelProperty(example = "null", required = true, value = "Problem type")
  @JsonProperty("type")
  @NotNull
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Problem instance
   **/
  
  @ApiModelProperty(example = "null", value = "Problem instance")
  @JsonProperty("instance")
  public String getInstance() {
    return instance;
  }
  public void setInstance(String instance) {
    this.instance = instance;
  }

  /**
   * Problem title
   **/
  
  @ApiModelProperty(example = "null", required = true, value = "Problem title")
  @JsonProperty("title")
  @NotNull
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Problem status
   **/
  
  @ApiModelProperty(example = "null", required = true, value = "Problem status")
  @JsonProperty("status")
  @NotNull
  public BigDecimal getStatus() {
    return status;
  }
  public void setStatus(BigDecimal status) {
    this.status = status;
  }

  /**
   * Problem detail
   **/
  
  @ApiModelProperty(example = "null", value = "Problem detail")
  @JsonProperty("details")
  public String getDetails() {
    return details;
  }
  public void setDetails(String details) {
    this.details = details;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Problem problem = (Problem) o;
    return Objects.equals(type, problem.type) &&
        Objects.equals(instance, problem.instance) &&
        Objects.equals(title, problem.title) &&
        Objects.equals(status, problem.status) &&
        Objects.equals(details, problem.details);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, instance, title, status, details);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Problem {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    instance: ").append(toIndentedString(instance)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    details: ").append(toIndentedString(details)).append("\n");
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

