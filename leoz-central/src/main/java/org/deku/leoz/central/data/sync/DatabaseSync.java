package org.deku.leoz.central.data.sync;

import com.google.common.base.Stopwatch;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.path.DateTimePath;
import com.mysema.query.types.path.EntityPathBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leoz.central.data.entities.jooq.Tables;
import org.deku.leoz.central.data.entities.jooq.tables.*;
import org.deku.leoz.central.data.entities.jooq.tables.records.*;
import org.deku.leoz.central.data.repositories.GenericRepository;
import org.deku.leoz.node.data.PersistenceConfiguration;
import org.deku.leoz.node.data.entities.master.*;
import org.deku.leoz.node.data.entities.system.Property;
import org.deku.leoz.node.data.entities.system.QProperty;
import org.deku.leoz.node.data.repositories.master.*;
import org.deku.leoz.node.data.repositories.system.PropertyRepository;
import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.impl.TableImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import sx.event.EventDelegate;
import sx.event.EventDispatcher;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import java.sql.Timestamp;
import java.util.function.Function;
import java.util.stream.StreamSupport;

/**
 * Created by masc on 15.05.15.
 */
@Named
public class DatabaseSync {
    private static Log mLog = LogFactory.getLog(DatabaseSync.class);

    //region Events
    public interface EventListener extends sx.event.EventListener {
        /** Emitted when entities have been updated */
        void onUpdate(Class entityType, Timestamp currentTimestamp);
    }

    private EventDispatcher<EventListener> mEventDispatcher = EventDispatcher.createThreadSafe();
    public EventDelegate<EventListener> getEventDelegate() { return mEventDispatcher; }
    //endregion

    @javax.persistence.PersistenceContext
    private EntityManager mEntityManager;

    // Transaction helpers
    private TransactionTemplate mTransaction;
    private TransactionTemplate mTransactionJooq;

    // Repositories
    @Inject
    private GenericRepository mSyncRepository;
    @Inject
    private StationRepository mStationRepository;
    @Inject
    private CountryRepository mCountryRepository;
    @Inject
    private RouteRepository mRouteRepository;
    @Inject
    private HolidayctrlRepository mHolidayCtrlRepository;
    @Inject
    private SectorRepository mSectorRepository;
    @Inject
    private PropertyRepository mPropertyRepository;
    @Inject
    private RoutingLayerRepository mRoutingLayerRepository;
    @Inject
    private StationSectorRepository mStationSectorRepository;

    @Inject
    public DatabaseSync(@Qualifier(PersistenceConfiguration.DB_EMBEDDED) PlatformTransactionManager tx,
                        @Qualifier(org.deku.leoz.central.data.PersistenceConfiguration.DB_CENTRAL) PlatformTransactionManager txJooq) {
        mTransaction = new TransactionTemplate(tx);
        mTransaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        mTransactionJooq = new TransactionTemplate(txJooq);
    }

    @Transactional(value = PersistenceConfiguration.DB_EMBEDDED)
    public void sync() {
        this.sync(false);
    }

    @Transactional(value = PersistenceConfiguration.DB_EMBEDDED)
    public void sync(boolean reload) {
        Stopwatch sw = Stopwatch.createStarted();

        boolean alwaysDelete = reload;

        this.updateEntities(
                Tables.MST_STATION,
                MstStation.MST_STATION.TIMESTAMP,
                mStationRepository,
                QStation.station,
                QStation.station.timestamp,
                (s) -> convert(s),
                alwaysDelete);

        this.updateEntities(
                Tables.MST_COUNTRY,
                MstCountry.MST_COUNTRY.TIMESTAMP,
                mCountryRepository,
                QCountry.country,
                QCountry.country.timestamp,
                (s) -> convert(s),
                alwaysDelete);

        this.updateEntities(
                Tables.MST_HOLIDAYCTRL,
                MstHolidayctrl.MST_HOLIDAYCTRL.TIMESTAMP,
                mHolidayCtrlRepository,
                QHolidayCtrl.holidayCtrl,
                QHolidayCtrl.holidayCtrl.timestamp,
                (s) -> convert(s),
                alwaysDelete);

        this.updateEntities(
                Tables.MST_ROUTE,
                MstRoute.MST_ROUTE.TIMESTAMP,
                mRouteRepository,
                QRoute.route,
                QRoute.route.timestamp,
                (s) -> convert(s),
                alwaysDelete);

        this.updateEntities(
                Tables.MST_SECTOR,
                MstSector.MST_SECTOR.TIMESTAMP,
                mSectorRepository,
                QSector.sector,
                QSector.sector.timestamp,
                (s) -> convert(s),
                alwaysDelete);

        this.updateEntities(
                Tables.MST_ROUTINGLAYER,
                MstRoutinglayer.MST_ROUTINGLAYER.TIMESTAMP,
                mRoutingLayerRepository,
                QRoutingLayer.routingLayer,
                QRoutingLayer.routingLayer.timestamp,
                (s) -> convert(s),
                alwaysDelete);

        this.updateEntities(
                Tables.SYS_PROPERTY,
                SysProperty.SYS_PROPERTY.TIMESTAMP,
                mPropertyRepository,
                QProperty.property,
                QProperty.property.timestamp,
                (s) -> convert(s),
                alwaysDelete
        );

        this.updateEntities(
                Tables.MST_STATION_SECTOR,
                MstStationSector.MST_STATION_SECTOR.TIMESTAMP,
                mStationSectorRepository,
                QStationSector.stationSector,
                QStationSector.stationSector.timestamp,
                (s) -> convert(s),
                alwaysDelete
        );

        mLog.info("Database sync took " + sw.toString());
    }

