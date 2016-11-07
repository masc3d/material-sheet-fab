package org.deku.leoz.node.web

import io.undertow.server.handlers.resource.Resource
import io.undertow.server.handlers.resource.ResourceChangeListener
import io.undertow.server.handlers.resource.ResourceManager
import io.undertow.server.handlers.resource.URLResource
import org.deku.leoz.node.App
import org.deku.leoz.node.config.MessageBrokerConfiguration
import org.jboss.resteasy.core.Dispatcher
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher
import org.jboss.resteasy.plugins.spring.SpringBeanProcessor
import org.jboss.resteasy.spi.Registry
import org.jboss.resteasy.spi.ResteasyDeployment
import org.jboss.resteasy.spi.ResteasyProviderFactory
import org.jboss.resteasy.springmvc.ResteasyHandlerMapping
import org.slf4j.LoggerFactory
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory
import org.springframework.boot.web.servlet.ServletContextInitializer
import org.springframework.boot.context.embedded.undertow.UndertowDeploymentInfoCustomizer
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory
import org.springframework.context.annotation.Bean
import org.springframework.web.context.support.WebApplicationContextUtils
import sx.jms.activemq.HttpExternalTunnelServlet
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
    var mLog = LoggerFactory.getLogger(WebContextInitializer::class.java.name)

    companion object {
        val RESTEASY_MAPPING_PATH = "/rs/api"
        private val STATIC_CONTENT_CLASSPATH = "/webapp"
        private val WELCOME_FILES = arrayOf("index.html")
    }

    @Inject
    private lateinit var mResteasyDeployment: ResteasyDeployment

    @Inject
    private lateinit var mResteasyHandlerMapping: ResteasyHandlerMapping

    @Inject
    private lateinit var mSpringBeanProcessor: SpringBeanProcessor

    @Inject
    private lateinit var mBrokerSettings: MessageBrokerConfiguration.Settings

    @Throws(ServletException::class)
    override fun onStartup(servletContext: ServletContext) {
        mLog.info("Leoz webcontext startup")

        // Inject web application context
        App.instance.setApplicationContext(WebApplicationContextUtils.getWebApplicationContext(servletContext))

        //region Setup servlets

        // Spring dispatcher servlet (variant 1)
        // requires the following spring boot autoconfigurations:
        // * HttpMessageConvertersAutoConfiguration.class,
        // * WebMvcAutoConfiguration.class,
        // * DispatcherServletAutoConfiguration.class,

        // Configuring resteasy handler mapping
        //mResteasyHandlerMapping.setPrefix("/rs/api");
        //mResteasyHandlerMapping.setThrowNotFound(false);

        // Resteasy dispatcher servlet configuration (variant 2)
        // This setup has the following advantages
        // * faster startup time (~2 seconds) as spring-webmvc/dispatcher not needed
        // * uses dedicated http dispatcher servlet for resteasy (ResteasyHandlerMappng has issues
        //   as it throws errors on any invalid resource, ven for paths outside scope/prefix)
        servletContext.setAttribute(ResteasyProviderFactory::class.java.name, mResteasyDeployment.providerFactory)
        servletContext.setAttribute(Dispatcher::class.java.name, mResteasyDeployment.dispatcher)
        servletContext.setAttribute(Registry::class.java.name, mResteasyDeployment.registry)

        var sr = servletContext.addServlet(HttpServletDispatcher::class.java.name, HttpServletDispatcher::class.java)
        sr.setInitParameter("resteasy.servlet.mapping.prefix", RESTEASY_MAPPING_PATH)
        sr.setInitParameter("javax.ws.rs.Application", "org.deku.leoz.node.rest.WebserviceApplication")
        sr.setLoadOnStartup(1)
        sr.addMapping(RESTEASY_MAPPING_PATH + "/*")

        try {
            sr = servletContext.addServlet(HttpExternalTunnelServlet::class.java.name,
                    HttpExternalTunnelServlet(
                            URI("http://localhost:8080/leoz/jms")))
            sr.setLoadOnStartup(1)
            sr.addMapping(this.mBrokerSettings.httpContextPath!! + "/*")
        } catch (e: URISyntaxException) {
            throw ServletException(e)
        }

        //endregion
    }

    @Bean
    fun servletContainer(): EmbeddedServletContainerFactory {
        // Creating embedded servlet container factory manually
        // as it's currently the only way to customize welcome pages
        // Undertow doesn't have index.html by default.
        val factory = UndertowEmbeddedServletContainerFactory()
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

                            val urlResource = object : URLResource(url, url.openConnection(), filePath) {
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