package org.deku.leoz.central.rest.services.zalando.v1

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.records.SddContzipRecord
import org.deku.leoz.central.data.jooq.tables.records.SddFpcsOrderRecord
import org.deku.leoz.node.rest.DefaultProblem
import org.deku.leoz.rest.entity.zalando.v1.DeliveryOption
import org.deku.leoz.rest.entity.zalando.v1.DeliveryOrder
import org.deku.leoz.rest.entity.zalando.v1.NotifiedDeliveryOrder
import org.deku.leoz.ws.gls.shipment.*
import org.jooq.DSLContext
import org.jooq.exception.TooManyRowsException
import org.springframework.beans.factory.annotation.Qualifier
import sx.rs.auth.ApiKey
import sx.time.replaceDate
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

    @Inject
    private lateinit var glsShipmentProcessingService: org.deku.leoz.ws.gls.shipment.ShipmentProcessingPortType

    private val API_KEY: String = "a2ad4a5d-0f7b-4bcb-8d6e-fa18da86fd22"

    /**
     *
     */
    override fun postDeliveryOrder(deliveryOrder: DeliveryOrder, authorizationKey: String): NotifiedDeliveryOrder {

        if (authorizationKey != API_KEY) {
            throw DefaultProblem(
                    status = Response.Status.UNAUTHORIZED)
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
                throw DefaultProblem(
                        status = Response.Status.NOT_FOUND,
                        title = "No delivery options found",
                        detail = "No delivery options found for given id")
            }

            val delOptionZip = result.getValue(0, Tables.SDD_CONTZIP.ZIP)
            val targetAddrZip = deliveryOrder.targetAddress.zipCode
            val glsDepot = result.getValue(0, Tables.SDD_CONTACT.ADMINDEPOTNO)

            // Ensure that there is only one record (may be not necessary due to unique/primary key "ID" in table "SDD_ContZip")
            if (result.size != 1) {
                throw DefaultProblem(
                        status = Response.Status.CONFLICT,
                        title = "Multiple delivery options found",
                        detail = "Multiple delivery options found")
            }

            // Make sure that the given zipcode of target address is same of the given delivery option.
            if (!delOptionZip.equals(targetAddrZip, ignoreCase = true)) {
                throw DefaultProblem(
                        title = "Delivery option not matching given address",
                        detail = "The given delivery option with zip code [$delOptionZip] does not match the target address zipcode [$targetAddrZip]")
            }

            val recordCount: Int = dslContext.fetchCount(
                    Tables.SDD_FPCS_ORDER,
                    Tables.SDD_FPCS_ORDER.CUSTOMERS_REFERENCE.eq(deliveryOrder.incomingId))

            if (recordCount > 0) {
                throw DefaultProblem(
                        title = "Duplicate entry",
                        detail = "There is already an record with the given IncomingID [${deliveryOrder.incomingId}]. Multiple IncomingID's are not supported yet.")
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

            /**
             * The UniStation needs an Service to skip manually input.
             * 10:00 worked fine in test scenarios. 12:00 didn't
             */
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

                if (parcelData.size != 1) {
                    fpcsRecord.cancelRequested = -2
                    fpcsRecord.store()
                    throw DefaultProblem(
                            title = "Error serving fpcs",
                            detail = "Central GLS system reported an error")
                }

                /**
                 * TODO: Check if this is the right number.
                 * This could be the preferred way, because this number does not needs to be "converted".
                 * In addition the returned structure is always the same respectively versioned.
                 */
                //val glsParcelNumAlt = parcelData[0].barcodes.uniShip.split("|")[17].substring(0, 11)

                val courierNum = parcelData[0].expressData.courierParcelNumber

                var glsParcelNum = courierNum
                glsParcelNum = glsParcelNum.substring(1, 3) + "85" + glsParcelNum.substring(4, 11)
                fpcsRecord.glsTrackid = parcelData[0].trackID
                fpcsRecord.glsParcelno = glsParcelNum.toDouble()

                fpcsRecord.store()

                //Check if GLS Parcel number is within the assigned range. Otherwise cancel the order and throw a problem.
                val checkRange = dslContext.fetchCount(
                        Tables.TBLSYSCOLLECTIONS
                                .join(Tables.SDD_CUSTOMER)
                                    .on(Tables.TBLSYSCOLLECTIONS.TXTP2
                                        .eq(Tables.SDD_CUSTOMER.CUSTOMERID)),
                        Tables.TBLSYSCOLLECTIONS.TYP.eq(80)
                                .and(Tables.SDD_CUSTOMER.NAME1.eq("Zalando"))
                                .and(Tables.TBLSYSCOLLECTIONS.TXTVALUE.lessOrEqual(courierNum))
                                .and(Tables.TBLSYSCOLLECTIONS.TXTP1.greaterOrEqual(courierNum)))

                if (checkRange > 0) {
                    return NotifiedDeliveryOrder(glsParcelNum, "https://gls-group.eu/DE/de/paketverfolgung?match=$glsParcelNum")
                } else {
                    cancelDeliveryOrder(glsParcelNum, authorizationKey)
                    throw DefaultProblem(
                            title = "Customers range exceeded.",
                            detail = "The order could not be processed due to an leaked customers range. Contact GLS Support ASAP!")
                }

            } catch(d: DefaultProblem) { //Don't catch a thrown DefaultProblem as a general Exception. These are supposed to be thrown.
                throw d
            } catch (e: Exception) {
                fpcsRecord.cancelRequested = -2
                fpcsRecord.store()
                throw DefaultProblem(
                        title = "Error serving GLS systems",
                        detail = "The order could not be stored in GLS Systems due to an error: ${e.message}")
            }
        } catch(d: DefaultProblem) { //Don't catch a thrown DefaultProblem as a general Exception. These are supposed to be thrown.
            throw d
        } catch(e: Exception) {
            throw DefaultProblem(
                    title = "Unhandled exception",
                    detail = e.message)
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
            throw DefaultProblem(
                    status = Response.Status.UNAUTHORIZED)
        }

        try {
            val sddRoute: SddContzipRecord = dslContext.fetchOne(
                    Tables.SDD_CONTZIP,
                    Tables.SDD_CONTZIP.ZIP
                            .eq(target_address_zip_code)
                            .and(Tables.SDD_CONTZIP.LAYER.between(5, 6))
            ) ?: return mutableListOf() //Return empty list if given zip-code does not match any Zalando SDD-Area

            val currentDate = Date()
            return listOf(DeliveryOption(
                    sddRoute.id.toString(),
                    sddRoute.cutOff.replaceDate(currentDate),
                    sddRoute.ltop.replaceDate(currentDate),
                    sddRoute.etod.replaceDate(currentDate),
                    sddRoute.ltod.replaceDate(currentDate)))

        } catch(e: TooManyRowsException) {
            /**
             * Make sure that zip-codes do not overlap within Zalando areas.
             * If so, check sdd_contzip Zalando-layers for duplicate entries.
             */
            throw DefaultProblem(
                    title = "Too many delivery options found",
                    detail = "The given zip code is not unique")
        } catch (e: Exception) {
            throw DefaultProblem(
                    title = "Unhandled exception",
                    detail = e.message)
        }
    }

    /**
     *
     */
    override fun cancelDeliveryOrder(id: String, authorizationKey: String): Response {

        if (authorizationKey != API_KEY) {
            throw DefaultProblem(
                    status = Response.Status.UNAUTHORIZED)
        }

        try {
            val order: SddFpcsOrderRecord = dslContext.fetchOne(
                    Tables.SDD_FPCS_ORDER,
                    Tables.SDD_FPCS_ORDER.GLS_PARCELNO
                            .eq(id.toDouble())
            ) ?: throw DefaultProblem(
                    status = Response.Status.NOT_FOUND,
                    detail = "No order with id [$id] found")

            if (order.cancelRequested == -1) {
                throw DefaultProblem(
                        detail = "Cancellation for order with id [$id] already requested")
            }

            order.cancelRequested = -1
            order.store()

            val cancelResponse: CancelParcelResponse = glsShipmentProcessingService.cancelParcelByID(order.glsTrackid)

            if (cancelResponse.result != null) {
                if (cancelResponse.result.equals("CANCELLATION_PENDING", ignoreCase = true) || cancelResponse.result.equals("CANCELLED", ignoreCase = true)) { //TODO Check for other possible results
                    return Response.ok().build()
                } else {
                    throw DefaultProblem(
                            title = "Cancellation failed",
                            detail = "Cancellation failed with response [${cancelResponse.result}]")
                }
            }

            throw BadRequestException()

        } catch(e: Exception) {
            throw DefaultProblem(
                    title = "Cancellation failed",
                    detail = "Cancellation for order with id [$id] failed due to an unhandled exception: ${e.message}")
        }
    }
}