package org.deku.leoz.node.data.repository

import org.deku.leoz.node.data.jpa.TadOrder
import org.deku.leoz.node.data.jpa.TadOrderParcel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import sx.persistence.querydsl.from
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import org.deku.leoz.node.data.jpa.QTadOrder
import org.deku.leoz.node.data.jpa.QTadOrder.tadOrder
import org.deku.leoz.node.data.jpa.QTadOrderParcel
import org.deku.leoz.node.data.jpa.QTadOrderParcel.tadOrderParcel
import sx.util.toNullable
import javax.inject.Inject

interface OrderRepository : JpaRepository<TadOrder, Long>,
        QuerydslPredicateExecutor<TadOrder>, OrderRepositoryExtension

interface OrderRepositoryExtension {
    fun findByOrderIds(ids: List<Long>): List<TadOrder>
    fun findByOrderId(id: Long): TadOrder?
}

interface OrderParcelRepository : JpaRepository<TadOrderParcel, Long>,
        QuerydslPredicateExecutor<TadOrderParcel>, OrderParcelRepositoryExtension

interface OrderParcelRepositoryExtension {
    fun findByScanId(scanId: Long): TadOrderParcel?
    fun findByOrderIds(ids: List<Long>): List<TadOrderParcel>
    fun findByOrderId(id: Long): List<TadOrderParcel>
}

class OrderRepositoryImpl : OrderRepositoryExtension {
    @PersistenceContext
    private lateinit var em: EntityManager

    @Inject
    private lateinit var orderRepository: OrderRepository

    override fun findByOrderIds(ids: List<Long>): List<TadOrder> {
        if (ids.count() == 0)
            return listOf()
        val set = LinkedHashSet(ids.map { it })
        return orderRepository.findAll(
                tadOrder.orderid.`in`(set)
                        .and(tadOrder.isCancelled.isFalse))
                .toList().sortedWith(compareBy { set.indexOf(it.id) })

    }

    override fun findByOrderId(id: Long): TadOrder? {
        return orderRepository.findOne(
                tadOrder.orderid.eq(id)
                        .and(tadOrder.isCancelled.isFalse))
                .toNullable()
    }
}

class OrderParcelRepositoryImpl : OrderParcelRepositoryExtension {
    @PersistenceContext
    private lateinit var em: EntityManager

    @Inject
    private lateinit var orderParcelRepository: OrderParcelRepository

    override fun findByScanId(scanId: Long): TadOrderParcel? {
        return em.from(tadOrderParcel)
                .where(tadOrderParcel.scanId.eq(scanId)
                        .and(tadOrderParcel.isCancelled.isFalse))
                .fetchOne()
    }

    override fun findByOrderIds(ids: List<Long>): List<TadOrderParcel> {
        if (ids.count() == 0)
            return listOf()
        val set = LinkedHashSet(ids.map { it })
        return orderParcelRepository.findAll(
                tadOrderParcel.orderid.`in`(set)
                        .and(tadOrderParcel.isCancelled.isFalse)
        ).toList().sortedWith(compareBy { set.indexOf(it.id) })
    }

    override fun findByOrderId(id: Long): List<TadOrderParcel> {
        return em.from(tadOrderParcel)
                .where(tadOrderParcel.orderid.eq(id)
                        .and(tadOrderParcel.isCancelled.isFalse))
                .fetch()
    }
}
