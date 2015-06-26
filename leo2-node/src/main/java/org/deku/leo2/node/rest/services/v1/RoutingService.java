package org.deku.leo2.node.rest.services.v1;

import com.google.common.primitives.Ints;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;
import org.deku.leo2.node.data.entities.*;
import org.deku.leo2.node.data.repositories.*;
import org.deku.leo2.rest.entities.ShortDate;
import org.deku.leo2.rest.entities.ShortTime;
import org.deku.leo2.rest.entities.v1.*;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
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
//        if (mProduct.equals(null)) {
//            //Set default Product
//            mProduct = "A";
//        } else
//            mProduct = mProduct.toUpperCase();
        Integer mServices = routingRequest.getServices();

// TODO REAL oder Volumen ???
        Float mWeight = routingRequest.getWeight();


// Unit CTRLs

        Integer mCTRLTransportUnit = 1;

        if (routingRequest.getWeight() > 100)
            mCTRLTransportUnit = mCTRLTransportUnit + 2;

        if ((mServices & 256) == 256)
            mCTRLTransportUnit = mCTRLTransportUnit + 4;

        if ((mServices & 512) == 512)
            mCTRLTransportUnit = mCTRLTransportUnit + 8;


// check usable Layers


        QRoutingLayer qLayer = QRoutingLayer.routingLayer;
        BooleanExpression rWhereLayer = qLayer.layer.eq(mCTRLTransportUnit);
        Iterable<RoutingLayer> mLayer = mRoutingLayerRepository.findAll();
//TODO rWereLayer bitmaske suchen

        ShortDate mSendDate = routingRequest.getSendDate();
        List<String> mPossibleSenderSectors = new ArrayList<>();

        Iterable<RoutingParticipant> mRoutingParticipantSender = queryRoute(mSendDate, routingRequest.getSender(), mLayer, mCTRLTransportUnit, "Sender: ");

        Iterator<RoutingParticipant> s = mRoutingParticipantSender.iterator();
        while (s.hasNext()) {
            if (!mPossibleSenderSectors.contains(s))
                mPossibleSenderSectors.add(s.next().getSector());
        }



        ShortDate mDeliveryDate = routingRequest.getDeliverDate();