    /**
     * Convert mysql mst_station record to jpa entity
     * @param ds
     * @return
     */
    private static Station convert(MstStationRecord ds) {
        Station s = new Station();

        s.setStationNr(ds.getStationNr());
        s.setTimestamp(ds.getTimestamp());
        s.setAddress1(ds.getAddress1());
        s.setAddress2(ds.getAddress2());
        s.setBillingAddress1(ds.getBillingAddress1());
        s.setBillingAddress2(ds.getBillingAddress2());
        s.setBillingCity(ds.getBillingCity());
        s.setBillingCountry(ds.getBillingCountry());
        s.setBillingHouseNr(ds.getBillingHouseNr());
        s.setBillingStreet(ds.getBillingStreet());
        s.setBillingZip(ds.getBillingZip());
        s.setCity(ds.getCity());
        s.setContactPerson1(ds.getContactPerson1());
        s.setContactPerson2(ds.getContactPerson2());
        s.setCountry(ds.getCountry());
        s.setEmail(ds.getEmail());
        s.setHouseNr(ds.getHouseNr());
        s.setMobile(ds.getMobile());
        s.setPhone1(ds.getPhone1());
        s.setPhone2(ds.getPhone2());
        s.setPosLat(ds.getPoslat());
        s.setPosLong(ds.getPoslong());
        s.setSector(ds.getSectors());
        s.setServicePhone1(ds.getServicePhone1());
        s.setServicePhone2(ds.getServicePhone2());
        // TODO: strang? strange? ;)
        s.setStrang(null);
        s.setStreet(ds.getStreet());
        s.setTelefax(ds.getTelefax());
        s.setuStId(ds.getUstid());
        s.setWebAddress(ds.getWebAddress());
        s.setZip(ds.getZip());
        return s;
    }

    /**
     * Convert mysql country record to jpa entity
     * @param cr
     * @return
     */
    private static Country convert(MstCountryRecord cr) {
        Country c = new Country();

        c.setCode(cr.getCode());
//        c.getNameStringId(cr.getNameStringid() );
        c.setTimestamp(cr.getTimestamp());
        c.setRoutingTyp(cr.getRoutingTyp());
        c.setMinLen(cr.getMinLen());
        c.setMaxLen(cr.getMaxLen());
        c.setZipFormat(cr.getZipFormat());

        return c;
    }

    /**
     * Convert mysql holidayctrl record to jpa entity
     * @param cr
     * @return
     */
    private static HolidayCtrl convert(MstHolidayctrlRecord cr) {
        HolidayCtrl d = new HolidayCtrl();

        d.setCountry(cr.getCountry());
        d.setCtrlPos(cr.getCtrlPos());
        d.setDescription(cr.getDescription());
        d.setHoliday(cr.getHoliday());
        d.setTimestamp(cr.getTimestamp());

        return d;
    }

    /**
     * Convert mysql sector record to jpa entity
     * @param cr
     * @return
     */
    private static Sector convert(MstSectorRecord cr) {
        Sector d = new Sector();

        d.setSectorFrom(cr.getSectorfrom());
        d.setSectorTo(cr.getSectorto());
        d.setTimestamp(cr.getTimestamp());
        d.setValidFrom(cr.getValidfrom());
        d.setValidTo(cr.getValidto());

        return d;
    }

    /**
     * Convert mysql value record to jpa entity
     * @param rs
     * @return
     */
    private static RoutingLayer convert(MstRoutinglayerRecord rs) {
        RoutingLayer d = new RoutingLayer();

        d.setLayer(rs.getLayer());
        d.setServices(rs.getServices());
        d.setDescription(rs.getDescription());
        d.setTimestamp(rs.getTimestamp());

        return d;
    }


    /**
     * Convert mysql route record to jpa entity
     * @param sr
     * @return
     */
    private static Route convert(MstRouteRecord sr) {
        Route d = new Route();

        d.setLayer(sr.getLayer());
        d.setCountry(sr.getCountry());
        d.setZipFrom(sr.getZipfrom());
        d.setZipTo(sr.getZipto());
        d.setValidCRTR(sr.getValidCtrl());
        d.setValidFrom(sr.getValidfrom());
        d.setValidTo(sr.getValidto());
        d.setTimestamp(sr.getTimestamp());
        d.setStation(sr.getStation());
        d.setArea(sr.getArea());
        d.setEtod(sr.getEtod());
        d.setLtop(sr.getLtop());
        d.setTerm(sr.getTerm());
        d.setSaturdayOK(sr.getSaturdayOk());
        d.setLtodsa(sr.getLtodsa());
        d.setLtodholiday(sr.getLtodholiday());
        d.setIsland(sr.getIsland());
        d.setHolidayCtrl(sr.getHolidayctrl());

        return d;
    }

