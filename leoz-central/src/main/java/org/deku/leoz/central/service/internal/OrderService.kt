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
@ApiKey(true)
@Path("internal/v1/order")
class OrderService : OrderService {
    override fun getById(id: String): OrderService.Order {
        val O = readOrder(id)
        if (O == null)
            throw DefaultProblem(
                    title = "Order not found",
                    status = Response.Status.NOT_FOUND)
        val R: OrderService.Order = O
        return R
    }

    override fun get(labelRef: String?, custRef: String?, ref: String?): List<OrderService.Order> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

//    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext
    @Inject
    private lateinit var orderRepository: OrderJooqRepository


    fun readOrder(Id: String): OrderService.Order? {
        var O: OrderService.Order? = null
        O = orderRepository.findByID(Id = Id)

        return O
    }

}