//        List<String> mPossibleConigneeSectors = new ArrayList<>();

        Iterable<RoutingParticipant> mRoutingParticipantConsignee = queryRoute(mDeliveryDate,routingRequest.getSender(), mLayer, mCTRLTransportUnit, "Consignee: ");

        Iterator<RoutingParticipant> c = mRoutingParticipantConsignee.iterator();
        while (c.hasNext()) {
            if (!mPossibleSenderSectors.contains(c))
                mPossibleSenderSectors.add(c.next().getSector());
        }

        String[] mViaHubs = {"NST", "N"};

        rWSRouting.setSendDate(mSendDate);
        rWSRouting.setDeliveryDate(mDeliveryDate);

        rWSRouting.setRoutingSender(mRoutingParticipantSender.iterator().next());
        rWSRouting.setRoutingConsignee(mRoutingParticipantConsignee.iterator().next());
        rWSRouting.setViaHubs(mViaHubs);
        rWSRouting.setLabelContent("NST N");
        rWSRouting.setMessage("OK");

        return rWSRouting;
    }

    public static ShortTime sqlTimeToShortTime(java.sql.Time time) {
        if (time == null)
            return null;
        return new ShortTime(time.toString());
    }


    private List<RoutingParticipant> queryRoute(ShortDate mDate,  Participant mParticipant, Iterable<RoutingLayer> mRoutingLayers, Integer mCTRL, String mExeptionPrefix) {

//        List<RoutingParticipant> mRoutingParticipants = new List<RoutingParticipant>() {
//            @Override
//            public int size() {
//                return 0;
//            }
//
//            @Override
//            public boolean isEmpty() {
//                return false;
//            }
//
//            @Override
//            public boolean contains(Object o) {
//                return false;
//            }
//
//            @Override
//            public Iterator<RoutingParticipant> iterator() {
//                return null;
//            }
//
//            @Override
//            public Object[] toArray() {
//                return new Object[0];
//            }
//
//            @Override
//            public <T> T[] toArray(T[] a) {
//                return null;
//            }
//
//            @Override
//            public boolean add(RoutingParticipant routingParticipant) {
//                return false;
//            }
//
//            @Override
//            public boolean remove(Object o) {
//                return false;
//            }
//
//            @Override
//            public boolean containsAll(Collection<?> c) {
//                return false;
//            }
//
//            @Override
//            public boolean addAll(Collection<? extends RoutingParticipant> c) {
//                return false;
//            }
//
//            @Override
//            public boolean addAll(int index, Collection<? extends RoutingParticipant> c) {
//                return false;
//            }
//
//            @Override
//            public boolean removeAll(Collection<?> c) {
//                return false;
//            }
//
//            @Override
//            public boolean retainAll(Collection<?> c) {
//                return false;
//            }
//
//            @Override
//            public void clear() {
//
//            }
//
//            @Override
//            public RoutingParticipant get(int index) {
//                return null;
//            }
//
//            @Override
//            public RoutingParticipant set(int index, RoutingParticipant element) {
//                return null;
//            }
//
//            @Override
//            public void add(int index, RoutingParticipant element) {
//
//            }
//
//            @Override
//            public RoutingParticipant remove(int index) {
//                return null;
//            }
//
//            @Override
//            public int indexOf(Object o) {
//                return 0;
//            }
//
//            @Override
//            public int lastIndexOf(Object o) {
//                return 0;
//            }
//
//            @Override
//            public ListIterator<RoutingParticipant> listIterator() {
//                return null;
//            }
//
//            @Override
//            public ListIterator<RoutingParticipant> listIterator(int index) {
//                return null;
//            }
//
//            @Override
//            public List<RoutingParticipant> subList(int fromIndex, int toIndex) {
//                return null;
//            }
//        };

//        RoutingParticipant mRoutingParticipant = new RoutingParticipant();

        List<RoutingParticipant> mRoutingParticipants = new ArrayList<>();

        //ShortDate mDate = mParticipant.getDate();

        if (mDate.equals(null)) {
            throw new IllegalArgumentException(mExeptionPrefix + "empty Date");
        }

//        ShortDate senddateShort = new ShortDate(mSenddate);

        java.time.LocalDate sd = LocalDate.from(mDate.getLocalDate());

        java.sql.Timestamp mSqlDate = Timestamp.valueOf(mDate.toString() + " 00:00:00");

        String mCountry = mParticipant.getCountry();

        if (mCountry.equals(null)) {
            throw new IllegalArgumentException(mExeptionPrefix + "empty Country");
        } else
            mCountry = mCountry.toUpperCase();

        String mZip = mParticipant.getZip();

        if (mZip.equals(null)) {
            throw new IllegalArgumentException(mExeptionPrefix + "empty Zipcode");
        } else
            mZip = mZip.toUpperCase();


        Country rcountry = mCountryRepository.findOne(mCountry);

        // -------------------------
        // ≤  country.equals(null) ≤
        // -------------------------
        //       ** forever **

        if (rcountry == null) {
            throw new IllegalArgumentException(mExeptionPrefix + "unknown Country");
        }

        if (rcountry.getZipFormat().equals("")) {
            throw new IllegalArgumentException(mExeptionPrefix + "unknown Country");
        }


        if (mZip.length() < rcountry.getMinLen()) {
            throw new IllegalArgumentException(mExeptionPrefix + "Zipcode to short");
        }

        if (rcountry.getRoutingTyp() < 0 || rcountry.getRoutingTyp() > 3) {
            throw new IllegalArgumentException(mExeptionPrefix + "Country not enabled");
        }

        if (mZip.length() > rcountry.getMaxLen()) {
            throw new IllegalArgumentException(mExeptionPrefix + "Zipcode to long");
        }

        s2str Zret = parceZip(rcountry.getZipFormat(), mZip);

        String mZipQuery = Zret.s1;
        String zipConform = Zret.s2;

        if (mZipQuery.equals("")) {
            throw new IllegalArgumentException(mExeptionPrefix + "Zipcode not conform");
        }

//        Function<Time, ShortTime> convertTime = (t) -> {
//            if (t != null)
//                return new ShortTime(t.toString());
//            return null;
//        };
//        mRoutingParticipant.iterator() instanceof ? (() mRoutingParticipant.iterator())
//        :null;


//for mRoutingLayer
        //RoutingParticipant mRoutingParticipantLayer = null;

//TODO verbessern ?
        Iterator<RoutingLayer> l = mRoutingLayers.iterator();
        while (l.hasNext()) {

            RoutingLayer mRoutingLayer = l.next();
            RoutingParticipant mRoutingParticipantLayer = queryRouteLayer(mParticipant, mZipQuery, mSqlDate, mRoutingLayer, mCTRL, mExeptionPrefix);

            if (!mRoutingParticipantLayer.getStation().equals("0"))
                mRoutingParticipants.add(mRoutingParticipantLayer);
        }

//        RoutingParticipant e=new RoutingParticipant();

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
//        mRoutingParticipantLayer.setDelieveryDay(getNextDeliveryDay(LocalDate.parse(mDate.toString()), mCountry, routeFound.getHolidayCtrl()));


//        //.setDayType(getDayType(LocalDate.parse(mDate.toString()), mCountry, routeFound.getHolidayCtrl()));
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
//        mRoutingParticipant.setDelieveryDay(getNextDeliveryDay(LocalDate.parse(mDate.toString()), mCountry, routeFound.getHolidayCtrl()));


//        convertTime.apply(null);

        return mRoutingParticipants;
    }

    private RoutingParticipant queryRouteLayer(Participant mParticipant, String mQueryZipCode, java.sql.Timestamp mSqlDate, RoutingLayer mRoutingLayer, Integer mCTRL, String mExeptionPrefix) {

        RoutingParticipant mqueryRouteLayer = new RoutingParticipant();

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
                qRoute.layer.eq(mRoutingLayer.getLayer())
                        .and(qRoute.country.eq(mParticipant.getCountry().toUpperCase()))
                        .and(qRoute.zipFrom.loe(mQueryZipCode))
                        .and(qRoute.zipTo.goe(mQueryZipCode))
                        .and(qRoute.validFrom.before(mSqlDate))
                        .and(qRoute.validTo.after(mSqlDate))
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

        if (rRouten.iterator().hasNext()) {
            Route routeFound = rRouten.iterator().next();
//TODO Sector aus stationsector

            Station rStation = mStationRepository.findOne(routeFound.getStation());
            mqueryRouteLayer.setSector(rStation.getSector());

//        mqueryRouteLayer.setDayType(getDayType(LocalDate.parse(mDate.toString()), mCountry, mRoutingParticipantLayer.routeFound.getHolidayCtrl()));
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
//        mqueryRouteLayer.setDelieveryDay(getNextDeliveryDay(LocalDate.parse(mDate.toString()), mCountry, routeFound.getHolidayCtrl()));
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
                    } else if (Ints.tryParse(csZip) == null) {
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
                zipQuery = "";

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

//    @Override
//    public RoutingVia findVia(ShortDate date, String sourceSector, String destinationSector) {
//        return new RoutingVia(new String[]{"S", "X"});
//    }
}
