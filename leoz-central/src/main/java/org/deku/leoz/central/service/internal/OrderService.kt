package org.deku.leoz.central.service.internal

import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.repository.OrderJooqRepository
import org.deku.leoz.central.data.repository.UserJooqRepository
import org.deku.leoz.central.data.repository.isActive
import org.deku.leoz.node.rest.DefaultProblem
import org.deku.leoz.service.internal.OrderService
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import sx.rs.auth.ApiKey
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.core.Response
import java.util.*

/**
 * Created by JT on 30.06.17.
 */
@Named
@ApiKey(false)
@Path("internal/v1/order")
class OrderService : OrderService {

    override fun get(labelRef: String?, custRef: String?, parcelScan: String?): OrderService.Order {
        val O = readOrder(parcelScan)
        if (O == null)
            throw DefaultProblem(
                    title = "Order not found",
                    status = Response.Status.NOT_FOUND)
        return O
    }

    override fun getById(id: Long): OrderService.Order {
        val O = readOrder(id)
        if (O == null)
            throw DefaultProblem(
                    title = "Order not found",
                    status = Response.Status.NOT_FOUND)
        return O
    }

//    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext
    @Inject
    private lateinit var orderRepository: OrderJooqRepository

    fun readOrder(id: Long): OrderService.Order? {
        return  orderRepository.findByID(id = id)
    }

    fun readOrder(parcelScan: String?): OrderService.Order? {
        return orderRepository.findByScan(ScanId = parcelScan)
    }

}