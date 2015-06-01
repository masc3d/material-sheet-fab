package org.deku.leo2.node.rest.services.v1;

import com.google.common.primitives.Ints;
import com.mysema.query.types.expr.BooleanExpression;
import org.deku.leo2.node.data.entities.*;
import org.deku.leo2.node.data.repositories.CountryRepository;
import org.deku.leo2.node.data.repositories.HolidayctrlRepository;
import org.deku.leo2.node.data.repositories.RouteRepository;
import org.deku.leo2.rest.entities.ShortDate;
import org.deku.leo2.rest.entities.ShortTime;
import org.deku.leo2.rest.entities.v1.HolidayType;
import org.deku.leo2.rest.entities.v1.Routing;
import org.deku.leo2.rest.entities.v1.RoutingVia;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.tools.JavaCompiler;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Created by masc on 20.04.15.
 */
@Component
@Path("v1/routing")
@Produces(MediaType.APPLICATION_JSON)
public class RoutingService implements org.deku.leo2.rest.services.v1.RoutingService {
    @Inject
    CountryRepository mCountryRepository;

    @Inject
    RouteRepository mRouteRepository;

    @Inject
    HolidayctrlRepository mHolidayctrlRepostitory;

    @Override
    public Routing find(ShortDate validForm, String country, String zip, String product) {

        Routing rWSRouting = new Routing();

//        Iterable<Country> countries = mCountryRepository.findWithQuery(country);
//        Country rcountry=mCountryRepository.find(country);

        Country rcountry = mCountryRepository.findOne(country);

        if (rcountry.equals(null)) {
            // kein Land
            throw new IllegalArgumentException("empty Country");
        }

        if (rcountry.getZipFormat().equals("")) {
            // ungültiges Land
            throw new IllegalArgumentException("unknown Country");
        }


        if (zip.length() < rcountry.getMinLen()) {
            //  ungültiger ZIP
            throw new IllegalArgumentException("Zipcode to short");
        }

        if (rcountry.getRoutingTyp() < 0 || rcountry.getRoutingTyp() > 3) {
            //  ungültiger Routingtyp
            throw new IllegalArgumentException("Country not enabled");
        }

        if (zip.length() > rcountry.getMaxLen()) {
            //  ungültiger ZIP
            throw new IllegalArgumentException("Zipcode to long");
            //return r;
        }

        String[] cZipFormat = rcountry.getZipFormat().split("");
        String[] cZip = zip.split("");

        int i = 0;
        int j = 0;
        String csZipFormat = "";
        String csZip = "";
        String csZipConform = "";
        String zipConform = "";
        String zipQuery = "";
        int cCount = 0;


        zipCalcEnd:

        while (j < rcountry.getZipFormat().length()) {
            csZip = cZip[i];
            csZipFormat = cZipFormat[j];

            switch (csZipFormat) {
                case "w":
                    if (csZip.equals(" "))
                        i++;
                    else if (csZip.equals("0")) {
                        i++;
                        j++;
                    } else
                        j++;
                    break;
                case "0":
                    if (csZip.equals("")) {
                        i++;
                        j++;
                    } else if (csZip.equals(" ")) {
                        i = i + 1;
                    } else if (Ints.tryParse(csZip).equals(null)) {
                        csZipConform = "";
                        //&& !(cZip.equals("0")))

                        break zipCalcEnd;
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
                    } else if (csZip == " ") {
                        zipConform = "";
                        zipQuery = "";
                    } else if (Ints.tryParse(csZip) > 0 || csZip.equals("0")) {
                        zipConform = "";
                        zipQuery = "";
                    } else {
                        i++;
                        j++;
                        csZipConform = csZip;
                    }
                    break;
                case "L":
                    if (csZip.equals(""))
                        ;
                    else if (csZip.contains("abcdefghijklmnopqrstuvwxyz0123456789 ")) {
                        i++;
                        j++;
                        csZipConform = csZip;
                    } else {
                        zipConform = "";
                    }
                    break;
                case "G":
                    if (csZip.equals(" "))
                        cCount++;
                    if (cCount > 1) {
                        zipConform = "";
                        break;
                    }
                    i++;
                    j++;
                    csZipConform = csZip;
                    break;
                case "-":
                    if (csZip.equals("-")) {
                        i++;
                        j++;
                        csZipConform = csZip;
                    } else if (Ints.tryParse(csZip).equals(null)) {
                        //&& csZip != "0") {
                        csZipConform = "";
                    } else {
                        i++;
                        j = j + 2;
                        csZipConform = "-" + csZip;
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Zipcode not conform");

            }


            zipConform = zipConform + csZipConform;
            if (cCount == 0)
                zipQuery = zipQuery + csZipConform;


        }

        if (zipQuery.equals("")) {
            //  ungültiger ZIP
            throw new IllegalArgumentException("Zipcode not conform");
            //return r;
        }

        java.sql.Timestamp sqlvalidForm = Timestamp.valueOf(validForm.toString() + " 00:00:00");


        QRoute qRoute = QRoute.route;
        BooleanExpression rWhere = null;

        // to do validto

        switch (rcountry.getRoutingTyp()) {
            case 0:
                rWhere =
                        qRoute.lkz.eq(country)
                                .and(qRoute.zip.eq(zipQuery))
                                .and(qRoute.validfrom.loe(sqlvalidForm));

                break;
            case 1:
                rWhere =
                        qRoute.lkz.eq(country)
                                .and(qRoute.zip.goe(zipQuery))
                                .and(qRoute.validfrom.loe(sqlvalidForm));
                break;
            case 2:
                rWhere =
                        qRoute.lkz.eq(country)
                                .and(qRoute.zip.goe(zipQuery))
                                .and(qRoute.validfrom.loe(sqlvalidForm));
                break;
        }

        // ??? feldauflösung via qRoute
        Sort sort = null;
        sort = new Sort(Sort.Direction.DESC, "validfrom");
        Iterable<Route> rRouten = new ArrayList<>();
//        Iterable<Route> rRouten = mRouteRepository.findAll(rWhere, sort);
//        Iterable<Route> rRouten = mRouteRepository.findAll(rWhere ,new Sort(Sort.Direction.DESC,qRoute.validfrom.toString()) );

        if (!rRouten.iterator().hasNext()) {
            //  keine Route
            throw new IllegalArgumentException("no Route to Depot");
        }
        Route routeFound = rRouten.iterator().next();

//** Feitertage
        Holidayctrl rholidayctrl = mHolidayctrlRepostitory.findOne(new HolidayctrlPK(sqlvalidForm, country));

        HolidayType holidayType = HolidayType.Regular;

        LocalDate dtsqlvalidForm;
        dtsqlvalidForm = LocalDate.parse(validForm.toString());
        DayOfWeek day = dtsqlvalidForm.getDayOfWeek();

        if (day== DayOfWeek.SUNDAY)
            holidayType = HolidayType.Sunday;
        if (day== DayOfWeek.SATURDAY)
            holidayType = HolidayType.Saturday;

        if (rholidayctrl != null) {
            if (rholidayctrl.getCtrlPos() == -1)
                holidayType = HolidayType.BankHoliday;
            else if (rholidayctrl.getCtrlPos() > 0) {
                if (routeFound.getHolidayctrl().substring(rholidayctrl.getCtrlPos(), rholidayctrl.getCtrlPos()) == "J")
                    holidayType = HolidayType.RegionalBankHoliday;
            }
        }


        rWSRouting.setSector(routeFound.getSector());
        rWSRouting.setHoliday(holidayType);
        rWSRouting.setRouting(routeFound.getStation());
        rWSRouting.setZone(routeFound.getArea());
        rWSRouting.setIsland(routeFound.getIsland() != 0);
        rWSRouting.setEarliestTimeOfDelivery(new ShortTime(routeFound.getEtod().toString()));
        //rWSRouting.setConformZipCode(zipConform);


        //Routing r = mRoutingService.find(new LocalDateParam(java.time.LocalDate.parse("2013-11-02")), "AT", "1010", "A");


//        rWSRouting.setNextDelieveryDay(new LocalDateParam (java.time.LocalDate.parse("2013-11-02")));

        rWSRouting.setNextDelieveryDay(new ShortDate("2013-11-02"));
//        rWSRouting.setNextDelieveryDay(java.time.LocalDate.of(2015,5,11 ));
        //        rWSRouting.setNextDelieveryDay(java.time.LocalDate LocalDateof(2015, 5, 11));


        //new LocalDate(java.time.LocalDate.parse("2015-05-22") ) );

//        Routing r = new Routing("sector1", "zone1", LocalTime.now(), 12, HolidayType.RegionalBankHoliday, false);

        return rWSRouting;
    }

    @Override
    public RoutingVia findVia(ShortDate date, String sourceSector, String destinationSector) {
        return new RoutingVia(new String[]{"S", "X"});
    }
}
