package org.deku.leoz.node.rest

import org.jboss.resteasy.plugins.interceptors.CorsFilter
import org.slf4j.LoggerFactory

import javax.ws.rs.core.Application
import java.util.HashSet

/**
 * Created by masc on 12.05.15.
 */
class WebserviceApplication : Application() {
    private val log = LoggerFactory.getLogger(WebserviceApplication::class.java)
    private val singletons = HashSet<Any>()
    private val classes = HashSet<Class<*>>()

    init {
        log.info("Leoz node jax/ws/rs webservice application")
        val corsFilter = CorsFilter()
        corsFilter.allowedOrigins.add("*")
        corsFilter.allowedMethods = "OPTIONS, GET, POST, DELETE, PUT, PATCH"
        singletons.add(corsFilter)
    }

    override fun getSingletons(): Set<Any> {
        return singletons
    }

    override fun getClasses(): Set<Class<*>> {
        return classes
    }
}
