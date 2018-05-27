package org.deku.leoz.node.data.repository

import org.eclipse.persistence.config.QueryHints
import org.eclipse.persistence.config.ResultSetType
import org.eclipse.persistence.queries.CursoredStream
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
     * @param syncId THe sync id to compare to
     * @param maxCount Maximum count to determine (performance optimization for large tables)
     * @return
     */
    fun countNewerThan(syncId: Long?, maxCount: Long? = null): Long {
        val builder = entityManager.criteriaBuilder

        return when {
            syncId == null || this.hasSyncIdAttribute() == false -> {
                // Plain entity count
                val query = builder.createQuery(Long::class.java)

                query.select(builder.count(
                        query.from(entityType)
                ))

                entityManager.createQuery(query).singleResult.let {
                    if (maxCount != null && it > maxCount)
                        maxCount
                    else
                        it
                }
            }

            else -> {
                // Query for sync-ids with max result (much more efficient than counting over large tables)
                val query = builder.createQuery(Long::class.java)

                // Roots and parameters
                val entityPath = query.from(entityType)

                val syncIdAttribute = this.syncIdAttribute
                        ?: throw IllegalStateException("Entity type [${entityType}] is missing sync-id field")

                val syncIdPath: Path<Long> = entityPath.get<Long>(syncIdAttribute.name)
                val syncIdParam: ParameterExpression<Long> = builder.parameter(Long::class.java)
                val syncIdPredicate: Predicate = builder.greaterThan(syncIdPath, syncIdParam)

                // Count query
                query.select(syncIdPath).where(syncIdPredicate)

                // Execute
                entityManager.createQuery(query).let {
                    it.setParameter(syncIdParam, syncId)

                    if (maxCount != null)
                        it.setMaxResults(maxCount.toInt())

                    it.resultList
                }.count().toLong()
            }
        }
    }

    /**
     * Find timestamp of newest entity
     * @return
     */
    fun findMaxSyncId(): Long? {
        val builder = entityManager.criteriaBuilder
        val query = builder.createQuery(Long::class.java)

        val entityPath = query.from(entityType)

        val syncIdAttribute = this.syncIdAttribute
                ?: return null

        val syncIdPath = entityPath.get<Long>(syncIdAttribute.name)

        // Execute
        return entityManager
                .createQuery(
                        query.select(builder.max(syncIdPath))
                )
                .singleResult
    }

    /**
     * Remove all entities
     */
    fun removeAll() {
        val builder = entityManager.criteriaBuilder
        val query = builder.createCriteriaDelete(entityType)

        entityManager.createQuery(query)
                .executeUpdate()
    }

    /**
     * Find entities newer than specific timestamp.
     * The resultset is ordered by timestamp
     * @param syncId
     * *
     * @return Cursor
     */
    fun findNewerThan(syncId: Long?, maxResults: Int): CursoredStream {
        val builder = entityManager.criteriaBuilder
        val query = builder.createQuery(entityType)

        // Roots and parameters
        val entityPath = query.from(entityType)
        var syncIdParam: ParameterExpression<Long>? = null
        var syncIdPredicate: Predicate? = null
        var syncIdPath: Path<Long>? = null
        val syncIdAttribute = syncIdAttribute
        if (syncIdAttribute != null && syncId != null) {
            syncIdPath = entityPath.get(syncIdAttribute.name)
            syncIdParam = builder.parameter(Long::class.java)
            syncIdPredicate = builder.greaterThan(syncIdPath, syncIdParam)
        }

        // Select
        @Suppress("UNCHECKED_CAST")
        query.select(entityPath as Selection<out Nothing>)
        if (syncIdPredicate != null)
            query.where(syncIdPredicate)

        if (syncIdPath != null)
            query.orderBy(builder.asc(syncIdPath))

        // Execute entity query
        val q = entityManager.createQuery(query)
                // Eclipselink specific hints for enabling cursor support, will change result of query to cursor
                .setHint(QueryHints.RESULT_SET_TYPE, ResultSetType.ForwardOnly)
                .setHint(QueryHints.CURSOR, true)
                .setMaxResults(maxResults)

        if (syncIdParam != null)
            q.setParameter(syncIdParam, syncId)

        return (q.singleResult as CursoredStream)
    }
}
