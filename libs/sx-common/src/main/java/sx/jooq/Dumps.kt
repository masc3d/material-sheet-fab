package sx.jooq

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.jooq.*
import org.jooq.conf.ParamType
import org.jooq.conf.Settings
import org.jooq.impl.DSL
import java.sql.ResultSet.CONCUR_READ_ONLY
import java.sql.ResultSet.TYPE_FORWARD_ONLY

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
                    // Format values
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

    return Observable.fromIterable(
            // Prepare and fetch jooq cursor
            this
                    .resultSetConcurrency(CONCUR_READ_ONLY)
                    .resultSetType(TYPE_FORWARD_ONLY)
                    .fetchSize(Int.MIN_VALUE)
                    .fetchLazy()
    )
            // Buffer records per statement
            .buffer(100)
            // Create statement from record batch
            .map { records ->
                // Determine table name from record
                val table = (records.get(0) as? TableRecord<*>)
                        ?.getTable()
                        ?: throw IllegalArgumentException("Dump requires table records")

                // Generate statement
                dsl.replace(
                        table = table,
                        values = *records.map { record ->
                            record.fields().map { it.getValue(record) }
                        }.toTypedArray()
                )
                        .getSQL(ParamType.INLINED) + ";\n"
            }
            .doFinally {
                dsl.close()
            }
}
