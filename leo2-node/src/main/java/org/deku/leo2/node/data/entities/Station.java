package org.deku.leo2.node.data.entities;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by JT on 11.05.15.
 */
@Entity
@Table(name = "mst_station")
public class Station implements Serializable {
    private static final long serialVersionUID = 7124430646756787268L;

    private Integer stationId;

    @Id
    public Integer getStationId() {
        return stationId;
    }

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }

    private String address1;

    @Basic
    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    private String address2;

    @Basic
    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    private String country;

    @Basic
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    private String zip;

    @Basic
    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    private String city;

    @Basic
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    private String street;

    @Basic
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    private String houseNr;

    @Basic
    public String getHouseNr() {
        return houseNr;
    }

    public void setHouseNr(String houseNr) {
        this.houseNr = houseNr;
    }

    private String phone1;

    @Basic
    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    private String phone2;

    @Basic
    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    private String telefax;

    @Basic
    public String getTelefax() {
        return telefax;
    }

    public void setTelefax(String telefax) {
        this.telefax = telefax;
    }

    private String mobile;

    @Basic
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    private String servicePhone1;

    @Basic
    public String getServicePhone1() {
        return servicePhone1;
    }

    public void setServicePhone1(String servicePhone1) {
        this.servicePhone1 = servicePhone1;
    }

    private String servicePhone2;

    @Basic
    public String getServicePhone2() {
        return servicePhone2;
    }

    public void setServicePhone2(String servicePhone2) {
        this.servicePhone2 = servicePhone2;
    }

    private String contactPerson1;

    @Basic
    public String getContactPerson1() {
        return contactPerson1;
    }

    public void setContactPerson1(String contactPerson1) {
        this.contactPerson1 = contactPerson1;
    }

    private String contactPerson2;

    @Basic
    public String getContactPerson2() {
        return contactPerson2;
    }

    public void setContactPerson2(String contactPerson2) {
        this.contactPerson2 = contactPerson2;
    }

    private String email;

    @Basic
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String webAddress;

    @Basic
    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

    private Integer stationNr;

    @Basic
    public Integer getStationNr() {
        return stationNr;
    }

    public void setStationNr(Integer stationNr) {
        this.stationNr = stationNr;
    }

    private Integer strang;

    @Basic
    public Integer getStrang() {
        return strang;
    }

    public void setStrang(Integer strang) {
        this.strang = strang;
    }

    private Double posLong;

    @Basic
    public Double getPosLong() {
        return posLong;
    }

    public void setPosLong(Double posLong) {
        this.posLong = posLong;
    }

    private Double posLat;

    @Basic
    public Double getPosLat() {
        return posLat;
    }

    public void setPosLat(Double posLat) {
        this.posLat = posLat;
    }

    private String sector;

    @Basic
    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    private String uStId;

    @Basic
    public String getuStId() {
        return uStId;
    }

    public void setuStId(String uStId) {
        this.uStId = uStId;
    }

    private String billingAddress1;

    @Basic
    public String getBillingAddress1() {
        return billingAddress1;
    }

    public void setBillingAddress1(String billingAddress1) {
        this.billingAddress1 = billingAddress1;
    }

    private String billingAddress2;

    @Basic
    public String getBillingAddress2() {
        return billingAddress2;
    }

    public void setBillingAddress2(String billingAddress2) {
        this.billingAddress2 = billingAddress2;
    }

    private String billingCountry;

    @Basic
    public String getBillingCountry() {
        return billingCountry;
    }

    public void setBillingCountry(String billingCountry) {
        this.billingCountry = billingCountry;
    }

    private String billingZip;

    @Basic
    public String getBillingZip() {
        return billingZip;
    }

    public void setBillingZip(String billingZip) {
        this.billingZip = billingZip;
    }

    private String billingCity;

    @Basic
    public String getBillingCity() {
        return billingCity;
    }

    public void setBillingCity(String billingCity) {
        this.billingCity = billingCity;
    }

    private String billingStreet;

    @Basic
    public String getBillingStreet() {
        return billingStreet;
    }

    public void setBillingStreet(String billingStreet) {
        this.billingStreet = billingStreet;
    }

    private String billingHouseNr;

    @Basic
    public String getBillingHouseNr() {
        return billingHouseNr;
    }

    public void setBillingHouseNr(String billingHouseNr) {
        this.billingHouseNr = billingHouseNr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Station station = (Station) o;

        if (stationId != null ? !stationId.equals(station.stationId) : station.stationId != null) return false;
        if (address1 != null ? !address1.equals(station.address1) : station.address1 != null) return false;
        if (address2 != null ? !address2.equals(station.address2) : station.address2 != null) return false;
        if (country != null ? !country.equals(station.country) : station.country != null) return false;
        if (zip != null ? !zip.equals(station.zip) : station.zip != null) return false;
        if (city != null ? !city.equals(station.city) : station.city != null) return false;
        if (street != null ? !street.equals(station.street) : station.street != null) return false;
        if (houseNr != null ? !houseNr.equals(station.houseNr) : station.houseNr != null) return false;
        if (phone1 != null ? !phone1.equals(station.phone1) : station.phone1 != null) return false;
        if (phone2 != null ? !phone2.equals(station.phone2) : station.phone2 != null) return false;
        if (telefax != null ? !telefax.equals(station.telefax) : station.telefax != null) return false;
        if (mobile != null ? !mobile.equals(station.mobile) : station.mobile != null) return false;
        if (servicePhone1 != null ? !servicePhone1.equals(station.servicePhone1) : station.servicePhone1 != null)
            return false;
        if (servicePhone2 != null ? !servicePhone2.equals(station.servicePhone2) : station.servicePhone2 != null)
            return false;
        if (contactPerson1 != null ? !contactPerson1.equals(station.contactPerson1) : station.contactPerson1 != null)
            return false;
        if (contactPerson2 != null ? !contactPerson2.equals(station.contactPerson2) : station.contactPerson2 != null)
            return false;
        if (email != null ? !email.equals(station.email) : station.email != null) return false;
        if (webAddress != null ? !webAddress.equals(station.webAddress) : station.webAddress != null) return false;
        if (stationNr != null ? !stationNr.equals(station.stationNr) : station.stationNr != null) return false;
        if (strang != null ? !strang.equals(station.strang) : station.strang != null) return false;
        if (posLong != null ? !posLong.equals(station.posLong) : station.posLong != null) return false;
        if (posLat != null ? !posLat.equals(station.posLat) : station.posLat != null) return false;
        if (sector != null ? !sector.equals(station.sector) : station.sector != null) return false;
        if (uStId != null ? !uStId.equals(station.uStId) : station.uStId != null) return false;
        if (billingAddress1 != null ? !billingAddress1.equals(station.billingAddress1) : station.billingAddress1 != null)
            return false;
        if (billingAddress2 != null ? !billingAddress2.equals(station.billingAddress2) : station.billingAddress2 != null)
            return false;
        if (billingCountry != null ? !billingCountry.equals(station.billingCountry) : station.billingCountry != null)
            return false;
        if (billingZip != null ? !billingZip.equals(station.billingZip) : station.billingZip != null) return false;
        if (billingCity != null ? !billingCity.equals(station.billingCity) : station.billingCity != null) return false;
        if (billingStreet != null ? !billingStreet.equals(station.billingStreet) : station.billingStreet != null)
            return false;
        if (billingHouseNr != null ? !billingHouseNr.equals(station.billingHouseNr) : station.billingHouseNr != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = stationId != null ? stationId.hashCode() : 0;
        result = 31 * result + (address1 != null ? address1.hashCode() : 0);
        result = 31 * result + (address2 != null ? address2.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (zip != null ? zip.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (street != null ? street.hashCode() : 0);
        result = 31 * result + (houseNr != null ? houseNr.hashCode() : 0);
        result = 31 * result + (phone1 != null ? phone1.hashCode() : 0);
        result = 31 * result + (phone2 != null ? phone2.hashCode() : 0);
        result = 31 * result + (telefax != null ? telefax.hashCode() : 0);
        result = 31 * result + (mobile != null ? mobile.hashCode() : 0);
        result = 31 * result + (servicePhone1 != null ? servicePhone1.hashCode() : 0);
        result = 31 * result + (servicePhone2 != null ? servicePhone2.hashCode() : 0);
        result = 31 * result + (contactPerson1 != null ? contactPerson1.hashCode() : 0);
        result = 31 * result + (contactPerson2 != null ? contactPerson2.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (webAddress != null ? webAddress.hashCode() : 0);
        result = 31 * result + (stationNr != null ? stationNr.hashCode() : 0);
        result = 31 * result + (strang != null ? strang.hashCode() : 0);
        result = 31 * result + (posLong != null ? posLong.hashCode() : 0);
        result = 31 * result + (posLat != null ? posLat.hashCode() : 0);
        result = 31 * result + (sector != null ? sector.hashCode() : 0);
        result = 31 * result + (uStId != null ? uStId.hashCode() : 0);
        result = 31 * result + (billingAddress1 != null ? billingAddress1.hashCode() : 0);
        result = 31 * result + (billingAddress2 != null ? billingAddress2.hashCode() : 0);
        result = 31 * result + (billingCountry != null ? billingCountry.hashCode() : 0);
        result = 31 * result + (billingZip != null ? billingZip.hashCode() : 0);
        result = 31 * result + (billingCity != null ? billingCity.hashCode() : 0);
        result = 31 * result + (billingStreet != null ? billingStreet.hashCode() : 0);
        result = 31 * result + (billingHouseNr != null ? billingHouseNr.hashCode() : 0);
        return result;
    }
}
