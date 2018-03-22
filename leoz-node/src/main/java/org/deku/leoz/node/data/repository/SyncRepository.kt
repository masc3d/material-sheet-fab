package org.deku.leoz.node.data.repository

import org.deku.leoz.node.data.jpa.LclSync
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import sx.persistence.querydsl.from
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

interface SyncRepositoryExtension {

    /**
     * Find min max sync id
     * @param entityPath querydsl entity table path
     * @param syncIdPath querydsl sync id field path
     */
    fun <TEntity> findSyncIdMinMax(
            entityPath: com.querydsl.core.types.dsl.EntityPathBase<TEntity>,
            syncIdPath: com.querydsl.core.types.dsl.NumberPath<Long>
    ): LongRange?
}

class SyncRepositoryExtensionImpl : SyncRepositoryExtension {
    @PersistenceContext
    private lateinit var em: EntityManager

    override fun <TEntity> findSyncIdMinMax(
            entityPath: com.querydsl.core.types.dsl.EntityPathBase<TEntity>,
            syncIdPath: com.querydsl.core.types.dsl.NumberPath<Long>

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
}