package org.deku.leo2.node.data.sync;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.ResultSetType;
import org.eclipse.persistence.queries.ScrollableCursor;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.sql.Timestamp;

/**
 * Repository for generic entity access
 * Created by masc on 21.06.15.
 */
public class EntityRepository {
    EntityManager mEntityManager;
    Class mEntityType;

    /**
     * c'tors
     * @param entityManager
     * @param entityType
     */
    public EntityRepository(EntityManager entityManager, Class entityType) {
        mEntityManager = entityManager;
        mEntityType = entityType;
    }

    /**
     * Count of entities newer than specific timestamp
     * @param timestamp
     * @return
     */
    public long countNewerThan(Timestamp timestamp) {
        CriteriaBuilder cb = mEntityManager.getCriteriaBuilder();

        // Crtieria queries
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        // Roots and parameters
        Root croot = cq.from(mEntityType);
        ParameterExpression<Timestamp> cparam = null;
        Predicate prTimestamp = null;
        if (timestamp != null) {
            cparam = cb.parameter(Timestamp.class);
            prTimestamp = cb.greaterThan(croot.get("timestamp"), cparam);
        }

        // Count
        cq.select(cb.count(croot));
        if (prTimestamp != null)
            cq.where(prTimestamp);

        Query q = mEntityManager.createQuery(cq);
        if (cparam != null)
            q.setParameter(cparam, timestamp);

        return (Long)q.getSingleResult();
    }

    /**
     * Find timestamp of newest entity
     * @return
     */
    public Timestamp findNewestTimestamp() {
        CriteriaBuilder cb = mEntityManager.getCriteriaBuilder();

        CriteriaQuery<Timestamp> cq = cb.createQuery(Timestamp.class);

        Root croot = cq.from(mEntityType);
        Expression prTimestamp = cb.max(croot.get("timestamp"));

        cq.select(prTimestamp);

        Query q = mEntityManager.createQuery(cq);

        return (Timestamp)q.getSingleResult();
    }

    /**
     * Find entities newer than specific timestamp
     * @param timestamp
     * @return Cursor
     */
    public ScrollableCursor findNewerThan(Timestamp timestamp) {
        CriteriaBuilder cb = mEntityManager.getCriteriaBuilder();

        // Crtieria queries
        CriteriaQuery cq = cb.createQuery(mEntityType);

        // Roots and parameters
        Root croot = cq.from(mEntityType);
        ParameterExpression<Timestamp> cparam = null;
        Predicate prTimestamp = null;
        if (timestamp != null) {
            cparam = cb.parameter(Timestamp.class);
            prTimestamp = cb.greaterThan(croot.get("timestamp"), cparam);
        }

        // Select
        cq.select(croot);
        if (prTimestamp != null)
            cq.where(prTimestamp);
        // Order by primary key (supposed to keep similar records together for better compression ratio)
        cq.orderBy(cb.asc(croot));

        // Execute entity query
        Query q = mEntityManager.createQuery(cq)
                // Eclipselink specific hints for enabling cursor support, will change result of query to cursor
                .setHint(QueryHints.RESULT_SET_TYPE, ResultSetType.ForwardOnly)
                .setHint(QueryHints.SCROLLABLE_CURSOR, true);
        if (cparam != null)
            q.setParameter(cparam, timestamp);

        return (ScrollableCursor) q.getSingleResult();
    }
}
