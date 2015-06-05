package org.deku.leo2.node.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.node.App;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.util.Properties;

/**
 * Embedded database persistence context
 */
@Configuration(PersistenceContext.DB_EMBEDDED)
@ComponentScan(lazyInit = true)
@EnableTransactionManagement(mode = AdviceMode.PROXY, proxyTargetClass = true)
@EnableJpaRepositories(considerNestedRepositories = false)
public class PersistenceContext implements DisposableBean /*, TransactionManagementConfigurer*/ {
    public static final String DB_EMBEDDED = "db_embedded";
    private Log mLog = LogFactory.getLog(PersistenceContext.class.getName());

    private boolean mShowSql = false;

    @Inject
    @Qualifier(DB_EMBEDDED)
    private AbstractDataSource mDataSource;

    @Bean
    @Lazy
    @Qualifier(DB_EMBEDDED)
    public AbstractDataSource dataSource() {
        final boolean IN_MEMORY = false;

        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        File dbPath = new File(App.instance().getLocalHomeDirectory(), "db/leo2");

        dataSource.setDriverClassName("org.h2.Driver");
        if (!IN_MEMORY) {
            dataSource.setUrl("jdbc:h2:file:" + dbPath.toString());
        } else {
            dataSource.setUrl("jdbc:h2:mem:db1");
        }

        Properties dataSourceProperties = new Properties();
        dataSourceProperties.setProperty("zeroDateTimeBehavior", "convertToNull");
        dataSourceProperties.setProperty("connectTimeout", "1000");
        // For in memory db
        if (IN_MEMORY) {
            dataSourceProperties.setProperty("INIT", "CREATE SCHEMA IF NOT EXISTS leo2");
            dataSourceProperties.setProperty("DB_CLOSE_DELAY", "-1");
        }

        dataSource.setConnectionProperties(dataSourceProperties);

        return dataSource;
    }

    //region JPA
    @Lazy
    @Bean
    @Qualifier(DB_EMBEDDED)
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        transactionManager.setDataSource(dataSource());

        return transactionManager;
    }

    @Lazy
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        // TODO. more robust behaviour when database is down.
        // eg. webservice fails when database is unreachable (on startup eg.)
        // more tests required referring to db outages during runtime
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("org.deku.leo2.node.data.entities");

        JpaVendorAdapter vendorAdapter = new EclipseLinkJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Properties eclipseLinkProperties = new Properties();
        eclipseLinkProperties.setProperty("javax.persistence.schema-generation.database.action", "create");
        eclipseLinkProperties.setProperty("javax.persistence.schema-generation.create-database-schemas", "true");
        eclipseLinkProperties.setProperty("eclipselink.allow-zero-id", "true");
        eclipseLinkProperties.setProperty("eclipselink.weaving", "false");
        eclipseLinkProperties.setProperty("eclipselink.target-database", "org.eclipse.persistence.platform.database.H2Platform");
        eclipseLinkProperties.setProperty("eclipselink.jdbc.batch-writing", "jdbc");


        if (mShowSql) {
            // Show SQL
            eclipseLinkProperties.setProperty("eclipselink.logging.level.sql", "FINE");
            eclipseLinkProperties.setProperty("eclipselink.logging.parameters", "true");
        }

        em.setJpaProperties(eclipseLinkProperties);

        return em;
    }
    //endregion

    @Override
    public void destroy() throws Exception {

    }

//        @Override
//        public PlatformTransactionManager annotationDrivenTransactionManager() {
//            return transactionManger(entityManagerFactory().getObject());
//        }

//        @Override
//        public PlatformTransactionManager annotationDrivenTransactionManager() {
//            return null;
//        }

//    @Aspect
//    public class DAOInterceptor {
//        private Logger log = Logger.getLog(DAOInterceptor.class.getName());
//
//        @Around("execution(* com.webforefront.jpa.service..*.*(..))")
//        public Object logQueryTimes(ProceedingJoinPoint pjp) throws Throwable {
//            StopWatch stopWatch = new StopWatch();
//            stopWatch.start();
//            Object retVal = pjp.proceed();
//            stopWatch.stop();
//            String str = pjp.getTarget().toString();
//            log.info(str.substring(str.lastIndexOf(".")+1, str.lastIndexOf("@")) + " - " + pjp.getSignature().getName() + ": " + stopWatch.getTime() + "ms");
//            return retVal;
//        }
//    }
}

