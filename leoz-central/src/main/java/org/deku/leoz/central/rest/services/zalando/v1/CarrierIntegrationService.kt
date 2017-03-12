package org.deku.leoz.central.rest.services.zalando.v1

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.records.SddContzipRecord
import org.deku.leoz.central.data.jooq.tables.records.SddFpcsOrderRecord
import org.deku.leoz.node.rest.ServiceException
import org.deku.leoz.rest.entity.zalando.v1.DeliveryOption
import org.deku.leoz.rest.entity.zalando.v1.DeliveryOrder
import org.deku.leoz.rest.entity.zalando.v1.NotifiedDeliveryOrder
import org.deku.leoz.rest.entity.zalando.v1.Problem
import org.jooq.DSLContext
import org.jooq.Result
import org.jooq.exception.TooManyRowsException
import org.springframework.beans.factory.annotation.Qualifier
import sx.rs.ApiKey
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.BadRequestException
import javax.ws.rs.Path
import javax.ws.rs.core.Response

/**
 * Zalanda carrier integration service
 * Created by 27694066 on 02.03.2017.
 */
@Named
@ApiKey(false)
@Path("zalando/v1/ldn")
class CarrierIntegrationService : org.deku.leoz.rest.service.zalando.v1.CarrierIntegrationService {

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext

    /**
     *
     */
    override fun postDeliveryOrder(deliveryOrder: DeliveryOrder, authorizationKey: String): NotifiedDeliveryOrder {

        if (authorizationKey != "APIKEY") {
            throw ServiceException(
                    status = Response.Status.UNAUTHORIZED,
                    entity = Problem())
        }

        try {
            val result = dslContext.select()
                    .from(Tables.SDD_CUSTOMER
                            .join(Tables.SDD_CONTACT).on(Tables.SDD_CUSTOMER.CUSTOMERID.equal(Tables.SDD_CONTACT.CUSTOMERID))
                            .join(Tables.SDD_CONTZIP).on(Tables.SDD_CONTACT.ZIPLAYER.equal(Tables.SDD_CONTZIP.LAYER)))
                    .where(Tables.SDD_CUSTOMER.NAME1.equal("Zalando")
                            .and(Tables.SDD_CONTZIP.ID.eq(deliveryOrder.deliveryOption.id.toInt())))
                    .fetch()

            val delOptionZip = result.getValue(0, Tables.SDD_CONTZIP.ZIP)
            val targetAddrZip = deliveryOrder.targetAddress.zipCode

            // Ensure that there is only one record (may be not necessary due to unique/primary key "ID" in table "SDD_ContZip")
            if (result.size != 1) {
                throw ServiceException(Problem("Multiple delivery options", "Multiple delivery options found. Contact GLS SDD-Team!"))
            }

            // Make sure that the given zipcode of target address is same of the given delivery option.
            if (!delOptionZip.equals(targetAddrZip, ignoreCase = true)) {
                throw ServiceException(Problem("Delivery Option not matching given address.", "The given delivery option [ZIP: $delOptionZip] does not match the given target address zipcode [$targetAddrZip]"))
            }

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

            /**
             * TODO: Call GLS FPCS (SOAP) Service and provide order information. Process returned data
             * Update local record with GLS parcel number which is returned by the FPCS service.
             * Return the obtained parcel number to the originally "requester" of this service (Zalando).
             */

            val glsParcelNum: String = "12345678901" //TODO: To be replaced with "real" GLS parcel number

            fpcsRecord.glsParcelno = glsParcelNum.toDouble()
            fpcsRecord.store()

            return NotifiedDeliveryOrder(glsParcelNum)

        } catch(e: Exception) {
            throw BadRequestException()
        }
    }

    /**
     *
     */
    override fun requestDeliveryOption(
            source_address_country_code: String,
            source_address_city: String,
            source_address_zip_code: String,
            source_address_address_line: String,
            target_address_country_code: String,
            target_address_city: String,
            target_address_zip_code: String,
            target_address_address_line:
            String, authorizationKey: String): DeliveryOption {

        if (authorizationKey != "APIKEY") {
            throw ServiceException(status = Response.Status.UNAUTHORIZED, entity = Problem())
        }

        try {
            val sddRoute: SddContzipRecord = dslContext.fetchOne(
                    Tables.SDD_CONTZIP,
                    Tables.SDD_CONTZIP.ZIP
                            .eq(target_address_zip_code)
                            .and(Tables.SDD_CONTZIP.LAYER.between(5, 6))
            ) ?: throw ServiceException(status = Response.Status.BAD_REQUEST, entity = Problem(
                    type = "https://problem.io",
                    instance = "/delivery-options",
                    title = "No Delivery Option found!",
                    details = "The given zip-code is not part of the defined delivery area"))

            return DeliveryOption(
                    sddRoute.id.toString(),
                    sddRoute.cutOff,
                    sddRoute.ltop,
                    sddRoute.etod,
                    sddRoute.ltod)

        } catch(e: TooManyRowsException) {
            throw ServiceException(status = Response.Status.BAD_REQUEST, entity = Problem(
                    type = "https://problem.io",
                    instance = "/delivery-options",
                    title = "Too many delivery options found.",
                    details = "The given ZipCode is not unique. Contact GLS SDD Team!"))
        } catch (e: Exception) {
            throw ServiceException(status = Response.Status.BAD_REQUEST, entity = Problem(
                    type = "https://problem.io",
                    instance = "/delivery-options",
                    title = "Unhandeled Exception!",
                    details = "Exception: {$e}"))
        }
    }

    /**
     *
     */
    override fun cancelDeliveryOrder(id: String, authorizationKey: String) {

        if (authorizationKey != "APIKEY") {
            throw ServiceException(status = Response.Status.UNAUTHORIZED, entity = Problem())
        }

        try {
            val order: SddFpcsOrderRecord = dslContext.fetchOne(
                    Tables.SDD_FPCS_ORDER,
                    Tables.SDD_FPCS_ORDER.CUSTOMERS_REFERENCE
                            .eq(id)
            ) ?: throw ServiceException(status = Response.Status.BAD_REQUEST, entity = Problem(
                    type = "PROBLEM TYPE",
                    instance = "INSTANCE",
                    title = "No record found",
                    details = "No Order with ID [$id] found!"))

            if (order.cancelRequested == -1) {
                throw ServiceException(status = Response.Status.BAD_REQUEST, entity = Problem(
                        type = "PROBLEM TYPE",
                        instance = "INSTANCE",
                        title = "Duplicate request",
                        details = "Cancellation for Order with ID [$id] already requested!"))
            }

            order.cancelRequested = -1
            order.store()

            //TODO Cancel GLS Order via FPCS, return OK if successful
        } catch(e: Exception) {
            throw ServiceException(status = Response.Status.BAD_REQUEST, entity = Problem(
                    type = "PROBLEM TYPE",
                    instance = "INSTANCE",
                    title = "Unhandled Exception",
                    details = "Cancellation for Order with ID [$id] failed due to an unhandled exception!"))
        }
    }
}