package org.deku.leoz.node.web

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.context.ConfigurableWebApplicationContext
import sx.log.slf4j.trace
import javax.inject.Inject
import javax.servlet.ServletContextEvent

/**
 * Created by masc on 17.09.14.
 */
@Component("node.ServletContextListener")
class ServletContextListener : javax.servlet.ServletContextListener {
    private var log = LoggerFactory.getLogger(ServletContextListener::class.java)

    @Inject
    private lateinit var context: ConfigurableWebApplicationContext

    override fun contextInitialized(sce: ServletContextEvent) {
        log.info("Leoz servlet context initalized")

        // Log all bean/names in the context
        val clbf = context.beanFactory

        log.trace { "Registered beans: ${context.beanDefinitionCount}" }

        context.beanDefinitionNames.forEach { beanName ->
            val mbd = clbf.getMergedBeanDefinition(beanName)
            val singleton = clbf.getSingleton(beanName)
            log.trace { "${beanName}: ${singleton?.javaClass?.name ?: "<null>"}, lazy ${mbd.isLazyInit}" }
        }
    }

    override fun contextDestroyed(sce: ServletContextEvent) {
        log.info("Leoz servlet Context destroyed")
    }
}
