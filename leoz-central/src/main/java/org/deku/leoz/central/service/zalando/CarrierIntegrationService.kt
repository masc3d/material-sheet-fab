package org.deku.leoz.central.service.zalando

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.SddContzipRecord
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.SddFpcsOrderRecord
import org.deku.leoz.service.zalando.CarrierIntegrationService
import org.deku.leoz.service.zalando.entity.DeliveryOption
import org.deku.leoz.service.zalando.entity.DeliveryOrder
import org.deku.leoz.service.zalando.entity.NotifiedDeliveryOrder
import org.deku.leoz.ws.gls.shipment.*
import org.jooq.DSLContext
import org.jooq.exception.TooManyRowsException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import sx.rs.RestProblem
import sx.time.plusDays
import sx.time.replaceDate
import sx.time.replaceTime
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.ws.rs.BadRequestException
import javax.ws.rs.Path
import javax.ws.rs.core.Response


/**
 * Zalanda carrier integration service
 * Created by 27694066 on 02.03.2017.
 */
@Component
@Path("zalando/v1/ldn")
class CarrierIntegrationService : CarrierIntegrationService {

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dsl: DSLContext

    @Inject
    private lateinit var glsShipmentProcessingService: ShipmentProcessingPortType

    /**
     *
     */
    override fun postDeliveryOrder(deliveryOrder: DeliveryOrder): NotifiedDeliveryOrder {

        try {
            val deliveryOptionId: Int = deliveryOrder.deliveryOption.id!!.split(delimiters = *arrayOf("-"), ignoreCase = true, limit = 0)[0].toInt()

            val result = dsl.select()
                    .from(Tables.SDD_CUSTOMER
                            .join(Tables.SDD_CONTACT).on(Tables.SDD_CUSTOMER.CUSTOMERID.equal(Tables.SDD_CONTACT.CUSTOMERID))
                            .join(Tables.SDD_CONTZIP).on(Tables.SDD_CONTACT.ZIPLAYER.equal(Tables.SDD_CONTZIP.LAYER)))
                    .where(Tables.SDD_CUSTOMER.NAME1.equal("Zalando")
                            .and(Tables.SDD_CONTZIP.ID.eq(deliveryOptionId)))
                    .fetch()

            if (result.size == 0) {
                throw RestProblem(
                        status = Response.Status.NOT_FOUND,
                        title = "No delivery options found",
                        detail = "No delivery options found for given id")
            }

            val delOptionZip = result.getValue(0, Tables.SDD_CONTZIP.ZIP)
            val targetAddrZip = deliveryOrder.targetAddress.zipCode

            // Ensure that there is only one record (may be not necessary due to unique/primary key "ID" in table "SDD_ContZip")
            if (result.size != 1) {
                throw RestProblem(
                        status = Response.Status.CONFLICT,
                        title = "Multiple delivery options found",
                        detail = "Multiple delivery options found")
            }

            // Make sure that the given zipcode of target address is same of the given delivery option.
            if (!delOptionZip.equals(targetAddrZip, ignoreCase = true)) {
                throw RestProblem(
                        title = "Delivery option not matching given address",
                        detail = "The given delivery option with zip code [$delOptionZip] does not match the target address zipcode [$targetAddrZip]")
            }

            val knownOrder = dsl.fetchCount(
                    Tables.SDD_FPCS_ORDER,
                    Tables.SDD_FPCS_ORDER.CUSTOMERS_REFERENCE.eq(deliveryOrder.incomingId)) > 0

            val fpcsRecord: SddFpcsOrderRecord = dsl.newRecord(Tables.SDD_FPCS_ORDER)
            fpcsRecord.customersReference = deliveryOrder.incomingId
            fpcsRecord.customerNo = result.getValue(0, Tables.SDD_CUSTOMER.CUSTOMERID)
            fpcsRecord.contactNo = result.getValue(0, Tables.SDD_CONTACT.CONTACTID)
            fpcsRecord.zipcodeRef = deliveryOptionId
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

            if (knownOrder) {
                val existingRecord = dsl.fetch(Tables.SDD_FPCS_ORDER, Tables.SDD_FPCS_ORDER.CUSTOMERS_REFERENCE.eq(fpcsRecord.customersReference).and(Tables.SDD_FPCS_ORDER.GLS_PARCELNO.isNotNull))
                fpcsRecord.glsParcelno = existingRecord[0].glsParcelno
                fpcsRecord.glsTrackid = existingRecord[0].glsTrackid
            }

            fpcsRecord.store()

            if (!knownOrder) {
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
                        throw RestProblem(
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

                    var glsParcelNum: String = parcelData[0].barcodes.primary1D.substring(0, 11)
                    //glsParcelNum = glsParcelNum.substring(1, 3) + "85" + glsParcelNum.substring(4, 11)
                    fpcsRecord.glsTrackid = parcelData[0].trackID
                    fpcsRecord.glsParcelno = glsParcelNum

                    fpcsRecord.store()

                    //Check if GLS Parcel number is within the assigned range. Otherwise cancel the order and throw a problem.
                    val checkRange = dsl.fetchCount(
                            Tables.TBLSYSCOLLECTIONS
                                    .join(Tables.SDD_CUSTOMER)
                                    .on(Tables.TBLSYSCOLLECTIONS.TXTP2
                                            .eq(Tables.SDD_CUSTOMER.CUSTOMERID)),
                            Tables.TBLSYSCOLLECTIONS.TYP.eq(80)
                                    .and(Tables.SDD_CUSTOMER.NAME1.eq("Zalando"))
                                    .and(Tables.TBLSYSCOLLECTIONS.TXTVALUE.lessOrEqual(courierNum))
                                    .and(Tables.TBLSYSCOLLECTIONS.TXTP1.greaterOrEqual(courierNum)))

                    if (checkRange > 0) {
                        //TODO: To be reverted to ${glsParcelNum} after schema update V9 is applied
                        return NotifiedDeliveryOrder(fpcsRecord.id.toString(), "https://gls-group.eu/DE/de/paketverfolgung?match=${fpcsRecord.glsTrackid}")
                    } else {
                        cancelDeliveryOrder(glsParcelNum)
                        throw RestProblem(
                                title = "Customers range exceeded.",
                                detail = "The order could not be processed due to an leaked customers range. Contact GLS Support ASAP!")
                    }

                } catch(d: RestProblem) { //Don't catch a thrown DefaultProblem as a general Exception. These are supposed to be thrown.
                    throw d
                } catch (e: Exception) {
                    fpcsRecord.cancelRequested = -2
                    fpcsRecord.store()
                    throw RestProblem(
                            title = "Error serving GLS systems",
                            detail = "The order could not be stored in GLS Systems due to an error: ${e.message}")
                }
            } else {
                //The provided order is already known. Return the original Tracking-URL and the new identifier.
                //TODO: To be changed back to ${fpcsRecord.glsParcelno} after schema update V9 applied
                return NotifiedDeliveryOrder(fpcsRecord.id.toString(), "https://gls-group.eu/DE/de/paketverfolgung?match=${fpcsRecord.glsTrackid}")
            }
        } catch(e: Exception) {
            if (e is RestProblem) {
                throw e
            } else {
                throw RestProblem(
                        title = "Unhandled exception",
                        detail = e.message)
            }
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
            target_address_address_line: String): List<DeliveryOption> {

        try {
            val sddRoute: SddContzipRecord = dsl.fetchOne(
                    Tables.SDD_CONTZIP,
                    Tables.SDD_CONTZIP.ZIP
                            .eq(target_address_zip_code)
                            .and(Tables.SDD_CONTZIP.LAYER.between(5, 6))
            ) ?: return mutableListOf() //Return empty list if given zip-code does not match any Zalando SDD-Area

            val currentDate = Date()
            return generateDeliveryOptions(DeliveryOption(
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
            throw RestProblem(
                    title = "Too many delivery options found",
                    detail = "The given zip code is not unique")
        } catch (e: Exception) {
            throw RestProblem(
                    title = "Unhandled exception",
                    detail = e.message)
        }
    }

    /**
     *
     */
    override fun cancelDeliveryOrder(id: String): Response {

        try {
            val order: SddFpcsOrderRecord = dsl.fetchOne(
                    Tables.SDD_FPCS_ORDER,
                    Tables.SDD_FPCS_ORDER.ID
                            .eq(id.toInt())
            ) ?: throw RestProblem(
                    status = Response.Status.NOT_FOUND,
                    detail = "No order with id [$id] found")

            if (order.cancelRequested == -1) {
                throw RestProblem(
                        detail = "Cancellation for order with id [$id] already requested")
            }

            order.cancelRequested = -1
            order.store()

            val cancelResponse: CancelParcelResponse = glsShipmentProcessingService.cancelParcelByID(order.glsTrackid)

            if (cancelResponse.result != null) {
                if (cancelResponse.result.equals("CANCELLATION_PENDING", ignoreCase = true) || cancelResponse.result.equals("CANCELLED", ignoreCase = true)) { //TODO Check for other possible results
                    return Response.ok().build()
                } else {
                    throw RestProblem(
                            title = "Cancellation failed",
                            detail = "Cancellation failed with response [${cancelResponse.result}]")
                }
            }

            throw BadRequestException()

        } catch(e: Exception) {
            throw RestProblem(
                    title = "Cancellation failed",
                    detail = "Cancellation for order with id [$id] failed due to an unhandled exception: ${e.message}")
        }
    }

    /**
     * Generates delivery options
     */
    fun generateDeliveryOptions(deliveryOption: DeliveryOption): List<DeliveryOption> {
        // Helper functions for generation

        /**
         * Add days to all date fields
         * @param days Amount of days to add
         * @return New instance
         */
        fun DeliveryOption.addDays(days: Int): DeliveryOption {
            return DeliveryOption(
                    id = this.id,
                    cutOff = this.cutOff?.plusDays(days),
                    pickUp = this.pickUp?.plusDays(days),
                    deliveryFrom = this.deliveryFrom?.plusDays(days),
                    deliveryTo = this.deliveryTo?.plusDays(days)
            )
        }

        /**
         * Convert to COB
         * @return Updataes DeliveryOption instance
         */
        fun DeliveryOption.convertToCOB(): DeliveryOption {
            val dateFormat = SimpleDateFormat("HHmm")

            return DeliveryOption(
                    id = this.id,
                    cutOff = this.cutOff
                            ?.replaceTime(dateFormat.parse("1600"))
                            ?.plusDays(-1),
                    pickUp = this.pickUp
                            ?.replaceTime(dateFormat.parse("1630"))
                            ?.plusDays(-1),
                    deliveryFrom = this.deliveryFrom
                            ?.replaceTime(dateFormat.parse("0800")),
                    deliveryTo = this.deliveryTo
                            ?.replaceTime(dateFormat.parse("1600"))
            )
        }

        /**
         * Helper extension for generating new unique identifier from delivery option
         */
        fun DeliveryOption.generateUniqueIdentifier(sdd: Boolean): String {
            val dateFormat = SimpleDateFormat("ddMMyyyy")
            return "${this.id}-${dateFormat.format(this.deliveryFrom)}#${if(sdd) "SDD" else "COB"}"
        }

        var count: Int = 0
        val delOptions: ArrayList<DeliveryOption> = ArrayList()

        while(true) {

            if (delOptions.size == 9) {
                break
            }

            val c: Calendar = Calendar.getInstance()
            c.time = Date()
            c.add(Calendar.DATE, count)

            if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                count++
            } else {
                val newDeliveryOption = deliveryOption
                        .addDays(count)

                val newDeliveryOptionCDB = deliveryOption
                        .addDays(count)
                        .convertToCOB()

                newDeliveryOption.id = newDeliveryOption.generateUniqueIdentifier(
                        sdd = true)

                newDeliveryOptionCDB.id = newDeliveryOptionCDB.generateUniqueIdentifier(
                        sdd = false)

                delOptions.add(newDeliveryOption)

                if (count > 0) {
                    delOptions.add(newDeliveryOptionCDB)
                }

                count++
            }


        }

        return delOptions
    }
}