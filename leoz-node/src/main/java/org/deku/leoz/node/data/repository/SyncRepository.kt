package org.deku.leoz.node.data.repository

import com.querydsl.core.types.EntityPath
import com.querydsl.core.types.dsl.NumberPath
import org.deku.leoz.node.data.jpa.LclSync
import org.eclipse.persistence.config.QueryHints
import org.eclipse.persistence.config.ResultSetType
import org.eclipse.persistence.queries.CursoredStream
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import sx.persistence.querydsl.from
import sx.util.letWithNotNull
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 * Sync repository
 * Created by JT on 29.06.15.
 */
interface SyncRepository :
        JpaRepository<LclSync, Long>,
        QuerydslPredicateExecutor<LclSync>,
        SyncRepositoryExtension

/** */

interface SyncRepositoryExtension {

    /**
     * Find min max sync id
     * @param entityPath querydsl entity table path
     * @param syncIdPath querydsl sync id field path
     */
    fun <TEntity> findSyncIdMinMax(
            entityPath: EntityPath<TEntity>,
            syncIdPath: NumberPath<Long>
    ): LongRange?

    fun <TEntity> findAllSyncIds(
            entityPath: EntityPath<TEntity>,
            syncIdPath: NumberPath<Long>
    ): Set<Long>

    fun <TEntity> count(
            entityPath: EntityPath<TEntity>
    ): Long

    fun <TEntity> countNewerThan(
            entityPath: EntityPath<TEntity>,
            syncIdPath: NumberPath<Long>,
            syncId: Long?): Long

    fun <TEntity> findNewerThan(
            entityPath: EntityPath<TEntity>,
            syncIdPath: NumberPath<Long>,
            syncId: Long?,
            maxResults: Int): CursoredStream
}

/** */

class SyncRepositoryExtensionImpl : SyncRepositoryExtension {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun <TEntity> findSyncIdMinMax(
            entityPath: EntityPath<TEntity>,
            syncIdPath: NumberPath<Long>

    ): LongRange? {
        return em.from(entityPath)
                .select(syncIdPath.min(), syncIdPath.max())
                .fetchFirst()
                .let {
                    val min = it.get(0, Long::class.java) ?: return@let null
                    val max = it.get(1, Long::class.java) ?: return@let null

                    (min..max)
                }
    }

    override fun <TEntity> findAllSyncIds(
            entityPath: EntityPath<TEntity>,
            syncIdPath: NumberPath<Long>
    ): Set<Long> {

        return em.from(entityPath)
                .select(syncIdPath)
                .where(syncIdPath.isNotNull)
                .fetch()
                .toSet()
    }

    /**
     * Find newer records with cursored stream support
     */
    override fun <TEntity> findNewerThan(
            entityPath: EntityPath<TEntity>,
            syncIdPath: NumberPath<Long>,
            syncId: Long?,
            maxResults: Int): CursoredStream {

        val q = em.from(entityPath)
                // Restrict by sync-id
                .letWithNotNull(syncId, { where(syncIdPath.gt(it)) })
                .orderBy(syncIdPath.asc())
                .createQuery()
                // Eclipselink specific hints for enabling dwcursor support, will change result of query to cursor
                .setHint(QueryHints.RESULT_SET_TYPE, ResultSetType.ForwardOnly)
                .setHint(QueryHints.CURSOR, true)
                .setMaxResults(maxResults)

        return (q.singleResult as CursoredStream)
    }

    override fun <TEntity> count(entityPath: EntityPath<TEntity>): Long {
        return em.from(entityPath).fetchCount()
    }

    /**
     * Count of entities newer than specific sync id
     * @param syncId THe sync id to compare to or null to count all
     * @return
     */
    override fun <TEntity> countNewerThan(
            entityPath: EntityPath<TEntity>,
            syncIdPath: NumberPath<Long>,
            syncId: Long?): Long {

        return em.from(entityPath)
                .letWithNotNull(syncId, { where(syncIdPath.gt(syncId)) })
                .fetchCount()
    }
}