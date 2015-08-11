package org.deku.leo2.node.rest.services.v1;

import com.google.common.primitives.Ints;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;
import org.deku.leo2.node.data.entities.master.*;
import org.deku.leo2.node.data.repositories.master.*;
import org.deku.leo2.rest.entities.ShortDate;
import org.deku.leo2.rest.entities.ShortTime;
import org.deku.leo2.rest.entities.v1.DayType;
import org.deku.leo2.rest.entities.v1.Routing;
import org.deku.leo2.rest.entities.v1.RoutingRequest;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    @Inject
    RoutingLayerRepository mRoutingLayerRepository;

    @Inject
    StationRepository mStationRepository;

//    @Override
//    public Routing find(String senddate, String country, String zip, String product) {

//        Routing rWSRouting = new Routing();
//
////        Iterable<Country> countries = mCountryRepository.findWithQuery(country);
////        Country rcountry=mCountryRepository.find(country);
//
//
//        if (senddate.equals(null)) {
//            throw new IllegalArgumentException("empty Senddate");
//        }
//
//        ShortDate senddateShort = new ShortDate(senddate);
//
//        java.time.LocalDate sd = LocalDate.from(senddateShort.getLocalDate());
//
//        java.sql.Timestamp sqlvalidForm = Timestamp.valueOf(senddateShort.toString() + " 00:00:00");
//
//        if (country.equals(null)) {
//            throw new IllegalArgumentException("empty Country");
//        } else
//            country = country.toUpperCase();
//
//        if (zip.equals(null)) {
//            throw new IllegalArgumentException("empty Zipcode");
//        } else
//            zip = zip.toUpperCase();
//
//        if (product.equals(null)) {
//            //Set default Product
//            product = "A";
//        } else
//            product = product.toUpperCase();
//
//
//        Country rcountry = mCountryRepository.findOne(country);
//
//        // -------------------------
//        // ≤  country.equals(null) ≤
//        // -------------------------
//        //       ** forever **
//
//        if (rcountry == null) {
//            throw new IllegalArgumentException("unknown Country");
//        }
//
//        if (rcountry.getZipFormat().equals("")) {
//            throw new IllegalArgumentException("unknown Country");
//        }
//
//
//        if (zip.length() < rcountry.getMinLen()) {
//            throw new IllegalArgumentException("Zipcode to short");
//        }
//
//        if (rcountry.getRoutingTyp() < 0 || rcountry.getRoutingTyp() > 3) {
//            throw new IllegalArgumentException("Country not enabled");
//        }
//
//        if (zip.length() > rcountry.getMaxLen()) {
//            throw new IllegalArgumentException("Zipcode to long");
//        }
//
//        s2str Zret = parceZip(rcountry.getZipFormat(), zip);
//
//        String zipQuery = Zret.s1;
//        String zipConform = Zret.s2;
//
//        if (zipQuery.equals("")) {
//            throw new IllegalArgumentException("Zipcode not conform");
//        }
//
//
//        QRoute qRoute = QRoute.route;
//        BooleanExpression rWhere = null;
//
//        // todo validto
//
//        switch (rcountry.getRoutingTyp()) {
//            case 0:
//                rWhere =
//                        qRoute.country.eq(country)
//                                .and(qRoute.country.eq(zipQuery))
//                                .and(qRoute.validFrom.loe(sqlvalidForm));
//
//                break;
//            case 1:
//                rWhere =
//                        qRoute.country.eq(country)
//                                .and(qRoute.country.goe(zipQuery))
//                                .and(qRoute.validFrom.loe(sqlvalidForm));
//                break;
//            case 2:
//                rWhere =
//                        qRoute.country.eq(country)
//                                .and(qRoute.country.goe(zipQuery))
//                                .and(qRoute.validFrom.loe(sqlvalidForm));
//                break;
//        }
//
//        // ??? feldauflösung via qRoute
////        Sort sort = null;
////        sort = new Sort(Sort.Direction.DESC, "validfrom");
////test
////        Iterable<Route> rRouten = new ArrayList<>();
////        Iterable<Route> rRouten = mRouteRepository.findAll(rWhere ,new Sort(Sort.Direction.DESC,qRoute.validfrom.toString()) );
////        Iterable<Route> rRouten = mRouteRepository.findAll(rWhere, new Sort(Sort.Direction.DESC, "validfrom"));
//        Iterable<Route> rRouten = mRouteRepository.findAll(rWhere, qRoute.validFrom.desc());
//
//
//        if (!rRouten.iterator().hasNext()) {
//            throw new IllegalArgumentException("no Route found to given data");
//        }
//        Route routeFound = rRouten.iterator().next();
//
//        Function<Time, ShortTime> convertTime = (t) -> {
//            if (t != null)
//                return new ShortTime(t.toString());
//            return null;
//        };
//
//        convertTime.apply(null);
//
//        rWSRouting.setDayType(getDayType(LocalDate.parse(senddateShort.toString()), country, routeFound.getHolidayCtrl()));
//        rWSRouting.setRouting(routeFound.getStation());
//        rWSRouting.setZone(routeFound.getArea());
//        rWSRouting.setIsland(routeFound.getIsland() != 0);
//        rWSRouting.setEarliestTimeOfDelivery(sqlTimeToShortTime(routeFound.getEtod()));
//        rWSRouting.setEarliestTimeOfDelivery(new ShortTime(routeFound.getEtod().toString()));
//        if (zipConform != null)
//            rWSRouting.setZipCode(zipConform);
//
//        rWSRouting.setterm(routeFound.getTerm());
//        if (routeFound.getLtodsa() != null)
//            rWSRouting.setSundayDeliveryUntil(new ShortTime(routeFound.getLtodsa().toString()));
//        rWSRouting.setDelieveryDay(getNextDeliveryDay(LocalDate.parse(senddateShort.toString()), country, routeFound.getHolidayCtrl()));
//
//        return rWSRouting;
//    }

    @Override
    public Routing request(RoutingRequest routingRequest) {

        Routing rWSRouting = new Routing();

        Integer services;
        if (routingRequest.getServices() == null)
            services = 0;
        else
            services = routingRequest.getServices();


// TODO REAL oder Volumen ???
        double weight;
        if (routingRequest.getWeight() == null)
            weight = 0;
        else
            weight = routingRequest.getWeight();

        if (routingRequest.getSendDate() == null) {
            throw new IllegalArgumentException("Send Date is required");
        }

        if (routingRequest.getSender() == null && routingRequest.getConsignee() == null) {
            throw new IllegalArgumentException("Sender or Consignee required");
        }


// Unit CTRLs

        Integer ctrlTransportUnit;

        ctrlTransportUnit = 1;

        if (weight > 100)
            ctrlTransportUnit = ctrlTransportUnit + 2;

        if ((services & 256) == 256)
            ctrlTransportUnit = ctrlTransportUnit + 4;

        if ((services & 512) == 512)
            ctrlTransportUnit = ctrlTransportUnit + 8;


// check usable Layers
//TODO
        ctrlTransportUnit = 23;

        QRoutingLayer qLayer = QRoutingLayer.routingLayer;

        BooleanExpression rWhereLayer = qLayer.services.eq(ctrlTransportUnit);

        Iterable<RoutingLayer> layer = mRoutingLayerRepository.findAll(rWhereLayer);
//TODO rWereLayer bitmaske suchen

        LocalDate sendDate = LocalDate.parse(routingRequest.getSendDate().toString());
        LocalDate routingValidDate = sendDate;
        LocalDate desireDeliveryDate = null;

        if (routingRequest.getDesireDeliveryDate() != null)
            desireDeliveryDate = LocalDate.parse(routingRequest.getDesireDeliveryDate().toString());
        LocalDate deliveryDate = null;

//        ShortDate sendDate = routingRequest.getSendDate();
        List<String> possibleSenderSectors = new ArrayList<>();
        Iterable<Routing.Participant> routingParticipantSender = null;
        if (routingRequest.getSender() != null) {
            routingParticipantSender = queryRoute("S", routingValidDate, sendDate, desireDeliveryDate, routingRequest.getSender(), layer, ctrlTransportUnit, "Sender: ");
            Iterator<Routing.Participant> s = routingParticipantSender.iterator();
            while (s.hasNext()) {
                if (!possibleSenderSectors.contains(s)) {
                    possibleSenderSectors.add(s.next().getSector());
                }
            }
            if (routingParticipantSender.iterator().next().getMessage().equals(""))
                rWSRouting.setSender(routingParticipantSender.iterator().next());
            routingParticipantSender.iterator().next().setMessage(null);
        } else
            rWSRouting.setSender(null);


        Iterable<Routing.Participant> routingParticipantConsignee = null;
        if (routingRequest.getConsignee() != null) {
            routingParticipantConsignee = queryRoute("D", routingValidDate, sendDate, desireDeliveryDate, routingRequest.getConsignee(), layer, ctrlTransportUnit, "Consignee: ");
            Iterator<Routing.Participant> c = routingParticipantConsignee.iterator();
            while (c.hasNext()) {
                if (!possibleSenderSectors.contains(c))
                    possibleSenderSectors.add(c.next().getSector());
            }
            if (routingParticipantConsignee.iterator().next().getMessage().equals("")) {
                rWSRouting.setConsignee(routingParticipantConsignee.iterator().next());
                deliveryDate = routingParticipantConsignee.iterator().next().getDate();
            }
            routingParticipantConsignee.iterator().next().setDate(null);
            routingParticipantConsignee.iterator().next().setMessage(null);
        } else
            rWSRouting.setConsignee(null);


        String[] mViaHubs = {""};// {"NST", "N1"};

        rWSRouting.setSendDate(new ShortDate(sendDate));
        if (deliveryDate != null)
            rWSRouting.setDesireDeliveryDate(new ShortDate(deliveryDate));

        String labelContent = "";
        if (routingRequest.getConsignee() != null)
            labelContent += com.google.common.base.Strings.padEnd(routingParticipantConsignee.iterator().next().getStation().toString(), 3, '0');
        rWSRouting.setLabelContent(labelContent);

        rWSRouting.setViaHubs(mViaHubs);
        rWSRouting.setMessage("OK");

        return rWSRouting;
    }

    public static ShortTime sqlTimeToShortTime(java.sql.Time time) {
        if (time == null)
            return null;
        return new ShortTime(time.toString());
    }


    //    private List<Routing.Participant> queryRoute(ShortDate date,
    private List<Routing.Participant> queryRoute(String sendDelivery,
                                                 LocalDate validdate,
                                                 LocalDate sendDate,
                                                 LocalDate setDeliveryDate,

                                                 RoutingRequest.RequestParticipant requestRequestParticipant,
                                                 Iterable<RoutingLayer> routingLayers,
                                                 Integer ctrl,
                                                 String exeptionPrefix) {


        List<Routing.Participant> resultParticipants = new ArrayList<>();

        //ShortDate date = requestRequestParticipant.getDate();

//        if (date.equals(null)) {
//            throw new IllegalArgumentException(exeptionPrefix + "empty Date");
//        }

//        ShortDate senddateShort = new ShortDate(mSenddate);

//        java.time.LocalDate sd = LocalDate.from(date.getLocalDate());

//        java.sql.Timestamp sqlDate = Timestamp.valueOf(date.toString() + " 00:00:00");

        String country = requestRequestParticipant.getCountry();


        if (country == null) {
            throw new IllegalArgumentException(exeptionPrefix + "empty country");
//            Routing.Participant ret = new Routing.Participant();
//            ret.setMessage("empty country");
//            resultParticipants.add(ret);
//            return resultParticipants;
        } else
            country = country.toUpperCase();

        String zip = requestRequestParticipant.getZip();

        if (zip == null) {
            throw new IllegalArgumentException(exeptionPrefix + "empty zipcode");
//            Routing.Participant ret = new Routing.Participant();
//            ret.setMessage("empty zipcode");
//            resultParticipants.add(ret);
//            return resultParticipants;
        } else
            zip = zip.toUpperCase();


        Country rcountry = mCountryRepository.findOne(country);

        // -------------------------
        // ≤  country.equals(null) ≤
        // -------------------------
        //       ** forever **

        if (rcountry == null) {
            throw new IllegalArgumentException(exeptionPrefix + "unknown country");
//            Routing.Participant ret = new Routing.Participant();
//            ret.setMessage("unknown country");
//            resultParticipants.add(ret);
//            return resultParticipants;
        }

        if (rcountry.getZipFormat().equals("")) {
            throw new IllegalArgumentException(exeptionPrefix + "unknown country");
//            Routing.Participant ret = new Routing.Participant();
//            ret.setMessage("unknown country");
//            resultParticipants.add(ret);
//            return resultParticipants;
        }


        if (zip.length() < rcountry.getMinLen()) {
            throw new IllegalArgumentException(exeptionPrefix + "zipcode too short");
//            Routing.Participant ret = new Routing.Participant();
//            ret.setMessage("zipcode too short");
//            resultParticipants.add(ret);
//            return resultParticipants;
        }

        if (rcountry.getRoutingTyp() < 0 || rcountry.getRoutingTyp() > 3) {
            throw new IllegalArgumentException(exeptionPrefix + "country not enabled");
//            Routing.Participant ret = new Routing.Participant();
//            ret.setMessage("country not enabled");
//            resultParticipants.add(ret);
        }

        if (zip.length() > rcountry.getMaxLen()) {
            throw new IllegalArgumentException(exeptionPrefix + "zipcode too long");
//            Routing.Participant ret = new Routing.Participant();
//            ret.setMessage("zipcode too long");
//            resultParticipants.add(ret);
//            return resultParticipants;
        }

        s2str zRet = parceZip(rcountry.getZipFormat(), zip);

        String zipQuery = zRet.s1;
        String zipConform = zRet.s2;

        if (zipQuery.equals("")) {
            throw new IllegalArgumentException(exeptionPrefix + "wrong zipcode format");
//            Routing.Participant ret = new Routing.Participant();
//            ret.setMessage("wrong zipcode format");
//            resultParticipants.add(ret);
//            return resultParticipants;
        }

//        Function<Time, ShortTime> convertTime = (t) -> {
//            if (t != null)
//                return new ShortTime(t.toString());
//            return null;
//        };
//        mRoutingParticipant.iterator() instanceof ? (() mRoutingParticipant.iterator())
//        :null;


//for routingLayer
        //resultParticipant mRoutingParticipantLayer = null;

//TODO verbessern ?
        Iterator<RoutingLayer> l = routingLayers.iterator();
        while (l.hasNext()) {
            RoutingLayer routingLayer = l.next();
            Routing.Participant resultParticipantLayer = queryRouteLayer(sendDelivery, requestRequestParticipant, zipQuery, validdate, sendDate, setDeliveryDate, routingLayer, ctrl, exeptionPrefix);

            if (!resultParticipantLayer.getStation().equals("0")) {
                resultParticipantLayer.setZipCode(zipConform);
                resultParticipants.add(resultParticipantLayer);
            }

        }

//        resultParticipant e=new resultParticipant();

//        mRoutingParticipantLayer.setDayType();
//        mRoutingParticipantLayer.setRouting(routeFound.getStation());
//        mRoutingParticipantLayer.setZone(routeFound.getArea());
//        mRoutingParticipantLayer.setIsland(routeFound.getIsland() != 0);
//        mRoutingParticipantLayer.setEarliestTimeOfDelivery(sqlTimeToShortTime(routeFound.getEtod()));
//        mRoutingParticipantLayer.setEarliestTimeOfDelivery(new ShortTime(routeFound.getEtod().toString()));
//        if (zipConform != null)
//            mRoutingParticipantLayer.setZipCode(zipConform);
//
//        mRoutingParticipantLayer.setterm(routeFound.getTerm());
//        if (routeFound.getLtodsa() != null)
//            mRoutingParticipantLayer.setSundayDeliveryUntil(new ShortTime(routeFound.getLtodsa().toString()));
//        mRoutingParticipantLayer.setDelieveryDay(getNextDeliveryDay(LocalDate.parse(date.toString()), mCountry, routeFound.getHolidayCtrl()));


//        //.setDayType(getDayType(LocalDate.parse(date.toString()), mCountry, routeFound.getHolidayCtrl()));
//        mRoutingParticipant.setRouting(routeFound.getStation());
//        mRoutingParticipant.setZone(routeFound.getArea());
//        mRoutingParticipant.setIsland(routeFound.getIsland() != 0);
//        mRoutingParticipant.setEarliestTimeOfDelivery(sqlTimeToShortTime(routeFound.getEtod()));
//        mRoutingParticipant.setEarliestTimeOfDelivery(new ShortTime(routeFound.getEtod().toString()));
//        if (zipConform != null)
//            mRoutingParticipant.setZipCode(zipConform);
//
//        mRoutingParticipant.setterm(routeFound.getTerm());
//        if (routeFound.getLtodsa() != null)
//            mRoutingParticipant.setSundayDeliveryUntil(new ShortTime(routeFound.getLtodsa().toString()));
//        mRoutingParticipant.setDelieveryDay(getNextDeliveryDay(LocalDate.parse(date.toString()), mCountry, routeFound.getHolidayCtrl()));


//        convertTime.apply(null);

        return resultParticipants;
    }

    //    private Routing.Participant queryRouteLayer(RoutingRequest.RequestParticipant requestRequestParticipant,
    //                                               String queryZipCode,
