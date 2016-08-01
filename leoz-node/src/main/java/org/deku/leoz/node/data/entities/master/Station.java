package org.deku.leoz.node.data.entities.master;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by JT on 11.05.15.
 */
@Entity
@Table(name = "mst_station")
public class Station implements Serializable {
    private static final long serialVersionUID = 7124430646756787268L;

//    private Integer stationId;
//
//    @Id
//    public Integer getStationId() {
//        return stationId;
//    }
//
//    public void setStationId(Integer stationId) {
//        this.stationId = stationId;
//    }


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    private Integer stationNr;

    @Id
    public Integer getStationNr() {
        return stationNr;
    }

    public void setStationNr(Integer stationNr) {
        this.stationNr = stationNr;
    }

    private Timestamp timestamp;

    @Basic
    @Column(nullable = false)
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
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

    private Long syncId;

    @Basic
    @Column(nullable = false)
    public Long getSyncId() {
        return syncId;
    }

    public void setSyncId(Long syncId) {
        this.syncId = syncId;
    }
}
