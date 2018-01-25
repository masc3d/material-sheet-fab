package org.deku.leoz.node.service.internal

import io.reactivex.Observable
import org.deku.leoz.smartlane.model.Address
import org.deku.leoz.smartlane.model.Routinginput
import org.deku.leoz.smartlane.model.toRouteDeliveryInput
import org.junit.Test
import org.slf4j.LoggerFactory
import sx.Stopwatch
import sx.log.slf4j.trace
import sx.time.plusHours
import java.util.*

/**
 * Created by masc on 20.11.17.
 */
class SmartlaneBridgeTest {
    private val log = LoggerFactory.getLogger(this.javaClass)


    private val bridge by lazy {
        SmartlaneBridge()
    }

    private val addresses = listOf(
            Address().also {
                it.contactfirstname = "Jürgen"
                it.contactlastname = "Töpper"
                it.contactcompany = "Test"
                it.street = "Waldhof"
                it.housenumber = "1"
                it.city = "Schaafheim"
                it.postalcode = "64850"
            },
            Address().also {
                it.contactcompany = "DERKURIER"
                it.street = "Dörrwiese"
                it.housenumber = "2"
                it.city = "Neuenstein"
                it.postalcode = "36286"
            }
    )

//    @Test
//    fun testRoute() {
//        Stopwatch.createStarted(this, "calculateRoute", {
//            bridge.optimize(
//                    routingInput = Routinginput().also {
//                        it.deliverydata = this.addresses.mapIndexed { index, address ->
//                            address.toRouteDeliveryInput(
//                                    customId = "DEKU_ADDRESS ${index}").also {
//                                it.pdtFrom = Date()
//                                it.pdtTo = Date().plusHours(4)
//                            }
//                        }
//                    }
//            )
//                    .blockingFirst()
//                    .also {
//                        log.trace { it }
//                    }
//        })
//    }
//
//    @Test
//    fun testRouteParallel() {
//        Stopwatch.createStarted(this, "calculateRoute", {
//            Observable.merge(
//                    (0..30).map {
//                        bridge.optimizeRoute(
//                                routingInput = Routinginput().also {
//                                    it.deliverydata = this.addresses.mapIndexed { index, address ->
//                                        address.toRouteDeliveryInput(
//                                                customId = "DEKU_ADDRESS ${index}")
//                                    }
//                                }
//                        )
//                    }
//            )
//                    .blockingSubscribe {
//                        log.trace("Processed ${it.map { it.id }}")
//                    }
//        })
//    }
}