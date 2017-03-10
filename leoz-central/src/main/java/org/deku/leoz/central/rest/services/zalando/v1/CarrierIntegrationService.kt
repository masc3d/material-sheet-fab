package org.deku.leoz.central.rest.services.zalando.v1

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.SddContzip
import org.deku.leoz.central.data.jooq.tables.SddFpcsOrder
import org.deku.leoz.central.data.jooq.tables.records.SddContzipRecord
import org.deku.leoz.central.data.jooq.tables.records.SddFpcsOrderRecord
import org.deku.leoz.rest.entity.zalando.v1.DeliveryOption
import org.deku.leoz.rest.entity.zalando.v1.DeliveryOrder
import org.deku.leoz.rest.entity.zalando.v1.Problem
import org.jooq.DSLContext
import org.jooq.Field
import org.springframework.beans.factory.annotation.Qualifier
import sx.rs.ApiKey
import java.math.BigDecimal
import java.sql.Date
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.BadRequestException
import javax.ws.rs.HeaderParam
import javax.ws.rs.NotAuthorizedException
import javax.ws.rs.Path
import javax.ws.rs.core.Response

/**
 * Created by 27694066 on 02.03.2017.
 */
@Named
@ApiKey(false)
@Path("zalando/v1/ldn")
class CarrierIntegrationService :  org.deku.leoz.rest.service.zalando.v1.CarrierIntegrationService {

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext

    override fun postDeliveryOrder(deliveryOrder: DeliveryOrder): Response {
        var response: Response? = null
        try{
            val sddRoute: SddContzipRecord = dslContext.fetchOne(
                    Tables.SDD_CONTZIP,
                    Tables.SDD_CONTZIP.ZIP
                            .eq(deliveryOrder.targetAddress.zipCode)
                            .and(Tables.SDD_CONTZIP.LAYER.between(5,6)))

            val fpcsRecord: SddFpcsOrderRecord = dslContext.newRecord(Tables.SDD_FPCS_ORDER)
            fpcsRecord.customersReference = deliveryOrder.incomingId
            fpcsRecord.customerNo = "CUSTOMERNO"
            fpcsRecord.contactNo = "CONTACTNO"
            fpcsRecord.zipcodeRef = deliveryOrder.deliveryOption.id.toInt()
            fpcsRecord.nameFrom = deliveryOrder.sourceAddress.contactName
            fpcsRecord.streetFrom = deliveryOrder.sourceAddress.addressLine
            fpcsRecord.cityFrom = deliveryOrder.sourceAddress.city
            fpcsRecord.countryFrom = deliveryOrder.sourceAddress.countryCode
            fpcsRecord.zipFrom = deliveryOrder.sourceAddress.zipCode
            fpcsRecord.nameTo = deliveryOrder.targetAddress.contactName
            fpcsRecord.streetTo = deliveryOrder.targetAddress.addressLine
            fpcsRecord.cityTo = deliveryOrder.targetAddress.city
            fpcsRecord.countryTo = deliveryOrder.targetAddress.countryCode
            fpcsRecord.zipTo = deliveryOrder.targetAddress.zipCode
            fpcsRecord.mailAddress = deliveryOrder.targetAddress.email
            fpcsRecord.dtShip = java.sql.Date(Calendar.getInstance().timeInMillis)
            fpcsRecord.store()

        }catch(e: Exception){
            throw BadRequestException()
        }
        return response!!
    }

    override fun requestDeliveryOption(source_address_country_code: String, source_address_city: String, source_address_zip_code: String, source_address_address_line: String, target_address_country_code: String, target_address_city: String, target_address_zip_code: String, target_address_address_line: String, authorizationKey: String): Response {

        var response: Response? = null

        if(!authorizationKey.equals("APIKEY")){
            throw NotAuthorizedException(Response.status(401).entity(Problem("", "", "", BigDecimal.ZERO, "")).build())
        }

        try{
            val sddRoute: SddContzipRecord? = dslContext.fetchOne(
                    Tables.SDD_CONTZIP,
                    Tables.SDD_CONTZIP.ZIP
                            .eq(target_address_zip_code)
                            .and(Tables.SDD_CONTZIP.LAYER.between(5,6)))


            val delOption: DeliveryOption = DeliveryOption(
                    sddRoute!!.id.toString(),
                    sddRoute!!.cutOff,
                    sddRoute!!.ltop,
                    sddRoute!!.etod,
                    sddRoute!!.ltod)

            response = Response.ok(delOption).status(200).build()

        }catch (e: NullPointerException){
            response = Response.status(400).entity(Problem(
                    "https://problem.io",
                    "/delivery-options",
                    "No Delivery Option found!",
                    BigDecimal.ZERO,
                    "The given zip-code is not part of the defined delivery area")).build()
        }catch (e: Exception){
            response = Response.status(400).entity(Problem(
                    "https://problem.io",
                    "/delivery-options",
                    "Unhandeled Exception!",
                    BigDecimal.ZERO,
                    "Contact developer!")).build()
        }

        return response!!
    }

}