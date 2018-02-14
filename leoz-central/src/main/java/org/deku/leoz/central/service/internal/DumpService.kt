package org.deku.leoz.central.service.internal

import io.reactivex.Observable
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables.*
import org.jooq.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import sx.jooq.dump
import sx.rs.attachment
import sx.rs.toStreamingOutput
import sx.rx.subscribeOn
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.core.*


/**
 * Dump service
 * Created by masc on 14.02.18.
 */
@Named
@Path("internal/v1/dump")
class DumpService : org.deku.leoz.service.internal.DumpService {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dsl: DSLContext

    @Inject
    private lateinit var executorService: ExecutorService

    // TODO: security: only admin role

    /** Timestamp format used for dumps */
    private val timestampFormat by lazy { SimpleDateFormat("yyyyMMddHHmmss") }

    /**
     * Transform jooq select to streaming REST response
     * @param name Base name of response attachment
     */
    private fun <R : Record> Select<R>.toResponse(name: String = "dump"): Response {
        val filename = "${name}-${timestampFormat.format(Date())}.sql"

        return this
                .dump()
                .subscribeOn(executorService)
                .toStreamingOutput()
                .let {
                    Response.ok()
                            .attachment(entity = it, filename = filename)
                            .build()
                }
    }

    override fun dumpCentralStations(): Response {
        return dsl.selectFrom(TBLDEPOTLISTE)
                .toResponse("tbldepotliste")
    }

    override fun dumpCentralRoutes(): Response {
        return dsl.selectFrom(MST_ROUTE)
                .toResponse("mst_route")
    }
}