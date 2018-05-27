package sx.persistence.querydsl

import com.querydsl.core.types.EntityPath
import com.querydsl.core.types.Predicate
import com.querydsl.jpa.impl.JPADeleteClause
import com.querydsl.jpa.impl.JPAQuery
import javax.persistence.EntityManager

/**
 * QueryDSL specific persistence extensions
 * Created by masc on 27.01.18.
 */

/**
 * Create QueryDSL delete clause.
 *
 * NOTE: Batch deleting entities may not trigger jpa entity events.
 *
 * @param entityPath QueryDSL entity path
 */
fun <T> EntityManager.batchDelete(entityPath: EntityPath<T>): JPADeleteClause =
        JPADeleteClause(this, entityPath)

/**
 * Delete entities.
 *
 * NOTE: This implementation uses entity manager remove and will reliably trigger entity delete events
 *
 * @param entityPath QueryDSL entity path
 * @param predicate QueryDSL predicate
 */
fun <T> EntityManager.delete(entityPath: EntityPath<T>, predicate: Predicate) {
    JPAQuery<T>(this)
            .from(entityPath)
            .where(predicate)
            .fetch()
            .forEach {
                this.remove(it)
            }
}

/**
 * Create QueryDSL query
 */
fun <T> EntityManager.from(entityPath: EntityPath<T>): JPAQuery<T> =
        JPAQuery<T>(this).from(entityPath)