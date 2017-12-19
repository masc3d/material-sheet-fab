package org.deku.leoz.node.web

import io.swagger.jaxrs.config.SwaggerContextService
import io.undertow.server.handlers.resource.Resource
import io.undertow.server.handlers.resource.ResourceChangeListener
import io.undertow.server.handlers.resource.ResourceManager
import io.undertow.server.handlers.resource.URLResource
import org.deku.leoz.config.Rest
import org.deku.leoz.node.Application
import org.deku.leoz.node.config.MqBrokerConfiguration
import org.deku.leoz.node.rest.swagger.SwaggerBootstrapServlet
import org.jboss.resteasy.core.Dispatcher
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher
import org.jboss.resteasy.plugins.spring.SpringBeanProcessor
import org.jboss.resteasy.spi.Registry
import org.jboss.resteasy.spi.ResteasyDeployment
import org.jboss.resteasy.spi.ResteasyProviderFactory
import org.jboss.resteasy.springmvc.ResteasyHandlerMapping
import org.slf4j.LoggerFactory
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory
import org.springframework.boot.web.servlet.ServletContextInitializer
import org.springframework.context.annotation.Bean
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.support.WebApplicationContextUtils
import sx.mq.jms.activemq.HttpExternalTunnelServlet
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import javax.inject.Inject
import javax.inject.Named
import javax.servlet.ServletContext
import javax.servlet.ServletException

/**
 * Created by masc on 27.05.15.
 */
@Named
class WebContextInitializer : ServletContextInitializer {
    var log = LoggerFactory.getLogger(WebContextInitializer::class.java.name)

    companion object {
        private val STATIC_CONTENT_CLASSPATH = "/webapp"
        private val WELCOME_FILES = arrayOf("index.html")
    }

    @Inject
    private lateinit var application: Application
    @Inject
    private lateinit var resteasyDeployment: ResteasyDeployment
    @Inject
    private lateinit var resteasyHandlerMapping: ResteasyHandlerMapping
    @Inject
    private lateinit var springBeanProcessor: SpringBeanProcessor
    @Inject
    private lateinit var brokerSettings: MqBrokerConfiguration.Settings

    @Throws(ServletException::class)
    override fun onStartup(servletContext: ServletContext) {
        log.info("Leoz webcontext startup")

        // Inject web application context
        this.application.setApplicationContext(WebApplicationContextUtils.getWebApplicationContext(servletContext) as WebApplicationContext)

        //region Setup servlets

        // Spring dispatcher servlet (variant 1)
        // requires the following spring boot autoconfigurations:
        // * HttpMessageConvertersAutoConfiguration.class,
        // * WebMvcAutoConfiguration.class,
        // * DispatcherServletAutoConfiguration.class,

        // Configuring resteasy handler mapping
        //mResteasyHandlerMapping.setPrefix(RestConfiguration.MAPPING_PREFIX);
        //mResteasyHandlerMapping.setThrowNotFound(false);

        // Resteasy dispatcher servlet configuration (variant 2)
        // This setup has the following advantages
        // * faster startup time (~2 seconds) as spring-webmvc/dispatcher not needed
        // * uses dedicated http dispatcher servlet for resteasy (ResteasyHandlerMappng has issues
        //   as it throws errors on any invalid resource, ven for paths outside scope/prefix)
        servletContext.setAttribute(ResteasyProviderFactory::class.java.name, resteasyDeployment.providerFactory)
        servletContext.setAttribute(Dispatcher::class.java.name, resteasyDeployment.dispatcher)
        servletContext.setAttribute(Registry::class.java.name, resteasyDeployment.registry)

        run {
            val sr = servletContext.addServlet(HttpServletDispatcher::class.java.name, HttpServletDispatcher::class.java)
            sr.setInitParameter("resteasy.servlet.mapping.prefix", Rest.MAPPING_PREFIX)
            sr.setInitParameter("javax.ws.rs.Application", "org.deku.leoz.node.rest.WebserviceApplication")
            sr.setInitParameter(SwaggerContextService.USE_PATH_BASED_CONFIG, "true")
            sr.setLoadOnStartup(1)
            sr.addMapping(Rest.MAPPING_PREFIX + "/*")
        }

        run {
            val sr = servletContext.addServlet(SwaggerBootstrapServlet::class.java.name, SwaggerBootstrapServlet::class.java)
            sr.setInitParameter(SwaggerContextService.USE_PATH_BASED_CONFIG, "true")
            sr.setLoadOnStartup(1)
        }

        try {
            run {
                val sr = servletContext.addServlet(HttpExternalTunnelServlet::class.java.name,
                        HttpExternalTunnelServlet(
                                URI("http://localhost:8080/leoz/jms")))
                sr.setLoadOnStartup(1)
                sr.addMapping(this.brokerSettings.httpContextPath!! + "/*")
            }
        } catch (e: URISyntaxException) {
            throw ServletException(e)
        }

        //endregion
    }

