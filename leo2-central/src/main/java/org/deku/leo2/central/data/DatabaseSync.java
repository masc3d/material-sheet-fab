package org.deku.leo2.central.data;

import com.google.common.base.Stopwatch;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.path.DateTimePath;
import com.mysema.query.types.path.EntityPathBase;
import org.deku.leo2.central.data.entities.jooq.Tables;
import org.deku.leo2.central.data.entities.jooq.tables.records.*;
import org.deku.leo2.central.data.repositories.jooq.GenericJooqRepository;
import org.deku.leo2.node.PersistenceContext;
import org.deku.leo2.node.data.entities.*;
import org.deku.leo2.node.data.repositories.*;
import org.jooq.Record;
import org.jooq.Result;
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
import java.util.logging.Logger;

/**
 * Created by masc on 15.05.15.
 */
@Named
public class DatabaseSync {
    private static Logger mLog = Logger.getLogger(DatabaseSync.class.getName());

    @javax.persistence.PersistenceContext
    EntityManager mEntityManager;

    TransactionTemplate mTransaction;

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
    public DatabaseSync(@Qualifier(PersistenceContext.DB_EMBEDDED) PlatformTransactionManager tx) {
        mTransaction = new TransactionTemplate(tx);
        mTransaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Transactional(value = PersistenceContext.DB_EMBEDDED)
    public void sync() {
        this.sync(false);
    }

    @Transactional(value = PersistenceContext.DB_EMBEDDED)
    public void sync(boolean reload) {
        boolean alwaysDelete = reload;

        this.updateEntities(
                mStationRepository,
                QStation.station,
                null,
                Tables.TBLDEPOTLISTE,
                null,
                (s) -> convert(s),
                alwaysDelete);

        this.updateEntities(
                mCountryRepository,
                QCountry.country,
                QCountry.country.timestamp,
                Tables.COUNTRY,
                org.deku.leo2.central.data.entities.jooq.tables.Country.COUNTRY.TIMESTAMP,
                (s) -> convert(s),
                alwaysDelete);

        this.updateEntities(
                mHolidayCtrlRepository,
                QHolidayctrl.holidayctrl,
                QHolidayctrl.holidayctrl.timestamp,
                Tables.HOLIDAYCTRL,
                org.deku.leo2.central.data.entities.jooq.tables.Holidayctrl.HOLIDAYCTRL.TIMESTAMP,
                (s) -> convert(s),
                alwaysDelete);

        this.updateEntities(
                mRouteRepository,
                QRoute.route,
                QRoute.route.timestamp,
                Tables.ROUTE,
                org.deku.leo2.central.data.entities.jooq.tables.Route.ROUTE.TIMESTAMP,
                (s) -> convert(s),
                alwaysDelete);

        this.updateEntities(
                mSectorRepository,
                QSector.sector,
                QSector.sector.timestamp,
                Tables.SECTOR,
                org.deku.leo2.central.data.entities.jooq.tables.Sector.SECTOR.TIMESTAMP,
                (s) -> convert(s),
                alwaysDelete);
    }

    /**
     * Convert tbldepotliste mysql record to jpa entity
     * @param depotlisteRecord
     * @return
     */
    private static Station convert(TbldepotlisteRecord depotlisteRecord) {
        Station s = new Station();

        // Map tbldepotliste record to station
        // TODO: map correctly, this is just random
        s.setStationId(depotlisteRecord.getId());
        s.setStationNr(depotlisteRecord.getId());
        s.setAdress1(depotlisteRecord.getFirma1());
        s.setAdress2(depotlisteRecord.getFirma2());
        s.setBillingCity(depotlisteRecord.getOrt());

        return s;
    }

    /**
     * Convert mysql country record to jpa entity
     * @param cr
     * @return
     */
    private static Country convert(CountryRecord cr) {
        Country c = new Country();

        c.setLkz(cr.getLkz());
        c.setLname(cr.getLname());
        c.setMaxLen(cr.getMaxlen());
        c.setMinLen(cr.getMinlen());
        c.setRoutingTyp(cr.getRoutingtyp().intValue());
        c.setTimestamp(cr.getTimestamp());
        c.setZipFormat(cr.getZipformat());

        return c;
    }

    /**
     * Convert mysql holidayctrl record to jpa entity
     * @param cr
     * @return
     */
    private static Holidayctrl convert(HolidayctrlRecord cr) {
        Holidayctrl d = new Holidayctrl();

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
    private static Sector convert(SectorRecord cr) {
        Sector d = new Sector();

        d.setProduct(cr.getProduct());
        d.setSectorfrom(cr.getSectorfrom());
        d.setSectorto(cr.getSectorto());
        d.setTimestamp(cr.getTimestamp());
        d.setValidfrom(cr.getValidfrom());
        d.setValidto(cr.getValidto());

        return d;
    }

    /**
     * Convert mysql country record to jpa entity
     * @param sr
     * @return
     */
    private static Route convert(RouteRecord sr) {
        Route d = new Route();

        d.setArea(sr.getArea());
        d.setEtod(sr.getEtod());
        d.setEtod2(sr.getEtod2());
        d.setHolidayctrl(sr.getHolidayctrl());
        d.setIsland(sr.getIsland());
        d.setLkz(sr.getLkz());
        d.setLtodholiday(sr.getLtodholiday());
        d.setLtodsa(sr.getLtodsa());
        d.setLtop(sr.getLtop());
        d.setLtop2(sr.getLtop2());
        d.setProduct(sr.getProduct());
        d.setSector(sr.getSector());
        d.setStation(sr.getStation());
        d.setTimestamp(sr.getTimestamp());
        d.setTransittime(sr.getTransittime());
        d.setValidfrom(sr.getValidfrom());
        d.setValidto(sr.getValidto());
        d.setZip(sr.getZip());

        return d;
    }

    /**
     * Generic updater for entites from jooq to jpa
     *
     * @param destRepository
     * @param destQdslEntityPath
     * @param destQdslTimestampPath
     * @param sourceTable
     * @param sourceTableField
     * @param conversionFunction
     * @param <TEntity>
     * @param <TCentralRecord>
     */
    private <TEntity, TCentralRecord extends Record> void updateEntities (
            JpaRepository<TEntity, ?> destRepository,
            EntityPathBase<TEntity> destQdslEntityPath,
            DateTimePath<Timestamp> destQdslTimestampPath,
            TableImpl<TCentralRecord> sourceTable,
            TableField<TCentralRecord, Timestamp> sourceTableField,
            Function<TCentralRecord, TEntity> conversionFunction,
            boolean deleteBeforeUpdate) {

        JPAQuery query = new JPAQuery(mEntityManager);

        Stopwatch sw = Stopwatch.createStarted();

        Function<String, Void> log = (m) -> {
            mLog.info(destQdslEntityPath.getType().getName() + " " + m + " " + sw.toString());
            return null;
        };

        mEntityManager.setFlushMode(FlushModeType.COMMIT);

        if (deleteBeforeUpdate || destQdslEntityPath == null ||destQdslTimestampPath == null ) {
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

        // Get newer records from central
        log.apply("Fetching");
        Result<TCentralRecord> source = mGenericJooqRepository.findNewerThan(timestamp, sourceTable, sourceTableField);
        log.apply(String.format("Fetched %d", source.size()));

        // Save to embedded
        //TODO: saving/transaction commit gets very slow when deleting and inserting within the same transaction
        //destRepository.save((Iterable<TEntity>) source.stream().map(d -> conversionFunction.apply(d))::iterator);
        // It's also faster to flush and clear in between
        log.apply("Inserting");
        mTransaction.execute((ts) -> {
            int i = 0;
            for (TEntity r : (Iterable<TEntity>)source.stream().map(d -> conversionFunction.apply(d))::iterator) {
                destRepository.save(r);
                if (i++ % 100 == 0) {
                    mEntityManager.flush();
                    mEntityManager.clear();
                }
            }
            return null;
        });
    }
}
