package org.deku.leoz.smartlane.model

/**
 * Created by masc on 20.11.17.
 */
fun Address.toRouteDeliveryInput(customId: String): Routedeliveryinput =
        Routedeliveryinput().also {
            it.contactcompany = this.contactcompany
            it.contactfirstname = this.contactfirstname
            it.contactlastname = this.contactlastname
            it.street = this.street
            it.housenumber = this.housenumber
            it.postalcode = this.postalcode
            it.city = this.city
            it.country = this.country
            it.phonenr = this.phonenr
            it.email = this.email
            it.customId = customId
        }
