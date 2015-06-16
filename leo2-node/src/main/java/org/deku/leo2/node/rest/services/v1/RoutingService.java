package org.deku.leo2.node.rest.services.v1;

import com.google.common.primitives.Ints;
import com.mysema.query.types.expr.BooleanExpression;
import org.deku.leo2.node.data.entities.*;
import org.deku.leo2.node.data.repositories.CountryRepository;
import org.deku.leo2.node.data.repositories.HolidayctrlRepository;
import org.deku.leo2.node.data.repositories.RouteRepository;
import org.deku.leo2.rest.entities.ShortDate;
import org.deku.leo2.rest.entities.ShortTime;
import org.deku.leo2.rest.entities.v1.DayType;
import org.deku.leo2.rest.entities.v1.Routing;
import org.deku.leo2.rest.entities.v1.RoutingVia;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.function.Function;

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
    public Routing find(String senddate, String country, String zip, String product) {

        Routing rWSRouting = new Routing();

//        Iterable<Country> countries = mCountryRepository.findWithQuery(country);
//        Country rcountry=mCountryRepository.find(country);


        if (senddate.equals(null)) {
            throw new IllegalArgumentException("empty Senddate");
        }

        ShortDate senddateShort=  new ShortDate ( senddate) ;

        java.time.LocalDate sd = LocalDate.from(senddateShort.getLocalDate());

        java.sql.Timestamp sqlvalidForm = Timestamp.valueOf(senddateShort.toString() + " 00:00:00");

        if (country.equals(null)) {
            throw new IllegalArgumentException("empty Country");
        } else
            country = country.toUpperCase();

        if (zip.equals(null)) {
            throw new IllegalArgumentException("empty Zipcode");
        } else
            zip = zip.toUpperCase();

        if (product.equals(null)) {
            //Set default Product
            product = "A";
        } else
            product = product.toUpperCase();


        Country rcountry = mCountryRepository.findOne(country);

        // -------------------------
        // ≤  country.equals(null) ≤
        // -------------------------
        //       ** forever **

        if (rcountry == null) {
            throw new IllegalArgumentException("unknown Country");
        }

        if (rcountry.getZipFormat().equals("")) {
            throw new IllegalArgumentException("unknown Country");
        }


        if (zip.length() < rcountry.getMinLen()) {
            throw new IllegalArgumentException("Zipcode to short");
        }

        if (rcountry.getRoutingTyp() < 0 || rcountry.getRoutingTyp() > 3) {
            throw new IllegalArgumentException("Country not enabled");
        }

        if (zip.length() > rcountry.getMaxLen()) {
            throw new IllegalArgumentException("Zipcode to long");
        }

        s2str Zret = parceZip(rcountry.getZipFormat(), zip);

        String zipQuery = Zret.s1;
        String zipConform = Zret.s2;

        if (zipQuery.equals("")) {
            throw new IllegalArgumentException("Zipcode not conform");
        }


        QRoute qRoute = QRoute.route;
        BooleanExpression rWhere = null;

        // todo validto

        switch (rcountry.getRoutingTyp()) {
            case 0:
                rWhere =
                        qRoute.lkz.eq(country)
                                .and(qRoute.zip.eq(zipQuery))
                                .and(qRoute.validFrom.loe(sqlvalidForm));

                break;
            case 1:
                rWhere =
                        qRoute.lkz.eq(country)
                                .and(qRoute.zip.goe(zipQuery))
                                .and(qRoute.validFrom.loe(sqlvalidForm));
                break;
            case 2:
                rWhere =
                        qRoute.lkz.eq(country)
                                .and(qRoute.zip.goe(zipQuery))
                                .and(qRoute.validFrom.loe(sqlvalidForm));
                break;
        }

        // ??? feldauflösung via qRoute
//        Sort sort = null;
//        sort = new Sort(Sort.Direction.DESC, "validfrom");
//test
//        Iterable<Route> rRouten = new ArrayList<>();
//        Iterable<Route> rRouten = mRouteRepository.findAll(rWhere ,new Sort(Sort.Direction.DESC,qRoute.validfrom.toString()) );
//        Iterable<Route> rRouten = mRouteRepository.findAll(rWhere, new Sort(Sort.Direction.DESC, "validfrom"));
        Iterable<Route> rRouten = mRouteRepository.findAll(rWhere, qRoute.validFrom.desc());


        if (!rRouten.iterator().hasNext()) {
            throw new IllegalArgumentException("no Route found to given data");
        }
        Route routeFound = rRouten.iterator().next();

        Function<Time, ShortTime> convertTime = (t) -> {
            if (t != null)
                return new ShortTime(t.toString());
            return null;
        };

        convertTime.apply(null);

        rWSRouting.setSector(routeFound.getSector());
        rWSRouting.setDayType(getDayType(LocalDate.parse(senddateShort.toString()), country, routeFound.getHolidayCtrl()));
        rWSRouting.setRouting(routeFound.getStation());
        rWSRouting.setZone(routeFound.getArea());
        rWSRouting.setIsland(routeFound.getIsland() != 0);
        rWSRouting.setEarliestTimeOfDelivery(sqlTimeToShortTime(routeFound.getEtod()));
        rWSRouting.setEarliestTimeOfDelivery(new ShortTime(routeFound.getEtod().toString()));
        if (zipConform != null)
            rWSRouting.setZipCode(zipConform);

        rWSRouting.setterm(routeFound.getTransitTime());
        if (routeFound.getLtodsa() != null)
            rWSRouting.setSundayDeliveryUntil(new ShortTime(routeFound.getLtodsa().toString()));
        rWSRouting.setDelieveryDay(getNextDeliveryDay(LocalDate.parse(senddateShort.toString()), country, routeFound.getHolidayCtrl()));

        return rWSRouting;
    }

    public static ShortTime sqlTimeToShortTime(java.sql.Time time) {
        if (time == null)
            return null;
        return new ShortTime(time.toString());
    }

    private class s2str {
        String s1;
        String s2;

        public s2str(String s1, String s2) {
            this.s1 = s1;
            this.s2 = s2;
        }
    }


    private s2str parceZip(String zipFormat, String zip) {

        String zipQuery = "";
        String zipConform = "";
        String[] cZipFormat = zipFormat.split("");
        String[] cZip = zip.split("");

        int i = 0;
        int j = 0;
        String csZipFormat = "";
        String csZip = "";
        String csZipConform = "";

        int cCount = 0;

        boolean validZip = true;
        //zipCalcEnd:

        while (j < zip.length() && validZip) {
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
                    } else if (Ints.tryParse(csZip) == null) {
                        validZip = false;
                        break;
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
                    } else if (Ints.tryParse(csZip)== null) {
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

            if (!validZip)
                zipQuery="";

        }

        return new s2str(zipQuery, zipConform);
    }

    private DayType getDayType(LocalDate javaDate, String Country, String Holidayctrl) {
        DayType daytype = DayType.WorkDay;

        DayOfWeek javaday = javaDate.getDayOfWeek();

        if (javaday == DayOfWeek.SUNDAY)
            daytype = DayType.Sunday;
        if (javaday == DayOfWeek.SATURDAY)
            daytype = DayType.Saturday;

        HolidayCtrl rholidayctrl = mHolidayctrlRepostitory.findOne(new HolidayCtrlPK(java.sql.Timestamp.valueOf(javaDate.toString() + " 00:00:00"), Country));

        if (rholidayctrl != null) {
            if (rholidayctrl.getCtrlPos() == -1)
                daytype = DayType.Holiday;
            else if (rholidayctrl.getCtrlPos() > 0) {
                if (Holidayctrl.substring(rholidayctrl.getCtrlPos(), rholidayctrl.getCtrlPos()) == "J")
                    daytype = DayType.RegionalHoliday;
            }
        }

        return daytype;
    }

    private ShortDate getNextDeliveryDay(LocalDate javaDate, String Country, String Holidayctrl) {

        do
            javaDate = javaDate.plusDays(1);
        while (!getDayType(javaDate, Country, Holidayctrl).equals(DayType.WorkDay));

        return new ShortDate(javaDate.toString());
    }

    @Override
    public RoutingVia findVia(ShortDate date, String sourceSector, String destinationSector) {
        return new RoutingVia(new String[]{"S", "X"});
    }
}