    @get:Bean
    val servletContainer: UndertowServletWebServerFactory
        get() {
            // Creating embedded servlet container factory manually
            // as it's currently the only way to customize welcome pages
            // Undertow doesn't have index.html by default.
            val factory = UndertowServletWebServerFactory()
            factory.deploymentInfoCustomizers.add(
                    UndertowDeploymentInfoCustomizer { deploymentInfo ->
                        deploymentInfo.resourceManager = object : ResourceManager {
                            @Throws(IOException::class)
                            override fun getResource(path: String): Resource? {
                                var filePath = path

                                // Check root path
                                var url = WebContextInitializer::class.java.getResource(filePath)
                                if (url == null) {
                                    // Check static cotent path
                                    filePath = STATIC_CONTENT_CLASSPATH + path
                                    url = WebContextInitializer::class.java.getResource(filePath)
                                }

                                if (url == null) {
                                    // Resource not found
                                    return null
                                }

                                val urlResource = object : URLResource(url, filePath) {
                                    override fun isDirectory(): Boolean {
                                        if (this.file != null)
                                            return super.isDirectory()
                                        else
                                            return this.contentLength == 0L
                                    }
                                }
                                return urlResource
                            }

                            override fun isResourceChangeListenerSupported(): Boolean {
                                return false
                            }

                            override fun registerResourceChangeListener(listener: ResourceChangeListener) {
                            }

                            override fun removeResourceChangeListener(listener: ResourceChangeListener) {
                            }

                            @Throws(IOException::class)
                            override fun close() {
                            }
                        }
                        for (wp in WELCOME_FILES)
                            deploymentInfo.addWelcomePage(wp)
                    })
            return factory
        }

    /**
     * Spring webmvc configurer adapter.
     * Only required when using spring-webmvc with dispatcher servlet for eg. configuring static content.
     * IMPORTANT: Please don't remove, this may be needed in the future as more web content/functionality is added.
     */
    //    @Component
    //    public static class WebMvcConfigurer extends WebMvcConfigurerAdapter {
    //
    //        @Override
    //        public void addResourceHandlers(ResourceHandlerRegistry registry) {
    //            registry.addResourceHandler("/**")
    //                    .addResourceLocations("classpath:/webapp/");
    //
    //            super.addResourceHandlers(registry);
    //        }
    //
    //        @Override
    //        public void addViewControllers(ViewControllerRegistry registry) {
    //            registry.addViewController("/").setViewName("forward:/index.html");
    //            registry.addViewController("/rs/").setViewName("forward:/rs/index.html");
    //            registry.addViewController("/rs/internal/").setViewName("forward:/rs/internal/index.html");
    //
    //            registry.addViewController("/rs").setViewName("redirect:/rs/");
    //            registry.addViewController("/rs/internal").setViewName("redirect:/rs/internal/");
    //        }
    //    }
}