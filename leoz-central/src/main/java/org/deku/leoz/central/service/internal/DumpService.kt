package org.deku.leoz.central.service.internal

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables.*
import org.deku.leoz.node.rest.authorizedUser
import org.deku.leoz.service.entity.ShortDate
import org.jooq.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import sx.jooq.dump
import sx.log.slf4j.trace
import sx.rs.attachment
import sx.rs.toStreamingOutput
import sx.rx.limit
import sx.rx.subscribeOn
import sx.time.toTimestamp
import sx.util.letWithParamNotNull
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import javax.inject.Inject
import javax.inject.Named
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.Path
import javax.ws.rs.core.*
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

    @Inject
    private lateinit var executorService: ExecutorService

    /** Timestamp format used for dumps */
    private val timestampFormat by lazy { SimpleDateFormat("yyyyMMddHHmmss") }

    /**
     * Transform observable (sql statement) strings to streaming REST response
     * @param name Base name of response attachment
     */
    private fun Observable<String>.toResponse(name: String = "dump"): Response {
        val filename = "${name}-${timestampFormat.format(Date())}.sql"

        return this
                .subscribeOn(Schedulers.from(executorService).limit(1))
                .toStreamingOutput()
                .let {
                    Response.ok()
                            .attachment(entity = it, filename = filename)
                            .build()
                }
    }

    /**
     * Transform jooq select to streaming REST response
     * @param name Base name of response attachment
     */
    private fun <R : Record> Select<R>.toResponse(name: String = "dump"): Response {
        return this
                .dump()
                .toResponse(name)
    }

    override fun dumpCentralStations(): Response {
        return dsl.selectFrom(TBLDEPOTLISTE)
                .toResponse("tbldepotliste")
    }

    override fun dumpCentralRoutes(): Response {
        return dsl.selectFrom(MST_ROUTE)
                .toResponse("mst_route")
    }

    override fun dumpDeliveryLists(stationNo: Int?, from: ShortDate?, to: ShortDate?): Response {
        val rkIds = dsl.select(RKKOPF.ID)
                .from(RKKOPF)
                .where()
                .letWithParamNotNull(stationNo, {
                    and(RKKOPF.LIEFERDEPOT.eq(it.toDouble()))
                })
                .letWithParamNotNull(from, {
                    and(RKKOPF.ROLLKARTENDATUM.ge(it.date.toTimestamp()))
                })
                .letWithParamNotNull(to, {
                    and(RKKOPF.ROLLKARTENDATUM.le(it.date.toTimestamp()))
                })
                .fetch(RKKOPF.ID)
                .toList()

        val orderIds = dsl.select(RKDETAILS.ORDERID)
                .from(RKDETAILS)
                .where(RKDETAILS.RK_ID.`in`(rkIds))
                .fetch(RKDETAILS.ORDERID)
                .toList()

        return Observable.concat(
                dsl.selectFrom(RKKOPF)
                        .where(RKKOPF.ID.`in`(rkIds))
                        .dump(),

                dsl.selectFrom(RKDETAILS)
                        .where(RKDETAILS.RK_ID.`in`(rkIds))
                        .dump(),

                dsl.selectFrom(TBLAUFTRAG)
                        .where(TBLAUFTRAG.ORDERID.`in`(orderIds))
                        .dump(),

                dsl.selectFrom(TBLAUFTRAGCOLLIES)
                        .where(TBLAUFTRAGCOLLIES.ORDERID.`in`(orderIds))
                        .dump()
        )
                .toResponse("deliverylist")
    }
}