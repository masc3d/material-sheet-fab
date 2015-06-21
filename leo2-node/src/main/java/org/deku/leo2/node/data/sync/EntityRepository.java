package org.deku.leo2.node.data.sync;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.ResultSetType;
import org.eclipse.persistence.queries.ScrollableCursor;

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
    Optional<Attribute> mTimestampAttribute;
    Path mTimestampPath = null;

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
     * Get timestamp jpa path
     * @return Metamodel path for timestamp field or null if it doesn't exist
     */
    private Path getTimestampPath(Root root) {
        if (mTimestampPath == null) {
            // Look for timestamp field within jpa metamodel
            Set<Attribute> attrs = root.getModel().getAttributes();
            Optional<Attribute> attr = attrs.stream().filter(a -> a.getName().equalsIgnoreCase("timestamp")).findFirst();
            if (attr.isPresent()) {
                mTimestampPath = root.get(attr.get().getName());
            }
        }
        return mTimestampPath;
    }

    private Attribute getTimestampAttribute() {
        if (mTimestampAttribute == null) {
            Set<Attribute> attrs = mEntityManager.getMetamodel().managedType(mEntityType).getAttributes();
            mTimestampAttribute = attrs.stream().filter(a -> a.getName().equalsIgnoreCase("timestamp")).findFirst();
        }
        return mTimestampAttribute.isPresent() ? mTimestampAttribute.get() : null;
    }

    /**
     * Indicates if entity has a timestamp attribute
     * @return
     */
    public boolean hasTimestampAttribute() {
        return this.getTimestampAttribute() != null;
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
        Path pathTimestamp = this.getTimestampPath(croot);
        Predicate prTimestamp = null;
        if (pathTimestamp != null && timestamp != null) {
            cparam = cb.parameter(Timestamp.class);
            prTimestamp = cb.greaterThan(this.getTimestampPath(croot), cparam);
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
        Path pathTimestamp = this.getTimestampPath(croot);

        if (pathTimestamp == null)
            return null;

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
        Path<Timestamp> pathTimestamp = this.getTimestampPath(croot);
        if (pathTimestamp != null && timestamp != null) {
            cparam = cb.parameter(Timestamp.class);
            prTimestamp = cb.greaterThan(pathTimestamp, cparam);
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
