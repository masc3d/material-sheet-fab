package org.deku.leo2.node.web;

import org.deku.leo2.node.Global;
import org.deku.leo2.node.Main;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.plugins.spring.SpringBeanProcessor;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.springmvc.ResteasyHandlerMapping;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.logging.Logger;

/**
 * Created by masc on 27.05.15.
 */
@Configuration
public class WebContext implements ServletContextInitializer {
    Logger mLog = Logger.getLogger(WebContext.class.getName());

    @Inject
    ResteasyDeployment mResteasyDeployment;

    @Inject
    ResteasyHandlerMapping mResteasyHandlerMapping;

    @Inject
    SpringBeanProcessor mSpringBeanProcessor;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        mLog.info("Leo2 webcontext startup");

        try {
            Global.instance().initialize();

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
            //   as it throws errors on any invalid resource and it's queries even for paths outside prefix
            servletContext.setAttribute(ResteasyProviderFactory.class.getName(), mResteasyDeployment.getProviderFactory());
            servletContext.setAttribute(Dispatcher.class.getName(), mResteasyDeployment.getDispatcher());
            servletContext.setAttribute(Registry.class.getName(), mResteasyDeployment.getRegistry());

            ServletRegistration.Dynamic sr = servletContext.addServlet("rs", HttpServletDispatcher.class);
            sr.setInitParameter("resteasy.servlet.mapping.prefix", "/rs/api");
            sr.setInitParameter("javax.ws.rs.Application", "org.deku.leo2.node.rest.WebserviceApplication");
            sr.setLoadOnStartup(1);
            sr.addMapping("/rs/api/*");
        } catch (Exception e) {
            throw new ServletException(e);
        }
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