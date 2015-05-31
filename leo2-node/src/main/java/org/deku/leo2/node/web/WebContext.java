package org.deku.leo2.node.web;

import org.deku.leo2.node.MainSpringBoot;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap;
import org.jboss.resteasy.plugins.spring.SpringBeanProcessor;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.springmvc.ResteasyHandlerMapping;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.resteasy.autoconfigure.ResteasyAutoConfiguration;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.io.File;
import java.util.Map;

/**
 * Created by masc on 27.05.15.
 */
@Profile(MainSpringBoot.SPRING_PROFILE_BOOT)
@Configuration
public class WebContext implements ServletContextInitializer {

    @Inject
    ResteasyDeployment mResteasyDeployment;

    @Inject
    ResteasyHandlerMapping mResteasyHandlerMapping;

    @Inject
    SpringBeanProcessor mSpringBeanProcessor;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        System.err.println("------------------------------------");

        // Spring dispatcher servlet (variant 1)
        // Configuring resteasy handler mapping
        mResteasyHandlerMapping.setPrefix("/rs/api");
        //mResteasyHandlerMapping.setOrder(Integer.MAX_VALUE);
        mResteasyHandlerMapping.setThrowNotFound(false);

        // Resteasy dispatcher servlet configuration (variant 2)
//        servletContext.setAttribute(ResteasyProviderFactory.class.getName(), mResteasyDeployment.getProviderFactory());
//        servletContext.setAttribute(Dispatcher.class.getName(), mResteasyDeployment.getDispatcher());
//        servletContext.setAttribute(Registry.class.getName(), mResteasyDeployment.getRegistry());
//
//        ServletRegistration.Dynamic sr = servletContext.addServlet("rs", HttpServletDispatcher.class);
//        sr.setInitParameter("resteasy.servlet.mapping.prefix", "/rs/api");
//        sr.setInitParameter("javax.ws.rs.Application", "org.deku.leo2.node.rest.WebserviceApplication");
//        sr.setLoadOnStartup(1);
//        sr.addMapping("/rs/api/*");
    }

    @Component
    public static class WebMvcConfigurer extends WebMvcConfigurerAdapter {

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/**")
                    .addResourceLocations("classpath:/webapp/");

            super.addResourceHandlers(registry);
        }

        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/").setViewName("forward:/index.html");
            registry.addViewController("/rs/").setViewName("forward:/rs/index.html");
            registry.addViewController("/rs/internal/").setViewName("forward:/rs/internal/index.html");

            registry.addViewController("/rs").setViewName("redirect:/rs/");
            registry.addViewController("/rs/internal").setViewName("redirect:/rs/internal/");
        }


    }

    @Component
    public static class CustomizationBean implements EmbeddedServletContainerCustomizer {

        @Override
        public void customize(ConfigurableEmbeddedServletContainer container) {
            container.setPort(8080);
            container.setContextPath("/leo2");
        }
    }
}