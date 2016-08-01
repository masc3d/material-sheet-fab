package org.deku.leoz.node.data.repositories;

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
    LazyInstance<Attribute> mSyncIdAttribute = new LazyInstance<>(() -> {
        Set<Attribute> attrs = mEntityManager.getMetamodel().managedType(mEntityType).getAttributes();
        Optional<Attribute> ts = attrs.stream().filter(a -> a.getName().equalsIgnoreCase("syncId")).findFirst();
        return (ts.isPresent()) ? ts.get() : null;
    });

    /**
     * Indicates if entity has sync id attribute
     * @return
     */
    public boolean hasSyncIdAttribute() {
        return mSyncIdAttribute.get() != null;
    }

    /**
     * Count of entities newer than specific sync id
     * @param syncId
     * @return
     */
    public long countNewerThan(Long syncId) {
        CriteriaBuilder cb = mEntityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        // Roots and parameters
        Root croot = cq.from(mEntityType);
        ParameterExpression<Long> cparam = null;
        Predicate prSyncId = null;
        if (mSyncIdAttribute.get() != null && syncId != null) {
            cparam = cb.parameter(Long.class);
            Path pathSyncId = croot.get(mSyncIdAttribute.get().getName());
            prSyncId = cb.greaterThan(pathSyncId, cparam);
        }

        // Count query
        cq.select(cb.count(croot));
        if (prSyncId != null)
            cq.where(prSyncId);

        // Execute
        Query q = mEntityManager.createQuery(cq);
        if (cparam != null)
            q.setParameter(cparam, syncId);
        return (Long)q.getSingleResult();
    }

    /**
     * Find timestamp of newest entity
     * @return
     */
    public Long findMaxSyncId() {
        CriteriaBuilder cb = mEntityManager.getCriteriaBuilder();
        CriteriaQuery<Timestamp> cq = cb.createQuery(Timestamp.class);

        Root croot = cq.from(mEntityType);

        if (mSyncIdAttribute.get() == null)
            return null;

        Path pathSyncId = croot.get(mSyncIdAttribute.get().getName());
        Expression prSyncId = cb.max(pathSyncId);
        cq.select(prSyncId);

        // Execute
        Query q = mEntityManager.createQuery(cq);
        return (Long)q.getSingleResult();
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
     * @param syncId
     * @return Cursor
     */
    public ScrollableCursor findNewerThan(Long syncId) {
        CriteriaBuilder cb = mEntityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(mEntityType);

        // Roots and parameters
        Root croot = cq.from(mEntityType);
        ParameterExpression<Long> cparam = null;
        Predicate prSyncId = null;
        Path<Long> pathSyncId = null;
        if (mSyncIdAttribute.get() != null && syncId != null) {
            pathSyncId = croot.get(mSyncIdAttribute.get().getName());
            cparam = cb.parameter(Long.class);
            prSyncId = cb.greaterThan(pathSyncId, cparam);
        }

        // Select
        cq.select(croot);
        if (prSyncId != null)
            cq.where(prSyncId);

        if (pathSyncId != null)
            cq.orderBy(cb.asc(pathSyncId));

        // Execute entity query
        Query q = mEntityManager.createQuery(cq)
                // Eclipselink specific hints for enabling cursor support, will change result of query to cursor
                .setHint(QueryHints.RESULT_SET_TYPE, ResultSetType.ForwardOnly)
                .setHint(QueryHints.SCROLLABLE_CURSOR, true);
        if (cparam != null)
            q.setParameter(cparam, syncId);

        return (ScrollableCursor) q.getSingleResult();
    }
}
