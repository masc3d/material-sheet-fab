package org.deku.leo2.node.web;

import io.undertow.server.handlers.resource.*;
import io.undertow.servlet.api.DeploymentInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by masc on 27.05.15.
 */
@Named
public class WebContextInitializer implements ServletContextInitializer {
    Log mLog = LogFactory.getLog(WebContextInitializer.class.getName());

    @Inject
    ResteasyDeployment mResteasyDeployment;

    @Inject
    ResteasyHandlerMapping mResteasyHandlerMapping;

    @Inject
    SpringBeanProcessor mSpringBeanProcessor;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        mLog.info("Leo2 webcontext startup");

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

        ServletRegistration.Dynamic sr = servletContext.addServlet("rs", HttpServletDispatcher.class);
        sr.setInitParameter("resteasy.servlet.mapping.prefix", "/rs/api");
        sr.setInitParameter("javax.ws.rs.Application", "org.deku.leo2.node.rest.WebserviceApplication");
        sr.setLoadOnStartup(1);
        sr.addMapping("/rs/api/*");
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
                                String basePath = "/webapp";
                                path = basePath + path;
                                URL url = WebContextInitializer.class.getResource(path);
                                return new URLResource(url, url.openConnection(), path);
                            }

                            @Override
                            public boolean isResourceChangeListenerSupported() {
                                return false;
                            }

                            @Override
                            public void registerResourceChangeListener(ResourceChangeListener listener) {

                            }

                            @Override
                            public void removeResourceChangeListener(ResourceChangeListener listener) {

                            }

                            @Override
                            public void close() throws IOException {

                            }
                        });
                        deploymentInfo.addWelcomePage("index.html");
                    }
                }
        );
        return factory;
    }

    /**
     * Spring webmvc configurer adapter.
     * Only required when using spring-webmvc with dispatcher servlet for eg. configuring static content
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