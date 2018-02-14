package org.deku.leoz.central.service.internal

import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables.*
import org.jooq.*
import org.jooq.conf.ParamType
import org.jooq.conf.RenderFormatting
import org.jooq.conf.Settings
import org.jooq.impl.DSL
import org.jooq.impl.DefaultConfiguration
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import sx.jooq.dump
import sx.log.slf4j.trace
import sx.rs.toStreamingOutput
import java.io.BufferedWriter
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.core.Response
import javax.ws.rs.core.StreamingOutput
import java.io.OutputStreamWriter
import java.sql.ResultSet
import javax.servlet.WriteListener
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.WebApplicationException
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.Suspended
import javax.ws.rs.core.Context


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

    // TODO: security: only admin role

    override fun dumpCentralStations(): StreamingOutput {
        return dsl.selectFrom(TBLDEPOTLISTE)
                .dump()
                .toStreamingOutput()
    }

    override fun dumpCentralRoutes(): StreamingOutput {
        return dsl.selectFrom(MST_ROUTE)
                .dump()
                .toStreamingOutput()
    }
}