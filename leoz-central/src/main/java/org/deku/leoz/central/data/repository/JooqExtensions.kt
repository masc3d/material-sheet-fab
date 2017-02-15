package org.deku.leoz.central.data.repository

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException
import org.jooq.Record
import org.jooq.ResultQuery

/**
 * Helper for executing a prepared statement and making sure the statement is closed when
 * a communication failure with the database occurs.
 * This method is MYSQL driver specific as it handles MYSQL specific communication exceptions.
 * Created by masc on 15-Feb-17.
 */
fun <R, T> ResultQuery<R>.prepared(block: (q: ResultQuery<R>) -> T): T where R : Record {
    try {
        return block(
                this.keepStatement(true)
        )
    } catch(e: Throwable) {
        when (e) {
            is com.mysql.jdbc.exceptions.jdbc4.CommunicationsException,
            is com.mysql.jdbc.CommunicationsException -> {
                this.close()
            }
        }
        throw e
    }
}