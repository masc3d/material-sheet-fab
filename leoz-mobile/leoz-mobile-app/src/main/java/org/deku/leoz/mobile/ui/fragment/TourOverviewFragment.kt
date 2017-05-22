package org.deku.leoz.mobile.ui.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_tour_overview.*
import org.deku.leoz.enums.ParcelService

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.Stop
import org.deku.leoz.mobile.ui.StopListAdapter
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class TourOverviewFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_tour_overview, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.uxStopList.adapter = StopListAdapter(
                context = context,
                data = mutableListOf(
                        Stop(
                                order = mutableListOf(Stop.Order(
                                        classification = Stop.Order.OrderClassification.DELIVERY,
                                        parcel = mutableListOf(
                                                Stop.Order.Parcel(
                                                    labelReference = "1234567890",
                                                    dimensions = Stop.Order.Dimension(
                                                            length = 10.0,
                                                            height = 10.0,
                                                            width = 10.0,
                                                            weight = 15.0
                                                    )
                                                ),
                                                Stop.Order.Parcel(
                                                        labelReference = "1234567891",
                                                        dimensions = Stop.Order.Dimension(
                                                                length = 10.0,
                                                                height = 10.0,
                                                                width = 10.0,
                                                                weight = 15.0
                                                        )
                                                )
                                        ),
                                        addresses = mutableListOf(Stop.Order.Address(
                                                classification = Stop.Order.Address.AddressClassification.DELIVERY,
                                                contactPerson = "Philipp Prangenberg",
                                                addressLine1 = "Burscheidter Weg",
                                                addressLineNo1 = "4",
                                                zipCode = "40822",
                                                city = "Mettmann",
                                                geoLocation = null
                                        )),
                                        appointment = "08.00 - 12.00",
                                        carrier = Stop.Order.Carrier.DERKURIER,
                                        service = mutableListOf(ParcelService.addressCorrection),
                                        additionalInformation = mutableListOf(),
                                        sort = 1
                                )),
                                address = Stop.Order.Address(
                                        classification = Stop.Order.Address.AddressClassification.DELIVERY,
                                        contactPerson = "Philipp Prangenberg",
                                        addressLine1 = "Burscheidter Weg",
                                        addressLineNo1 = "4",
                                        zipCode = "40822",
                                        city = "Mettmann",
                                        geoLocation = null
                                ),
                                appointment = "08.00 - 12.00",
                                sort = 1
                        ),
                        Stop(
                                order = mutableListOf(Stop.Order(
                                        classification = Stop.Order.OrderClassification.DELIVERY,
                                        parcel = mutableListOf(
                                                Stop.Order.Parcel(
                                                        labelReference = "1234567892",
                                                        dimensions = Stop.Order.Dimension(
                                                                length = 10.0,
                                                                height = 10.0,
                                                                width = 10.0,
                                                                weight = 15.0
                                                        )
                                                ),
                                                Stop.Order.Parcel(
                                                        labelReference = "1234567893",
                                                        dimensions = Stop.Order.Dimension(
                                                                length = 10.0,
                                                                height = 10.0,
                                                                width = 10.0,
                                                                weight = 15.0
                                                        )
                                                )
                                        ),
                                        addresses = mutableListOf(Stop.Order.Address(
                                                classification = Stop.Order.Address.AddressClassification.DELIVERY,
                                                contactPerson = "Philipp Prangenberg",
                                                addressLine1 = "Simon-Haune-Str.",
                                                addressLineNo1 = "1",
                                                zipCode = "36251",
                                                city = "Bad Hersfeld",
                                                geoLocation = null
                                        )),
                                        appointment = "09.00 - 17.00",
                                        carrier = Stop.Order.Carrier.DERKURIER,
                                        service = mutableListOf(ParcelService.addressCorrection),
                                        additionalInformation = mutableListOf(),
                                        sort = 1
                                )),
                                address = Stop.Order.Address(
                                        classification = Stop.Order.Address.AddressClassification.DELIVERY,
                                        contactPerson = "Philipp Prangenberg",
                                        addressLine1 = "Simon-Haune-Str.",
                                        addressLineNo1 = "1",
                                        zipCode = "36251",
                                        city = "Bad Hersfeld",
                                        geoLocation = null
                                ),
                                appointment = "09.00 - 17.00",
                                sort = 2
                        ),Stop(
                        order = mutableListOf(
                                Stop.Order(
                                    classification = Stop.Order.OrderClassification.DELIVERY,
                                    parcel = mutableListOf(
                                            Stop.Order.Parcel(
                                                    labelReference = "1234567890",
                                                    dimensions = Stop.Order.Dimension(
                                                            length = 10.0,
                                                            height = 10.0,
                                                            width = 10.0,
                                                            weight = 15.0
                                                    )
                                            ),
                                            Stop.Order.Parcel(
                                                    labelReference = "1234567891",
                                                    dimensions = Stop.Order.Dimension(
                                                            length = 10.0,
                                                            height = 10.0,
                                                            width = 10.0,
                                                            weight = 15.0
                                                    )
                                            )
                                    ),
                                    addresses = mutableListOf(Stop.Order.Address(
                                            classification = Stop.Order.Address.AddressClassification.DELIVERY,
                                            contactPerson = "Philipp Prangenberg",
                                            addressLine1 = "Burscheidter Weg",
                                            addressLineNo1 = "4",
                                            zipCode = "40822",
                                            city = "Mettmann",
                                            geoLocation = null
                                    )),
                                    appointment = "08.00 - 12.00",
                                    carrier = Stop.Order.Carrier.DERKURIER,
                                    service = mutableListOf(ParcelService.addressCorrection),
                                    additionalInformation = mutableListOf(),
                                    sort = 1
                                ),
                                Stop.Order(
                                        classification = Stop.Order.OrderClassification.DELIVERY,
                                        parcel = mutableListOf(
                                                Stop.Order.Parcel(
                                                        labelReference = "1234567890",
                                                        dimensions = Stop.Order.Dimension(
                                                                length = 10.0,
                                                                height = 10.0,
                                                                width = 10.0,
                                                                weight = 15.0
                                                        )
                                                ),
                                                Stop.Order.Parcel(
                                                        labelReference = "1234567891",
                                                        dimensions = Stop.Order.Dimension(
                                                                length = 10.0,
                                                                height = 10.0,
                                                                width = 10.0,
                                                                weight = 15.0
                                                        )
                                                )
                                        ),
                                        addresses = mutableListOf(Stop.Order.Address(
                                                classification = Stop.Order.Address.AddressClassification.DELIVERY,
                                                contactPerson = "Philipp Prangenberg",
                                                addressLine1 = "Burscheidter Weg",
                                                addressLineNo1 = "4",
                                                zipCode = "40822",
                                                city = "Mettmann",
                                                geoLocation = null
                                        )),
                                        appointment = "08.00 - 12.00",
                                        carrier = Stop.Order.Carrier.DERKURIER,
                                        service = mutableListOf(ParcelService.addressCorrection),
                                        additionalInformation = mutableListOf(),
                                        sort = 1
                                )
                        ),
                        address = Stop.Order.Address(
                                classification = Stop.Order.Address.AddressClassification.DELIVERY,
                                contactPerson = "Philipp Prangenberg",
                                addressLine1 = "Burscheidter Weg",
                                addressLineNo1 = "4",
                                zipCode = "40822",
                                city = "Mettmann",
                                geoLocation = null
                        ),
                        appointment = "08.00 - 12.00",
                        sort = 1
                ),
                        Stop(
                                order = mutableListOf(Stop.Order(
                                        classification = Stop.Order.OrderClassification.DELIVERY,
                                        parcel = mutableListOf(
                                                Stop.Order.Parcel(
                                                        labelReference = "1234567892",
                                                        dimensions = Stop.Order.Dimension(
                                                                length = 10.0,
                                                                height = 10.0,
                                                                width = 10.0,
                                                                weight = 15.0
                                                        )
                                                ),
                                                Stop.Order.Parcel(
                                                        labelReference = "1234567893",
                                                        dimensions = Stop.Order.Dimension(
                                                                length = 10.0,
                                                                height = 10.0,
                                                                width = 10.0,
                                                                weight = 15.0
                                                        )
                                                )
                                        ),
                                        addresses = mutableListOf(Stop.Order.Address(
                                                classification = Stop.Order.Address.AddressClassification.DELIVERY,
                                                contactPerson = "Philipp Prangenberg",
                                                addressLine1 = "Simon-Haune-Str.",
                                                addressLineNo1 = "1",
                                                zipCode = "36251",
                                                city = "Bad Hersfeld",
                                                geoLocation = null
                                        )),
                                        appointment = "09.00 - 17.00",
                                        carrier = Stop.Order.Carrier.DERKURIER,
                                        service = mutableListOf(ParcelService.addressCorrection),
                                        additionalInformation = mutableListOf(),
                                        sort = 1
                                )),
                                address = Stop.Order.Address(
                                        classification = Stop.Order.Address.AddressClassification.DELIVERY,
                                        contactPerson = "Philipp Prangenberg",
                                        addressLine1 = "Simon-Haune-Str.",
                                        addressLineNo1 = "1",
                                        zipCode = "36251",
                                        city = "Bad Hersfeld",
                                        geoLocation = null
                                ),
                                appointment = "09.00 - 17.00",
                                sort = 2
                        ),Stop(
                        order = mutableListOf(Stop.Order(
                                classification = Stop.Order.OrderClassification.DELIVERY,
                                parcel = mutableListOf(
                                        Stop.Order.Parcel(
                                                labelReference = "1234567890",
                                                dimensions = Stop.Order.Dimension(
                                                        length = 10.0,
                                                        height = 10.0,
                                                        width = 10.0,
                                                        weight = 15.0
                                                )
                                        ),
                                        Stop.Order.Parcel(
                                                labelReference = "1234567891",
                                                dimensions = Stop.Order.Dimension(
                                                        length = 10.0,
                                                        height = 10.0,
                                                        width = 10.0,
                                                        weight = 15.0
                                                )
                                        )
                                ),
                                addresses = mutableListOf(Stop.Order.Address(
                                        classification = Stop.Order.Address.AddressClassification.DELIVERY,
                                        contactPerson = "Philipp Prangenberg",
                                        addressLine1 = "Burscheidter Weg",
                                        addressLineNo1 = "4",
                                        zipCode = "40822",
                                        city = "Mettmann",
                                        geoLocation = null
                                )),
                                appointment = "08.00 - 12.00",
                                carrier = Stop.Order.Carrier.DERKURIER,
                                service = mutableListOf(ParcelService.addressCorrection),
                                additionalInformation = mutableListOf(),
                                sort = 1
                        )),
                        address = Stop.Order.Address(
                                classification = Stop.Order.Address.AddressClassification.DELIVERY,
                                contactPerson = "Philipp Prangenberg",
                                addressLine1 = "Burscheidter Weg",
                                addressLineNo1 = "4",
                                zipCode = "40822",
                                city = "Mettmann",
                                geoLocation = null
                        ),
                        appointment = "08.00 - 12.00",
                        sort = 1
                ),
                        Stop(
                                order = mutableListOf(Stop.Order(
                                        classification = Stop.Order.OrderClassification.DELIVERY,
                                        parcel = mutableListOf(
                                                Stop.Order.Parcel(
                                                        labelReference = "1234567892",
                                                        dimensions = Stop.Order.Dimension(
                                                                length = 10.0,
                                                                height = 10.0,
                                                                width = 10.0,
                                                                weight = 15.0
                                                        )
                                                ),
                                                Stop.Order.Parcel(
                                                        labelReference = "1234567893",
                                                        dimensions = Stop.Order.Dimension(
                                                                length = 10.0,
                                                                height = 10.0,
                                                                width = 10.0,
                                                                weight = 15.0
                                                        )
                                                )
                                        ),
                                        addresses = mutableListOf(Stop.Order.Address(
                                                classification = Stop.Order.Address.AddressClassification.DELIVERY,
                                                contactPerson = "Philipp Prangenberg",
                                                addressLine1 = "Simon-Haune-Str.",
                                                addressLineNo1 = "1",
                                                zipCode = "36251",
                                                city = "Bad Hersfeld",
                                                geoLocation = null
                                        )),
                                        appointment = "09.00 - 17.00",
                                        carrier = Stop.Order.Carrier.DERKURIER,
                                        service = mutableListOf(ParcelService.addressCorrection),
                                        additionalInformation = mutableListOf(),
                                        sort = 1
                                )),
                                address = Stop.Order.Address(
                                        classification = Stop.Order.Address.AddressClassification.DELIVERY,
                                        contactPerson = "Philipp Prangenberg",
                                        addressLine1 = "Simon-Haune-Str.",
                                        addressLineNo1 = "1",
                                        zipCode = "36251",
                                        city = "Bad Hersfeld",
                                        geoLocation = null
                                ),
                                appointment = "09.00 - 17.00",
                                sort = 2
                        ),Stop(
                        order = mutableListOf(Stop.Order(
                                classification = Stop.Order.OrderClassification.DELIVERY,
                                parcel = mutableListOf(
                                        Stop.Order.Parcel(
                                                labelReference = "1234567890",
                                                dimensions = Stop.Order.Dimension(
                                                        length = 10.0,
                                                        height = 10.0,
                                                        width = 10.0,
                                                        weight = 15.0
                                                )
                                        ),
                                        Stop.Order.Parcel(
                                                labelReference = "1234567891",
                                                dimensions = Stop.Order.Dimension(
                                                        length = 10.0,
                                                        height = 10.0,
                                                        width = 10.0,
                                                        weight = 15.0
                                                )
                                        )
                                ),
                                addresses = mutableListOf(Stop.Order.Address(
                                        classification = Stop.Order.Address.AddressClassification.DELIVERY,
                                        contactPerson = "Philipp Prangenberg",
                                        addressLine1 = "Burscheidter Weg",
                                        addressLineNo1 = "4",
                                        zipCode = "40822",
                                        city = "Mettmann",
                                        geoLocation = null
                                )),
                                appointment = "08.00 - 12.00",
                                carrier = Stop.Order.Carrier.DERKURIER,
                                service = mutableListOf(ParcelService.addressCorrection),
                                additionalInformation = mutableListOf(),
                                sort = 1
                        )),
                        address = Stop.Order.Address(
                                classification = Stop.Order.Address.AddressClassification.DELIVERY,
                                contactPerson = "Philipp Prangenberg",
                                addressLine1 = "Burscheidter Weg",
                                addressLineNo1 = "4",
                                zipCode = "40822",
                                city = "Mettmann",
                                geoLocation = null
                        ),
                        appointment = "08.00 - 12.00",
                        sort = 1
                ),
                        Stop(
                                order = mutableListOf(Stop.Order(
                                        classification = Stop.Order.OrderClassification.DELIVERY,
                                        parcel = mutableListOf(
                                                Stop.Order.Parcel(
                                                        labelReference = "1234567892",
                                                        dimensions = Stop.Order.Dimension(
                                                                length = 10.0,
                                                                height = 10.0,
                                                                width = 10.0,
                                                                weight = 15.0
                                                        )
                                                ),
                                                Stop.Order.Parcel(
                                                        labelReference = "1234567893",
                                                        dimensions = Stop.Order.Dimension(
                                                                length = 10.0,
                                                                height = 10.0,
                                                                width = 10.0,
                                                                weight = 15.0
                                                        )
                                                )
                                        ),
                                        addresses = mutableListOf(Stop.Order.Address(
                                                classification = Stop.Order.Address.AddressClassification.DELIVERY,
                                                contactPerson = "Philipp Prangenberg",
                                                addressLine1 = "Simon-Haune-Str.",
                                                addressLineNo1 = "1",
                                                zipCode = "36251",
                                                city = "Bad Hersfeld",
                                                geoLocation = null
                                        )),
                                        appointment = "09.00 - 17.00",
                                        carrier = Stop.Order.Carrier.DERKURIER,
                                        service = mutableListOf(ParcelService.addressCorrection),
                                        additionalInformation = mutableListOf(),
                                        sort = 1
                                )),
                                address = Stop.Order.Address(
                                        classification = Stop.Order.Address.AddressClassification.DELIVERY,
                                        contactPerson = "Philipp Prangenberg",
                                        addressLine1 = "Simon-Haune-Str.",
                                        addressLineNo1 = "1",
                                        zipCode = "36251",
                                        city = "Bad Hersfeld",
                                        geoLocation = null
                                ),
                                appointment = "09.00 - 17.00",
                                sort = 2
                        )
                )
        )
    }

}
