package org.deku.leoz.node.web;

import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.server.handlers.resource.URLResource;
import io.undertow.servlet.api.DeploymentInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leoz.node.App;
import org.deku.leoz.node.config.MessageBrokerConfiguration;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.plugins.spring.SpringBeanProcessor;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.springmvc.ResteasyHandlerMapping;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.boot.context.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.support.WebApplicationContextUtils;
import sx.jms.embedded.activemq.HttpExternalTunnelServlet;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by masc on 27.05.15.
 */
@Named
class WebContextInitializer implements ServletContextInitializer {
    Log mLog = LogFactory.getLog(WebContextInitializer.class.getName());

    private static final String STATIC_CONTENT_CLASSPATH = "/webapp";
    private static final String RESTEASY_MAPPING_PATH = "/rs/api";
    private static final String[] WELCOME_FILES = new String[] { "index.html" };

    @Inject
    ResteasyDeployment mResteasyDeployment;

    @Inject
    ResteasyHandlerMapping mResteasyHandlerMapping;

    @Inject
    SpringBeanProcessor mSpringBeanProcessor;

    @Inject
    MessageBrokerConfiguration mMessageBrokerConfiguration;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        mLog.info("Leoz webcontext startup");

        // Inject web application context
        App.getInstance().setApplicationContext(WebApplicationContextUtils.getWebApplicationContext(servletContext));

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
        servletContext.setAttribute(ResteasyProviderFactory.class.getName(), mResteasyDeployment.getProviderFactory());
        servletContext.setAttribute(Dispatcher.class.getName(), mResteasyDeployment.getDispatcher());
        servletContext.setAttribute(Registry.class.getName(), mResteasyDeployment.getRegistry());

        ServletRegistration.Dynamic sr = servletContext.addServlet(HttpServletDispatcher.class.getName(), HttpServletDispatcher.class);
        sr.setInitParameter("resteasy.servlet.mapping.prefix", RESTEASY_MAPPING_PATH);
        sr.setInitParameter("javax.ws.rs.Application", "org.deku.leoz.node.rest.WebserviceApplication");
        sr.setLoadOnStartup(1);
        sr.addMapping(RESTEASY_MAPPING_PATH + "/*");

        try {
            sr = servletContext.addServlet(HttpExternalTunnelServlet.class.getName(),
                    new HttpExternalTunnelServlet(
                            new URI("http://localhost:8080/leoz/jms")));
            sr.setLoadOnStartup(1);
            sr.addMapping(this.mMessageBrokerConfiguration.getHttpContextPath() + "/*");
        } catch (URISyntaxException e) {
            throw new ServletException(e);
        }
        //endregion
    }

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        // Creating embedded servlet container factory manually
        // as it's currently the only way to customize welcome pages
        // Undertow doesn't have index.html by default.
        UndertowEmbeddedServletContainerFactory factory = new UndertowEmbeddedServletContainerFactory();
        factory.getDeploymentInfoCustomizers().add(
                new UndertowDeploymentInfoCustomizer() {
                    @Override
                    public void customize(DeploymentInfo deploymentInfo) {
                        deploymentInfo.setResourceManager(new ResourceManager() {
                            @Override
                            public Resource getResource(String path) throws IOException {
                                String filePath = STATIC_CONTENT_CLASSPATH + path;
                                URL url = WebContextInitializer.class.getResource(filePath);
                                if (url == null)
                                    return null;
                                URLResource urlResource = new URLResource(url, url.openConnection(), filePath) {
                                    @Override
                                    public boolean isDirectory() {
                                        if (this.getFile() != null)
                                            return super.isDirectory();
                                        else
                                            return (this.getContentLength() == 0);
                                    }
                                };
                                return urlResource;
                            }

                            @Override
                            public boolean isResourceChangeListenerSupported() { return false; }

                            @Override
                            public void registerResourceChangeListener(ResourceChangeListener listener) { }

                            @Override
                            public void removeResourceChangeListener(ResourceChangeListener listener) { }

                            @Override
                            public void close() throws IOException { }
                        });
                        for (String wp : WELCOME_FILES)
                            deploymentInfo.addWelcomePage(wp);
                    }
                }
        );
        return factory;
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