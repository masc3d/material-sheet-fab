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
import org.deku.leoz.time.toShortTime
import org.deku.leoz.ws.gls.shipment.*
import org.jooq.DSLContext
import org.jooq.Result
import org.jooq.exception.TooManyRowsException
import org.springframework.beans.factory.annotation.Qualifier
import sx.rs.ApiKey
import sx.time.replaceDate
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.BadRequestException
import javax.ws.rs.Path
import javax.ws.rs.core.Response
import java.util.Locale
import java.text.SimpleDateFormat
import java.text.DateFormat



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

    @Inject
    private lateinit var glsShipmentProcessingService: org.deku.leoz.ws.gls.shipment.ShipmentProcessingPortType

    private val API_KEY: String = "a2ad4a5d-0f7b-4bcb-8d6e-fa18da86fd22"

    /**
     *
     */
    override fun postDeliveryOrder(deliveryOrder: DeliveryOrder, authorizationKey: String): NotifiedDeliveryOrder {

        if (authorizationKey != API_KEY) {
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
                            .and(Tables.SDD_CONTZIP.ID.eq(deliveryOrder.deliveryOption.id!!.toInt())))
                    .fetch()

            if (result.size == 0) {
                throw ServiceException(Problem("Delivery option not valid", "No delivery options found with given ID. Contact GLS SDD-Team!"))
            }

            val delOptionZip = result.getValue(0, Tables.SDD_CONTZIP.ZIP)
            val targetAddrZip = deliveryOrder.targetAddress.zipCode
            val glsDepot = result.getValue(0, Tables.SDD_CONTACT.ADMINDEPOTNO)

            // Ensure that there is only one record (may be not necessary due to unique/primary key "ID" in table "SDD_ContZip")
            if (result.size != 1) {
                throw ServiceException(Problem("Multiple delivery options", "Multiple delivery options found. Contact GLS SDD-Team!"))
            }

            // Make sure that the given zipcode of target address is same of the given delivery option.
            if (!delOptionZip.equals(targetAddrZip, ignoreCase = true)) {
                throw ServiceException(Problem("Delivery Option not matching given address.", "The given delivery option [ZIP: $delOptionZip] does not match the given target address zipcode [$targetAddrZip]"))
            }

            val recordCount: Int = dslContext.fetchCount(
                    Tables.SDD_FPCS_ORDER,
                    Tables.SDD_FPCS_ORDER.CUSTOMERS_REFERENCE.eq(deliveryOrder.incomingId))

            if (recordCount > 0) {
                throw ServiceException(Problem(title = "Duplicate entry", details = "There is already an record with the given IncomingID [" + deliveryOrder.incomingId + "]. Multiple IncomingID's are not supported yet."))
            }

            val fpcsRecord: SddFpcsOrderRecord = dslContext.newRecord(Tables.SDD_FPCS_ORDER)
            fpcsRecord.customersReference = deliveryOrder.incomingId
            fpcsRecord.customerNo = result.getValue(0, Tables.SDD_CUSTOMER.CUSTOMERID)
            fpcsRecord.contactNo = result.getValue(0, Tables.SDD_CONTACT.CONTACTID)
            fpcsRecord.zipcodeRef = deliveryOrder.deliveryOption.id!!.toInt()
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
             * Call GLS FPCS (SOAP) Service and provide order information. Process returned data
             * Update local record with GLS parcel number which is returned by the FPCS service.
             * Return the obtained parcel number to the originally "requester" of this service (Zalando).
             */
            val consignee = Consignee()
            val consAddr = Address()
            consAddr.city = fpcsRecord.cityTo
            consAddr.name1 = fpcsRecord.nameTo
            consAddr.contactPerson = fpcsRecord.nameTo
            consAddr.countryCode = fpcsRecord.countryTo
            consAddr.zipCode = fpcsRecord.zipTo
            consAddr.street = fpcsRecord.streetTo
            consAddr.streetNumber = fpcsRecord.streetnoTo
            consignee.address = consAddr

            val shipper = Shipper()
            shipper.id = result.getValue(0, Tables.SDD_CONTACT.CONTACTID)

            val shipmentUnit = ShipmentUnit()
            shipmentUnit.weight = "1"

            val shipment = Shipment()
            shipment.shipper = shipper
            shipment.referenceNumber.add(fpcsRecord.customersReference)
            shipment.product = ProductType.EXPRESS
            shipment.consignee = consignee
            shipment.shipper = shipper
            shipment.shipmentUnit.add(shipmentUnit)

            val returnLabels = ReturnLabels()
            returnLabels.templateSet = TemplateSet.NONE
            returnLabels.labelFormat = LabelFormat.PDF

            val printOptions = PrintingOptions()
            printOptions.returnLabels = returnLabels

            val service = Service()
            service.serviceName = "service_1000"

            val shippingService = ShipmentService()
            shippingService.service = service

            val shipmentRequestData: ShipmentRequestData = ShipmentRequestData()
            shipmentRequestData.shipment = shipment
            shipmentRequestData.printingOptions = printOptions
            shipmentRequestData.shipment.service.add(shippingService)

            val glsResponse: CreateParcelsResponse
            try {
                glsResponse = glsShipmentProcessingService.createParcels(shipmentRequestData)
                val parcelData = glsResponse.createdShipment.parcelData

                if (parcelData.size != 1){
                    fpcsRecord.cancelRequested = -2
                    fpcsRecord.store()
                    throw ServiceException(Problem(title = "Error serving FPCS", details = "Error processing Parceldata to central GLS System. Contact GLS SDD-Team!"))
                }

                //TODO: Check if this is the right number.
                //val glsParcelNumAlt = parcelData[0].barcodes.uniShip.split("|")[18]

                var glsParcelNum = parcelData[0].expressData.courierParcelNumber
                glsParcelNum = glsParcelNum.substring(1, 3) + "85" + glsParcelNum.substring(4)
                fpcsRecord.glsTrackid = parcelData[0].trackID
                fpcsRecord.glsParcelno = glsParcelNum.toDouble()

                fpcsRecord.store()

                return NotifiedDeliveryOrder(glsParcelNum)

            } catch (e: Exception) {
                fpcsRecord.cancelRequested = -2
                fpcsRecord.store()
                throw ServiceException(Problem(title = "Error serving GLS Systems", details = "The order could not be stored in GLS Systems due to an error. Message: " + e.message), Response.Status.BAD_REQUEST)
            }
        } catch (s: ServiceException) {
            throw s
        } catch(e: Exception) {
            throw ServiceException(Problem(title = "Unhandled Exception", details = "Exception message: " + e.message), Response.Status.BAD_REQUEST)
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
            String, authorizationKey: String): List<DeliveryOption> {

        if (authorizationKey != API_KEY) {
            throw ServiceException(status = Response.Status.UNAUTHORIZED, entity = Problem())
        }

        try {
            val sddRoute: SddContzipRecord = dslContext.fetchOne(
                    Tables.SDD_CONTZIP,
                    Tables.SDD_CONTZIP.ZIP
                            .eq(target_address_zip_code)
                            .and(Tables.SDD_CONTZIP.LAYER.between(5, 6))
            ) ?: throw ServiceException(status = Response.Status.BAD_REQUEST, entity = Problem(
                    type = "",
                    instance = "",
                    title = "No Delivery Option found!",
                    details = "The given zip-code is not part of the defined delivery area"))

            val currentDate = Date()
            return listOf(DeliveryOption(
                    sddRoute.id.toString(),
                    sddRoute.cutOff.replaceDate(currentDate),
                    sddRoute.ltop.replaceDate(currentDate),
                    sddRoute.etod.replaceDate(currentDate),
                    sddRoute.ltod.replaceDate(currentDate)))

        } catch (s: ServiceException) {
            throw s
        } catch(e: TooManyRowsException) {
            throw ServiceException(status = Response.Status.BAD_REQUEST, entity = Problem(
                    type = "",
                    instance = "",
                    title = "Too many delivery options found.",
                    details = "The given ZipCode is not unique. Contact GLS SDD Team!"))
        } catch (e: Exception) {
            throw ServiceException(status = Response.Status.BAD_REQUEST, entity = Problem(
                    type = "",
                    instance = "",
                    title = "Unhandeled Exception!",
                    details = "Exception: {$e}"))
        }
    }

    /**
     *
     */
    override fun cancelDeliveryOrder(id: String, authorizationKey: String): Response {

        if (authorizationKey != API_KEY) {
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

            val cancelResponse: CancelParcelResponse = glsShipmentProcessingService.cancelParcelByID(order.glsTrackid)

            if (cancelResponse.result != null) {
                if (cancelResponse.result.equals("CANCELLATION_PENDING", ignoreCase = true) || cancelResponse.result.equals("CANCELLED", ignoreCase = true)) { //TODO Check for other possible results
                    return Response.ok().build()
                } else {
                    throw BadRequestException("Cancellation failed. Response: [" + cancelResponse.result + "]")
                }
            }

            throw BadRequestException()

        } catch (s: ServiceException) {
            throw s
        } catch (b: BadRequestException) {
            throw b
        } catch(e: Exception) {
            throw ServiceException(status = Response.Status.BAD_REQUEST, entity = Problem(
                    type = "PROBLEM TYPE",
                    instance = "INSTANCE",
                    title = "Unhandled Exception",
                    details = "Cancellation for Order with ID [$id] failed due to an unhandled exception!"))
        }
    }
}