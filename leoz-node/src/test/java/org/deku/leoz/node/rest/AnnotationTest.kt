package org.deku.leoz.node.rest

import org.deku.leoz.rest.RestrictRoles
import org.deku.leoz.rest.RestrictStation
import org.deku.leoz.service.entity.ShortDate
import org.deku.leoz.service.internal.DumpService
import org.deku.leoz.service.internal.StationService
import org.deku.leoz.service.internal.entity.Station
import org.deku.leoz.service.internal.entity.StationV2
import org.junit.Test
import org.junit.experimental.categories.Category
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.util.ReflectionUtils
import sx.junit.StandardTest
import sx.log.slf4j.trace
import sx.reflect.*
import javax.ws.rs.core.Response


/**
 * Created by masc on 08.03.18.
 */
@Category(StandardTest::class)
class AnnotationTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    class StationServiceImpl : StationService {
        override fun get(): Array<Station> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun find(query: String): Array<Station> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getByStationNo(stationNo: Int): StationV2 {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getByDebitorId(debitorId: Int): Array<StationV2> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    class DumpServiceImpl : DumpService {
        override fun dumpCentralStations(): Response {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun dumpCentralRoutes(): Response {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun dumpDeliveryLists(stationNo: Int?, from: ShortDate?, to: ShortDate?): Response {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    @Test
    fun testInterfaceMethodAnnotation() {
        val type = StationService::class.java

        log.trace { "${type}" }
        type.methods.forEach {
            log.trace {
                "${it.name} :: ${it.annotations.map { it.toString() }.joinToString(", ")}"
            }
        }
    }

    @Test
    fun testInterfaceMethodAnnotationWithSpring() {
        val type = DumpService::class.java

        log.trace {
            AnnotationUtils.findAnnotation(type, RestrictRoles::class.java)
        }
    }

    @Test
    fun testInterfaceParameterAnnotation() {
        val type = StationService::class.java

        log.trace { "${type}" }
        type.methods.forEach {
            log.trace {
                "${it.name} :: ${it.parameters.map { it.toString() }.joinToString(", ")}"
            }

            log.trace {
                it.parameters.map { it.annotations.map { it.toString() } }
            }

            log.trace {
                it.parameters.map { it.isAnnotationPresent(RestrictStation::class.java) }
            }
        }
    }

    @Test
    fun testParameterAnnotation() {
        // Implementing type
        val type = StationServiceImpl::class.java

        // Interface types
        val interfaces = type.allInterfaces

        log.trace { "${type}" }
        type.methods.forEach {
            // Find method in interfaces
            interfaces.mapNotNull { c ->
                ReflectionUtils.findMethod(c, it.name, *it.parameterTypes)
            }
                    .forEach {
                        log.trace {
                            "${it.name} :: ${it.parameters.map { it.toString() }.joinToString(", ")}"
                        }

                        log.trace {
                            it.parameters.map { it.annotations.map { it.toString() } }
                        }

                        log.trace {
                            it.parameters.map { it.isAnnotationPresent(RestrictStation::class.java) }
                        }
                    }

        }
    }

    @Test
    fun testMethodAnnotationWithSpring() {
        val type = DumpServiceImpl::class.java

        log.trace {
            AnnotationUtils.findAnnotation(type, RestrictRoles::class.java)
        }
    }

    @Test
    fun testParameterAnnotationWithSpring() {
        val type = StationServiceImpl::class.java

        log.trace { "${type}" }

        // Interface types
        val interfaces = type.allInterfaces

        type.methods.forEach {
            // Find method in interfaces
            interfaces.mapNotNull { c ->
                ReflectionUtils.findMethod(c, it.name, *it.parameterTypes)
            }
                    .forEach {
                        log.trace { it }
                        it.parameters.forEach {
                            log.trace {
                                "annotation -> ${AnnotationUtils.findAnnotation(it, RestrictStation::class.java)}"
                            }
                        }
                    }

        }
    }
}