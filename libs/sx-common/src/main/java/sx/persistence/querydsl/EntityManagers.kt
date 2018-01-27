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
 * Create QueryDSL delete clause
 */
fun <T> EntityManager.delete(entityPath: EntityPath<T>): JPADeleteClause =
        JPADeleteClause(this, entityPath)

/**
 * Create QueryDSL query
 */
fun <T> EntityManager.from(entityPath: EntityPath<T>): JPAQuery<T> =
        JPAQuery<T>(this).from(entityPath)