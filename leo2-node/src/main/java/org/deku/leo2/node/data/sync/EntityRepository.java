package org.deku.leo2.node.data.sync;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.ResultSetType;
import org.eclipse.persistence.queries.ScrollableCursor;
import sx.LazyInstance;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.Attribute;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.Set;

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
     * Timestamp attribute from jpa metamodel
     */
    LazyInstance<Attribute> mTimestampAttribute = new LazyInstance<>(() -> {
        Set<Attribute> attrs = mEntityManager.getMetamodel().managedType(mEntityType).getAttributes();
        Optional<Attribute> ts = attrs.stream().filter(a -> a.getName().equalsIgnoreCase("timestamp")).findFirst();
        return (ts.isPresent()) ? ts.get() : null;
    });

    /**
     * Indicates if entity has a timestamp attribute
     * @return
     */
    public boolean hasTimestampAttribute() {
        return mTimestampAttribute.get() != null;
    }

    /**
     * Count of entities newer than specific timestamp
     * @param timestamp
     * @return
     */
    public long countNewerThan(Timestamp timestamp) {
        CriteriaBuilder cb = mEntityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        // Roots and parameters
        Root croot = cq.from(mEntityType);
        ParameterExpression<Timestamp> cparam = null;
        Predicate prTimestamp = null;
        if (mTimestampAttribute.get() != null && timestamp != null) {
            cparam = cb.parameter(Timestamp.class);
            Path pathTimestamp = croot.get(mTimestampAttribute.get().getName());
            prTimestamp = cb.greaterThanOrEqualTo(pathTimestamp, cparam);
        }

        // Count query
        cq.select(cb.count(croot));
        if (prTimestamp != null)
            cq.where(prTimestamp);

        // Execute
        Query q = mEntityManager.createQuery(cq);
        if (cparam != null)
            q.setParameter(cparam, timestamp);
        return (Long)q.getSingleResult();
    }

    /**
     * Find timestamp of newest entity
     * @return
     */
    public Timestamp findMaxTimestamp() {
        CriteriaBuilder cb = mEntityManager.getCriteriaBuilder();
        CriteriaQuery<Timestamp> cq = cb.createQuery(Timestamp.class);

        Root croot = cq.from(mEntityType);

        if (mTimestampAttribute.get() == null)
            return null;

        Path pathTimestamp = croot.get(mTimestampAttribute.get().getName());
        Expression prTimestamp = cb.max(pathTimestamp);
        cq.select(prTimestamp);

        // Execute
        Query q = mEntityManager.createQuery(cq);
        return (Timestamp)q.getSingleResult();
    }

    /**
     * Remove all entities
     */
    public void removeAll() {
        CriteriaBuilder cb = mEntityManager.getCriteriaBuilder();
        CriteriaDelete cd = cb.createCriteriaDelete(mEntityType);

        Query q = mEntityManager.createQuery(cd);
        q.executeUpdate();
    }

    /**
     * Find entities newer than specific timestamp.
     * The resultset is ordered by timestamp
     * @param timestamp
     * @return Cursor
     */
    public ScrollableCursor findNewerThan(Timestamp timestamp) {
        CriteriaBuilder cb = mEntityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(mEntityType);

        // Roots and parameters
        Root croot = cq.from(mEntityType);
        ParameterExpression<Timestamp> cparam = null;
        Predicate prTimestamp = null;
        Path<Timestamp> pathTimestamp = null;
        if (mTimestampAttribute.get() != null && timestamp != null) {
            pathTimestamp = croot.get(mTimestampAttribute.get().getName());
            cparam = cb.parameter(Timestamp.class);
            prTimestamp = cb.greaterThanOrEqualTo(pathTimestamp, cparam);
        }

        // Select
        cq.select(croot);
        if (prTimestamp != null)
            cq.where(prTimestamp);

        if (pathTimestamp != null)
            cq.orderBy(cb.asc(pathTimestamp));

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
