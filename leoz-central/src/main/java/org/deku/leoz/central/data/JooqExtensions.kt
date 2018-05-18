package org.deku.leoz.central.data

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException
import org.jooq.Record
import org.jooq.ResultQuery
import org.jooq.exception.DataAccessException
import org.jooq.types.UByte
import org.jooq.types.UInteger
import java.math.BigDecimal

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
        if (e.isJooqAccessException()) {
            try {
                this.close()
            } catch(e: Throwable) {
                // Ignore exception on statement close
            }
        }

        throw e
    }
}

/** Jooq access or jdbc communication problem */
fun Throwable.isJooqAccessException(): Boolean {
    return setOf<Class<*>>(
            CommunicationsException::class.java,
            com.mysql.jdbc.CommunicationsException::class.java,
            DataAccessException::class.java
    ).let {
        it.contains(this.javaClass) || (this.cause != null && it.contains(this.cause!!.javaClass))
    }
}

// JOOQ unsigned type extension methods

fun Int.toUInteger(): UInteger {
    return UInteger.valueOf(this)
}
fun Int.toUByte(): UByte {
    return UByte.valueOf(this)
}
fun BigDecimal.toDoubleWithTwoDecimalDigits():Double{
    return this.setScale(2,BigDecimal.ROUND_HALF_UP).toDouble()
}