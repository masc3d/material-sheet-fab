package org.deku.leo2.central.rest.services.v1;

import com.google.common.primitives.Ints;
import com.mysema.query.types.expr.BooleanExpression;
import org.deku.leo2.central.data.entities.*;
import org.deku.leo2.central.data.repositories.CountryRepository;
import org.deku.leo2.central.data.repositories.HolidayctrlRepository;
import org.deku.leo2.central.data.repositories.RouteRepository;
import org.deku.leo2.rest.adapters.LocalDateParam;
import org.deku.leo2.rest.entities.v1.HolidayType;
import org.deku.leo2.rest.entities.v1.Routing;
import org.deku.leo2.rest.entities.v1.RoutingVia;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.Timestamp;

/**
 * Created by masc on 20.04.15.
 */
@Component
@Path("v1/routing")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class RoutingService implements org.deku.leo2.rest.services.v1.RoutingService {
    @Inject
    CountryRepository mCountryRepository;

    @Inject
    RouteRepository mRouteRepository;

    @Inject
    HolidayctrlRepository mHolidayctrlRepostitory;

    @Override
    public Routing find(LocalDateParam validForm, String country, String zip, String product) {

        Routing rWSRouting = new Routing();

//        Iterable<Country> countries = mCountryRepository.findWithQuery(country);

//        Country rcountry=mCountryRepository.find(country);
        Country rcountry = mCountryRepository.findOne(country);

        if (rcountry.equals(null)) {
            // kein Land
            return rWSRouting;
        }

        if (rcountry.getZipFormat() == "") {
            // ungültiges Land
            return rWSRouting;
        }


        if (zip.length() < rcountry.getMinLen()) {
            //  ungültiger ZIP
            return rWSRouting;
        }

        if (rcountry.getRoutingTyp() < 0 || rcountry.getRoutingTyp() > 3) {
            //  ungültiger Routingtyp
            return rWSRouting;
        }

        if (zip.length() > rcountry.getMaxLen()) {
            //  ungültiger ZIP
            throw new IllegalArgumentException("Nicht konform");
            //return r;
        }

        String[] cZipFormat = rcountry.getZipFormat().split("");
//        char[] cZipFormat = rcountry.getZipFormat().toCharArray();
        String[] cZip = zip.split("");

        int i = 0;
        int j = 0;
        String csZipFormat = "";
        String csZip = "";
        String csZipConform = "";
        //String csZipNew = "";
        String zipConform = "";
        String zipQuery = "";
        int cCount = 0;


        zipCalcEnd:

        while (j < rcountry.getZipFormat().length()) {
            csZip = cZip[i];
            csZipFormat = cZipFormat[j];


            switch (csZipFormat) {
                case "w":
                    if (csZip == " ")
                        i++;
                    else if (csZip == "0") {
                        i++;
                        j++;
                    } else
                        j++;
                    break;
                case "0":
                    if (csZip == "") {
                        i++;
                        j++;
                    } else if (csZip == " ") {
                        i = i + 1;
//                    } else if (Ints.tryParse(csZip) == 0 && !(cZip.equals("0"))) {
//                        csZipConform = "";
//                        break zipCalcEnd;
                    } else {
                        i++;
                        j++;
                        csZipConform = csZip;
                    }
                    break;
                case "A":
                    if (csZip == "") {
                        csZipConform = "";
                        zipQuery = "";
                        break;
                    } else if (csZip == " ") {
                        zipConform = "";
                        zipQuery = "";
                        break;
                    } else if (Ints.tryParse(csZip) > 0 || csZip == "0") {
                        zipConform = "";
                        zipQuery = "";
                        break;
                    } else {
                        i++;
                        j++;
                        csZipConform = csZip;
                    }

                case "L":
                    if (csZip == "")
                        break;
                    else if (csZip.contains("abcdefghijklmnopqrstuvwxyz0123456789 ")) {
                        i++;
                        j++;
                        csZipConform = csZip;
                    } else {
                        zipConform = "";
                        break;
                    }
                case "G":
                    if (csZip == " ")
                        cCount++;
                    if (cCount > 1) {

                        zipConform = "";
                        break;
                    }
                    i++;
                    j++;
                    csZipConform = csZip;
                case "-":
                    if (csZip == "-") {
                        i++;
                        j++;
                        csZipConform = csZip;
                    } else if (Ints.tryParse(csZip) == null && csZip != "0") {
                        csZipConform = "";
                        break;
                    } else {
                        i++;
                        j = j + 2;
                        csZipConform = "-" + csZip;
                    }
                default:
                    throw new IllegalArgumentException("Format Nicht konform");
            }


            zipConform = zipConform + csZipConform;
            if (cCount == 0)
                zipQuery = zipQuery + csZipConform;


        }

        if (zipQuery == "") {
            //  ungültiger ZIP
            throw new IllegalArgumentException("Nicht konform");
            //return r;
        }


        java.sql.Timestamp sqlTime = Timestamp.valueOf(validForm.toString() + " 00:00:00");


//        RoutePK epk = new RoutePK();
        //epk.setZip(zipConform);
        //epk.setLkz(country);
        //epk.setProduct(product);
        //Timestamp.valueOf(validForm);
        //"2013-11-01 00:00:00");
//        epk.setValidfrom(sqlTime);
//        epk.setValidfrom( );
//        Iterable<Route> lRoute = mRouteRepository.findAll(epk );
//        Route rRoute = lRoute.iterator().next();
//        Route rRoute = mRouteRepository.findOne(epk);
//        Route rRoute= mRouteRepository.findActualRoute(epk) ;

        QRoute qRoute = QRoute.route;
        BooleanExpression rWhere = null;

        switch (rcountry.getRoutingTyp()) {
            case 0:
                rWhere =
                        qRoute.lkz.eq(country)
                                .and(qRoute.zip.eq(zipQuery))
                                .and(qRoute.validfrom.goe(sqlTime)) ;
                //... sortierung, validto
                break;
            case 1:
                rWhere =
                        qRoute.lkz.eq(country)
                                .and(qRoute.zip.eq(zipQuery))
                                .and(qRoute.validfrom.goe(sqlTime));
                break;
            case 2:
                rWhere =
                        qRoute.lkz.eq(country)
                                .and(qRoute.zip.eq(zipQuery))
                                .and(qRoute.validfrom.goe(sqlTime));
                break;
        }
        Iterable<Route> rRouten = mRouteRepository.findAll(rWhere);


//        rRouten = mRouteRepository.findAll(
//                qRoute.lkz.eq(country)
//                        .and(qRoute.zip.eq(zipConform))
//                        .and(qRoute.validfrom.goe(sqlTime))
//        );
        if (!rRouten.iterator().hasNext()) {
            //  keine Route
            throw new IllegalArgumentException("keine Route");
            //return r;
        }
        Route routeFound = rRouten.iterator().next();

//        HolidayctrlPK hd = new HolidayctrlPK();
//        hd.setCountry(country);
//        hd.setHoliday(sqlTime);

        Holidayctrl rholidayctrl = mHolidayctrlRepostitory.findOne(new HolidayctrlPK(sqlTime,country));

//** Feitertage

        HolidayType holidayType = null;

        if (rholidayctrl.getCtrlPos() == -1)
            holidayType = HolidayType.BankHoliday;
//        else if (rholidayctrl.getCtrlPos() > 0 && ) {
//
//        }


        rWSRouting.setSector(rholidayctrl.getCtrlPos().toString());
        rWSRouting.setHoliday(holidayType);
        rWSRouting.setRouting(routeFound.getStation());
        rWSRouting.setZone(routeFound.getArea());
        rWSRouting.setIsland(routeFound.getIsland() != 0);

//        Routing r = new Routing("sector1", "zone1", LocalTime.now(), 12, HolidayType.RegionalBankHoliday, false);

        return rWSRouting;
    }

    @Override
    public RoutingVia findVia(LocalDateParam date, String sourceSector, String destinationSector) {
        return new RoutingVia(new String[]{"S", "X"});
    }
}
