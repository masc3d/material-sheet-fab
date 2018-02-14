package sx.jooq

import io.reactivex.Observable
import java.io.OutputStream
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.StreamingOutput
import org.jooq.*
import org.jooq.conf.ParamType
import org.jooq.conf.Settings
import org.jooq.impl.DSL

/**
 * Created by masc on 14.02.18.
 */

/**
 * Workaround for missing MYSQL specific REPLACE statement
 */
fun <R : Record> DSLContext.replace(table: Table<R>, vararg values: List<Any>): ResultQuery<Record> {
    val valuesTemplate = values.mapIndexed { index, list ->
        "({${index + 2}})"
    }.joinToString(",")

    return this.resultQuery("REPLACE INTO {0} ({1}) VALUES ${valuesTemplate}",
            // Query part arguments
            *kotlin.arrayOf(
                    table,
                    DSL.list(table.fields().map { it.unqualifiedName })
            ).plus(
                    values.map { DSL.list(it.map(DSL::`val`)) }
            )
    )
}

/**
 * Dump jooq select
 * @param table Table spec
 * @return Observable sql statements
 */
fun <R : Record> Select<R>.dump(): Observable<String> {
    // Create specific DSL for dump formatting
    val dsl = DSL.using(
            // Clone global configuration
            this.configuration().derive().set(
                    // Change render formatting
                    Settings()
                            // TODO: current formatting ugly, need advanced `RenderFormatting()` coming in jooq-3.11
                            .withRenderFormatted(true)
            )
    )

    return io.reactivex.Observable.fromIterable(
            this
                    .resultSetConcurrency(java.sql.ResultSet.CONCUR_READ_ONLY)
                    .resultSetType(java.sql.ResultSet.TYPE_FORWARD_ONLY)
                    .fetchSize(kotlin.Int.MIN_VALUE)
                    .fetchLazy()
    )
            .buffer(100)
            .map { records ->
                // Determine table name from record
                val table = (records.get(0) as? TableRecord<*>)
                        ?.getTable()
                        ?: throw IllegalArgumentException("Select does not contain table records")

                // Generate statement
                dsl.replace(
                        table = table,
                        values = *records.map { record ->
                            record.fields().map { it.getValue(record) }
                        }.toTypedArray()
                )
                        .getSQL(ParamType.INLINED)
            }
            .doFinally {
                dsl.close()
            }
            .subscribeOn(io.reactivex.schedulers.Schedulers.computation())
}