//                                                java.sql.Timestamp sqlDate,
    private Routing.Participant queryRouteLayer(String sendDelivery, RoutingRequest.RequestParticipant requestParticipant,
                                                String queryZipCode,
                                                LocalDate validDate,
                                                LocalDate sendDate,
                                                LocalDate desireDeliveryDate,
                                                RoutingLayer routingLayer,
                                                Integer ctrl,
                                                String exeptionPrefix) {

        Routing.Participant mqueryRouteLayer = new Routing.Participant();

// TODO Join
//        JPAQuery qJPARote = new JPAQuery();
//        QStation qStation = QStation.station;
//        qJPARote.from(qRoute).leftJoin(qStation);

        JPAQuery qJPARote = new JPAQuery();
        QRoute qRoute = QRoute.route;
        QStation qStation = QStation.station;
        qJPARote.from(qRoute).innerJoin(qStation);
        BooleanExpression rWhere = null;

        rWhere =
                qRoute.layer.eq(routingLayer.getLayer())
                        .and(qRoute.country.eq(requestParticipant.getCountry().toUpperCase()))
                        .and(qRoute.zipFrom.loe(queryZipCode))
                        .and(qRoute.zipTo.goe(queryZipCode))
                        .and(qRoute.validFrom.before(Timestamp.valueOf(validDate.toString() + " 00:00:00")))
                        .and(qRoute.validTo.after(Timestamp.valueOf(validDate.toString() + " 00:00:00")))
        ;

        qJPARote.where(rWhere);
        Iterable<Route> rRouten = mRouteRepository.findAll(rWhere);
//        QRoute qRoute = QRoute.route;
//        BooleanExpression rWhere = null;
//        rWhere =
//                qRoute.layer.eq(1)
//                        .and(qRoute.country.eq("DE"))
//                        .and(qRoute.zipFrom.eq("64850"))
//        ;
//
//        //  qJPARote.where(rWhere);
//        Iterable<Route> rRouten = mRouteRepository.findAll(rWhere);
//
//        if (!rRouten.iterator().hasNext()) {
//            throw new IllegalArgumentException("no Route found to given data");
//        }

//        List<Route> rRouten = qJPARote.list(qRoute);

//        switch (rcountry.getRoutingTyp()) {
//            case 0:
//                rWhere =
//                        qRoute.country.eq(mCountry)
//                                .and(qRoute.country.eq(zipQuery))
//
//                                .and(qRoute.validFrom.before(mSqlSendDate))
//                .and(qRoute.validTo.after(mSqlSendDate))
//                ;
//
//                break;
//            case 1:
//                rWhere =
//                        qRoute.country.eq(mCountry)
//                                .and(qRoute.country.goe(zipQuery))
//                                .and(qRoute.validFrom.loe(sqlvalidForm));
//                break;
//            case 2:
//                rWhere =
//                        qRoute.country.eq(mCountry)
//                                .and(qRoute.country.goe(zipQuery))
//                                .and(qRoute.validFrom.loe(sqlvalidForm));
//                break;
//        }

//        Iterable<Route> rRouten = mRouteRepository.findAll(rWhere, qRoute.validFrom.desc());

//        Iterable<Route> rRouten = mRouteRepository.findAll(rWhere);

        if (!rRouten.iterator().hasNext())
            throw new IllegalArgumentException(exeptionPrefix + "no Route found");
        else {
            Route routeFound = rRouten.iterator().next();
//TODO Sector aus stationsector

            Station rStation = mStationRepository.findOne(routeFound.getStation());
            mqueryRouteLayer.setSector(rStation.getSector());
            mqueryRouteLayer.setCountry(routeFound.getCountry());
            mqueryRouteLayer.setZipCode(queryZipCode);
//            mqueryRouteLayer.setPartnerManager(rStation.getP);
//        mqueryRouteLayer.setDayType(getDayType(LocalDate.parse(date.toString()), mCountry, mRoutingParticipantLayer.routeFound.getHolidayCtrl()));
            //mqueryRouteLayer.setDayType(getDayType(LocalDate.from(sqlDate.toLocalDateTime()), requestRequestParticipant.getCountry().toUpperCase(), routeFound.getHolidayCtrl()).toString());


            LocalDate deliveryDate;
            mqueryRouteLayer.setTerm(routeFound.getTerm());
            if (sendDelivery == "S") {
//                mqueryRouteLayer.setDayType(getDayType(sendDate, mqueryRouteLayer.getCountry(), routeFound.getHolidayCtrl()).toString());
//TODO nächsten Linientag ermitteln
                mqueryRouteLayer.setDate(sendDate);
//                mqueryRouteLayer.setDate(getNextDeliveryDay(sendDate, mqueryRouteLayer.getCountry(), routeFound.getHolidayCtrl()));
//                if (date.equals(null))
//                    date = getNextDeliveryDay(date, mqueryRouteLayer.getCountry(), routeFound.getHolidayCtrl());

            }
            if (sendDelivery == "D") {
                if (desireDeliveryDate == null)
                    deliveryDate = getNextDeliveryDay(sendDate, mqueryRouteLayer.getCountry(), routeFound.getHolidayCtrl());
                else
                    deliveryDate = desireDeliveryDate;
                mqueryRouteLayer.setDate(deliveryDate);

            }
            mqueryRouteLayer.setDayType(getDayType(mqueryRouteLayer.getDate(), requestParticipant.getCountry().toUpperCase(), routeFound.getHolidayCtrl()).toString());

            mqueryRouteLayer.setStation(routeFound.getStation());
            mqueryRouteLayer.setZone(routeFound.getArea());
            mqueryRouteLayer.setIsland(routeFound.getIsland() != 0);
            mqueryRouteLayer.setEarliestTimeOfDelivery(sqlTimeToShortTime(routeFound.getEtod()));
            mqueryRouteLayer.setEarliestTimeOfDelivery(new ShortTime(routeFound.getEtod().toString()));
//        if (zipConform != null)
//            mqueryRouteLayer.setZipCode(zipConform);
            mqueryRouteLayer.setTerm(routeFound.getTerm());
            if (routeFound.getLtodsa() != null)
                mqueryRouteLayer.setSundayDeliveryUntil(new ShortTime(routeFound.getLtodsa().toString()));
//        mqueryRouteLayer.setDelieveryDay(getNextDeliveryDay(LocalDate.parse(date.toString()), mCountry, routeFound.getHolidayCtrl()));
        }
        return mqueryRouteLayer;
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
        String csNew = "";

        int cCount = 0;

        boolean validZip = true;
        //zipCalcEnd:

        while (j < zipFormat.length() && validZip) {
            if (i + 1 > cZip.length)
                csZip = "";
            else
                csZip = cZip[i];
            csZipFormat = cZipFormat[j];
            csNew = "";
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
                        csNew = csZip;
                    }
                    break;
                case "A":
                    if (csZip == "") {
                        csNew = "";
                        zipQuery = "";
                    } else if (csZip == " ") {
                        validZip = false;
                        break;
                    } else if (Ints.tryParse(csZip) != null) {
                        validZip = false;
                        break;
                    } else {
                        i++;
                        j++;
                        csNew = csZip;
                    }
                    break;
                case "L":
                    if (csZip.equals(""))
                        ;
                    else if (csZip.contains("abcdefghijklmnopqrstuvwxyz0123456789 ")) {
                        i++;
                        j++;
                        csNew = csZip;
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
                    csNew = csZip;
                    break;
                case "-":
                    if (csZip.equals("-")) {
                        i++;
                        j++;
                        csNew = csZip;
                    } else if (Ints.tryParse(csZip) == null) {
                        csNew = "";
                    } else {
                        i++;
                        j = j + 2;
                        csNew = "-" + csZip;
                    }
                    break;
                default:
                    throw new IllegalArgumentException("wrong zipcode formatdescription");
            }


            zipConform = zipConform + csNew;
            if (cCount == 0)
                zipQuery = zipQuery + csNew;

            if (!validZip)
                zipQuery = "";

        }

        return new s2str(zipQuery, zipConform);
    }

    private DayType getDayType(LocalDate date, String Country, String Holidayctrl) {

//    private DayType getDayType(LocalDate javaDate, String Country, String Holidayctrl) {

        DayType daytype = DayType.Workday;

//        DayOfWeek javaday = date.getDayOfWeek();
        DayOfWeek day = date.getDayOfWeek();

        if (day == DayOfWeek.SUNDAY)
            daytype = DayType.Sunday;
        if (day == DayOfWeek.SATURDAY)
            daytype = DayType.Saturday;


        HolidayCtrl rholidayctrl = mHolidayctrlRepostitory.findOne(new HolidayCtrlPK(java.sql.Timestamp.valueOf(date.toString() + " 00:00:00"), Country));

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

    private LocalDate getNextDeliveryDay(LocalDate date, String Country, String Holidayctrl) {
        Integer forDeliveryNecessaryWorkdays = 1;
        if (!getDayType(date, Country, Holidayctrl).equals(DayType.Workday))
            forDeliveryNecessaryWorkdays = 2;

        Integer wokdays = 0;
        do {
            date = date.plusDays(1);
            if (getDayType(date, Country, Holidayctrl).equals(DayType.Workday))
                wokdays++;
        }
        while (wokdays < forDeliveryNecessaryWorkdays);

        return date;
    }

//    @Override
//    public RoutingVia findVia(ShortDate date, String sourceSector, String destinationSector) {
//        return new RoutingVia(new String[]{"S", "X"});
//    }
}
