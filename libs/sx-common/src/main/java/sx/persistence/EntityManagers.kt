package sx.persistence

import sx.annotationOfType
import javax.persistence.EntityManager
import javax.persistence.Table

/**
 * Generic persistence extensionss
 */

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
    val result: T
    val et = this.transaction
    et.begin()
    try {
        result = block()
        et.commit()
    } finally {
        if (et.isActive)
            et.rollback()
    }
    return result
}

