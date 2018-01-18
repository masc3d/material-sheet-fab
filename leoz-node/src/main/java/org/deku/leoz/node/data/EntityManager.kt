package org.deku.leoz.node.data

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