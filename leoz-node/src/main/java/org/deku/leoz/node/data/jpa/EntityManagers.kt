package org.deku.leoz.node.data.jpa

import sx.annotationOfType
import javax.persistence.EntityManager
import javax.persistence.Table

/**
 * Truncate table using a native query
 * @param entityClass Entity class
 * Created by masc on 18.01.18.
 */
fun EntityManager.truncate(entityClass: Class<*>) {
    val tableName = entityClass.annotationOfType(Table::class.java).name
    this.createNativeQuery("TRUNCATE TABLE ${tableName}").executeUpdate()
}

/**
 * Perform transaction
 * @param em EntityManager
 * @param block Block to run within a transaction
 */
@Throws(Exception::class)
fun <T> EntityManager.transaction(block: () -> T): T {
    val t: T
    val et = this.transaction
    try {
        et.begin()
        t = block()
        et.commit()
    } catch (e: Exception) {
        throw e
    } finally {
        if (et.isActive)
            et.rollback()
    }
    return t
}
