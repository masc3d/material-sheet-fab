package org.deku.leoz.node.data.repository

import org.eclipse.persistence.config.QueryHints
import org.eclipse.persistence.config.ResultSetType
import org.eclipse.persistence.queries.ScrollableCursor
import javax.persistence.EntityManager
import javax.persistence.criteria.ParameterExpression
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Selection

/**
 * Repository for generic entity access
 * Created by masc on 21.06.15.
 * @param entityManager
 * @param entityType
 */
class EntityRepository(
        private val entityManager: EntityManager,
        private val entityType: Class<*>) {

    /**
     * Sync attribute from jpa metamodel
     */
    private val syncIdAttribute by lazy {
        entityManager.metamodel.managedType(entityType).attributes.filter {
            it.name.equals("syncid", ignoreCase = true)
        }.firstOrNull()
    }

    /**
     * Indicates if entity has sync id attribute
     * @return
     */
    fun hasSyncIdAttribute(): Boolean {
        return syncIdAttribute != null
    }

    /**
     * Count of entities newer than specific sync id
     * @param syncId
     * @return
     */
    fun countNewerThan(syncId: Long?): Long {
        val cb = entityManager.criteriaBuilder
        val cq = cb.createQuery(Long::class.java)

        // Roots and parameters
        val croot = cq.from(entityType)
        var cparam: ParameterExpression<Long>? = null
        var prSyncId: Predicate? = null
        val syncIdAttribute = this.syncIdAttribute
        if (syncIdAttribute != null && syncId != null) {
            cparam = cb.parameter(Long::class.java)
            val pathSyncId = croot.get<Long>(syncIdAttribute.name)
            prSyncId = cb.greaterThan(pathSyncId, cparam)
        }

        // Count query
        cq.select(cb.count(croot))
        if (prSyncId != null)
            cq.where(prSyncId)

        // Execute
        val q = entityManager.createQuery(cq)
        if (cparam != null)
            q.setParameter(cparam, syncId)
        return q.singleResult
    }

    /**
     * Find timestamp of newest entity
     * @return
     */
    fun findMaxSyncId(): Long? {
        val cb = entityManager.criteriaBuilder
        val cq = cb.createQuery(Long::class.java)

        val croot = cq.from(entityType)

        val syncIdAttribute = this.syncIdAttribute
        if (syncIdAttribute == null)
            return null

        val pathSyncId = croot.get<Long>(syncIdAttribute.name)
        val prSyncId = cb.max(pathSyncId)
        cq.select(prSyncId)

        // Execute
        val q = entityManager.createQuery(cq)
        return q.singleResult
    }

    /**
     * Remove all entities
     */
    fun removeAll() {
        val cb = entityManager.criteriaBuilder
        val cd = cb.createCriteriaDelete(entityType)

        val q = entityManager.createQuery(cd)
        q.executeUpdate()
    }

    /**
     * Find entities newer than specific timestamp.
     * The resultset is ordered by timestamp
     * @param syncId
     * *
     * @return Cursor
     */
    fun findNewerThan(syncId: Long?): ScrollableCursor {
        val cb = entityManager.criteriaBuilder
        val cq = cb.createQuery(entityType)

        // Roots and parameters
        val croot = cq.from(entityType)
        var cparam: ParameterExpression<Long>? = null
        var prSyncId: Predicate? = null
        var pathSyncId: Path<Long>? = null
        val syncIdAttribute = syncIdAttribute
        if (syncIdAttribute != null && syncId != null) {
            pathSyncId = croot.get(syncIdAttribute.name)
            cparam = cb.parameter(Long::class.java)
            prSyncId = cb.greaterThan(pathSyncId, cparam)
        }

        // Select
        @Suppress("UNCHECKED_CAST")
        cq.select(croot as Selection<out Nothing>)
        if (prSyncId != null)
            cq.where(prSyncId)

        if (pathSyncId != null)
            cq.orderBy(cb.asc(pathSyncId))

        // Execute entity query
        val q = entityManager.createQuery(cq)
                .setHint(QueryHints.RESULT_SET_TYPE, ResultSetType.ForwardOnly)// Eclipselink specific hints for enabling cursor support, will change result of query to cursor
                .setHint(QueryHints.SCROLLABLE_CURSOR, true)
        if (cparam != null)
            q.setParameter(cparam, syncId)

        return q.singleResult as ScrollableCursor
    }
}
