package org.deku.leo2.central.data.sync;

import com.google.common.base.Stopwatch;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.path.DateTimePath;
import com.mysema.query.types.path.EntityPathBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.central.data.entities.jooq.Tables;
import org.deku.leo2.central.data.entities.jooq.tables.*;
import org.deku.leo2.central.data.entities.jooq.tables.records.*;
import org.deku.leo2.central.data.repositories.jooq.GenericJooqRepository;
import org.deku.leo2.node.data.PersistenceContext;
import org.deku.leo2.node.data.entities.*;
import org.deku.leo2.node.data.repositories.*;
import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.impl.TableImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

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

    @javax.persistence.PersistenceContext
    EntityManager mEntityManager;

    TransactionTemplate mTransaction;
    TransactionTemplate mTransactionJooq;

    @Inject
    GenericJooqRepository mGenericJooqRepository;
    @Inject
    StationRepository mStationRepository;
    @Inject
    CountryRepository mCountryRepository;
    @Inject
    RouteRepository mRouteRepository;
    @Inject
    HolidayctrlRepository mHolidayCtrlRepository;
    @Inject
    SectorRepository mSectorRepository;
    @Inject
    ValueRepository mValueRepository;
    @Inject
    RoutingLayerRepository mRoutingLayerRepository;

    @Inject
    public DatabaseSync(@Qualifier(PersistenceContext.DB_EMBEDDED) PlatformTransactionManager tx,
                        @Qualifier(org.deku.leo2.central.data.PersistenceContext.DB_CENTRAL) PlatformTransactionManager txJooq) {
        mTransaction = new TransactionTemplate(tx);
        mTransaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        mTransactionJooq = new TransactionTemplate(txJooq);
    }

    @Transactional(value = PersistenceContext.DB_EMBEDDED)
    public void sync() {
        this.sync(false);
    }

    @Transactional(value = PersistenceContext.DB_EMBEDDED)
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
                MstRoute.MST_ROUTE.MST_ROUTE.TIMESTAMP,
                mRouteRepository,
                QRoute.route,
                QRoute.route.timestamp,
                (s) -> convert(s),
                alwaysDelete);

        this.updateEntities(
                Tables.MST_SECTOR,
                MstSector.MST_SECTOR.MST_SECTOR.TIMESTAMP,
                mSectorRepository,
                QSector.sector,
                QSector.sector.timestamp,
                (s) -> convert(s),
                alwaysDelete);

        this.updateEntities(
                Tables.SYS_ROUTINGLAYER,
                SysRoutinglayer.SYS_ROUTINGLAYER.SYS_ROUTINGLAYER.TIMESTAMP,
                mRoutingLayerRepository,
                QRoutingLayer.routingLayer,
                QRoutingLayer.routingLayer.timestamp,
                (s) -> convert(s),
                alwaysDelete);



        mLog.info("Database sync took " + sw.toString());
    }

    /**
     * Convert mysql mst_station record to jpa entity
     * @param ds
     * @return
     */
    private static Station convert(MstStationRecord ds) {
        Station s = new Station();

        s.setStationNr(ds.getStationnr());
        s.setSector(ds.getSectors());
        //TODO rest

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
        c.setName(cr.getName());
        c.setTimestamp(cr.getTimestamp());
        c.setRoutingTyp(cr.getRoutingtyp());
        c.setMinLen(cr.getMinlen());
        c.setMaxLen(cr.getMaxlen());
        c.setZipFormat(cr.getZipformat());

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
        d.setCtrlPos(cr.getCtrlpos());
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
     * @param cs
     * @return
     */
    private static Values convert(SysValuesRecord cs) {
        Values d = new Values();

        d.setTyp(cs.getTyp());
        d.setId(cs.getId());
        d.setSort(cs.getSort());
        d.setDescription(cs.getDescription());
        d.setP1i(cs.getP1i());
        d.setP2i(cs.getP2i());
        d.setP3s(cs.getP3s());
        d.setP4s(cs.getP4s());


        return d;
    }

    /**
     * Convert mysql value record to jpa entity
     * @param rs
     * @return
     */
    private static RoutingLayer convert(SysRoutinglayerRecord rs) {
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
        d.setValidCRTR(sr.getValidctrl());
        d.setValidFrom(sr.getValidfrom());
        d.setValidTo(sr.getValidto());
        d.setTimestamp(sr.getTimestamp());
        d.setStation(sr.getStation());
        d.setArea(sr.getArea());
        d.setEtod(sr.getEtod());
        d.setLtop(sr.getLtop());
        d.setTerm(sr.getTerm());
        d.setSaturdayOK(sr.getSaturdayok());
        d.setLtodsa(sr.getLtodsa());
        d.setLtodholiday(sr.getLtodholiday());
        d.setIsland(sr.getIsland());
        d.setHolidayCtrl(sr.getHolidayctrl());

        return d;
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

        Function<String, Void> log = (m) -> {
            mLog.info(destQdslEntityPath.getType().getName() + " " + m + " " + sw.toString());
            return null;
        };

        mEntityManager.setFlushMode(FlushModeType.COMMIT);

        if (deleteBeforeUpdate || destQdslEntityPath == null || destQdslTimestampPath == null) {
            mTransaction.execute((ts) -> {
                log.apply("Deleting");
                destRepository.deleteAllInBatch();
                mEntityManager.flush();
                mEntityManager.clear();
                return null;
            });
        }

        // Get latest timestamp
        Timestamp timestamp = null;
        if (destQdslEntityPath != null && destQdslTimestampPath != null) {
            log.apply("Timestamp check");
            timestamp = query.from(destQdslEntityPath).singleResult(destQdslTimestampPath.max());
        }
        final Timestamp fTimestamp = timestamp;

        // Get newer records from central
        // masc20150530. JOOQ cursor requires an explicit transaction
        mTransactionJooq.execute((tsJooq) -> {
            log.apply("Fetching");
            Iterable<TCentralRecord> source = mGenericJooqRepository.findNewerThan(fTimestamp, sourceTable, sourceTableField);
            log.apply(String.format("Fetched"));

            // Save to embedded
            //TODO: saving/transaction commit gets very slow when deleting and inserting within the same transaction
            //destRepository.save((Iterable<TEntity>) source.stream().map(d -> conversionFunction.apply(d))::iterator);
            // It's also faster to flush and clear in between
            log.apply("Inserting");
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
            log.apply("Inserted");
            return null;
        });
        log.apply("Done");
    }
}