    /**
     * Convert mysql properties record to jpa entity
     * @param sp
     * @return
     */
    private static Property convert(SysPropertyRecord sp) {
        Property p = new Property();
        p.setId(sp.getId());

        p.setStation(sp.getStation());

        p.setDescription(sp.getDescription());
        p.setValue(sp.getValue());
        p.setEnabled(sp.getEnabled() != 0);
        p.setTimestamp(sp.getTimestamp());

        return p;
    }

    /**
     * Convert mysql stationsectors record to jpa entity
     * @param ss
     * @return
     */
    private static StationSector convert(MstStationSectorRecord ss) {
        StationSector s = new StationSector();
        s.setStationNr(ss.getStationNr());
        s.setSector(ss.getSector());
        s.setRoutingLayer(ss.getRoutingLayer());
        s.setTimestamp(ss.getTimestamp());
        return s;
    }


    /**
     * Generic updater for entites from jooq to jpa
     * @param sourceTable           JOOQ source table
     * @param sourceTableField      JOOQ source timestamp field
     * @param destRepository        Destination JPA repository
     * @param destQdslEntityPath    Destination QueryDSL entity table path
     * @param destQdslTimestampPath Destination QueryDSL timestamp field path
     * @param conversionFunction    Conversion function JOOQ record -> JPA entity
     * @param <TEntity>             Type of destiantion JPA entity
     * @param <TCentralRecord>      Type of source JOOQ record
     */
    private <TCentralRecord extends Record, TEntity> void updateEntities(
            TableImpl<TCentralRecord> sourceTable,
            TableField<TCentralRecord, Timestamp> sourceTableField,
            JpaRepository<TEntity, ?> destRepository,
            EntityPathBase<TEntity> destQdslEntityPath,
            DateTimePath<Timestamp> destQdslTimestampPath,
            Function<TCentralRecord, TEntity> conversionFunction,
            boolean deleteBeforeUpdate) {

        JPAQuery query = new JPAQuery(mEntityManager);

        Stopwatch sw = Stopwatch.createStarted();

        Function<String, String> lfmt = s -> "[" + destQdslEntityPath.getType().getName() + "] " + s + " " + sw.toString();

        mEntityManager.setFlushMode(FlushModeType.COMMIT);

        if (deleteBeforeUpdate || destQdslEntityPath == null || destQdslTimestampPath == null) {
            mTransaction.execute((ts) -> {
                mLog.info(lfmt.apply("Deleting"));
                destRepository.deleteAllInBatch();
                mEntityManager.flush();
                mEntityManager.clear();
                return null;
            });
        }

        // Get latest timestamp
        Timestamp timestamp = null;
        if (destQdslEntityPath != null && destQdslTimestampPath != null) {
            timestamp = query.from(destQdslEntityPath).singleResult(destQdslTimestampPath.max());
            mLog.info(lfmt.apply(String.format("Current destination timestamp [%s]", timestamp)));
        }
        final Timestamp fTimestamp = timestamp;

        // Get newer records from central
        // masc20150530. JOOQ cursor requires an explicit transaction
        mTransactionJooq.execute((tsJooq) -> {
            mLog.info(lfmt.apply("Fetching"));
            Iterable<TCentralRecord> source = mSyncRepository.findNewerThan(fTimestamp, sourceTable, sourceTableField);
            mLog.info(lfmt.apply(String.format("Fetched")));

            if (source.iterator().hasNext()) {
                // Save to embedded
                //TODO: saving/transaction commit gets very slow when deleting and inserting within the same transaction
                //destRepository.save((Iterable<TEntity>) source.stream().map(d -> conversionFunction.apply(d))::iterator);
                // It's also faster to flush and clear in between
                mLog.info(lfmt.apply("Inserting"));
                mTransaction.execute((ts) -> {
                    int i = 0;
                    for (TEntity r : (Iterable<TEntity>) StreamSupport.stream(source.spliterator(), false).map(d -> conversionFunction.apply(d))::iterator) {
                        destRepository.save(r);
                        if (i++ % 100 == 0) {
                            mEntityManager.flush();
                            mEntityManager.clear();
                        }
                    }
                    return null;
                });
                mLog.info(lfmt.apply("Inserted"));

                // Emit update event
                mEventDispatcher.emit(e -> e.onUpdate(destQdslEntityPath.getType(), fTimestamp));
            }
            return null;
        });
        mLog.info(lfmt.apply("Done"));
    }
}
