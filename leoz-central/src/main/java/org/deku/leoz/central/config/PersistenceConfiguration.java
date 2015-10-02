package org.deku.leoz.central.config;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Central database persistence context
 * Created by masc on 28.08.14.
 */
@Configuration(PersistenceConfiguration.DB_CENTRAL)
@ComponentScan(lazyInit = true,
        basePackageClasses = { org.deku.leoz.central.data.Package.class },
        // Exclude configuration from component scan (for test run efficiency)
        excludeFilters =
                { @ComponentScan.Filter(value = { Configuration.class } )}
)
@Import(org.deku.leoz.node.config.PersistenceConfiguration.class)
@EnableConfigurationProperties
@EnableTransactionManagement(mode = AdviceMode.PROXY, proxyTargetClass = true)
public class PersistenceConfiguration {
    public static final String DB_CENTRAL = "db_central";

    private Log mLog = LogFactory.getLog(PersistenceConfiguration.class);

    // TODO: tomcat deployment breaks with circular dependency when wither dataSourceCentral()
    // or jooqTransactionAwareDataSourceProxy() are not @Lazy.
    // This works perfectly fine when running standalone.
    @Bean
    @Qualifier(DB_CENTRAL)
    @ConfigurationProperties(prefix="datasource.central")
    public AbstractDataSource dataSourceCentral() {
        DriverManagerDataSource dataSource = (DriverManagerDataSource)DataSourceBuilder
                .create()
                // When running within tomcat, spring can't figure out the driver type (even though mysql is
                // part of jdbc url)
                .driverClassName("com.mysql.jdbc.Driver")
                .type(DriverManagerDataSource.class)
                .build();

        // TODO: figure out how to get those into application.properties
        Properties dataSourceProperties = new Properties();
        dataSourceProperties.setProperty("zeroDateTimeBehavior", "convertToNull");
        dataSourceProperties.setProperty("connectTimeout", "1000");
        dataSource.setConnectionProperties(dataSourceProperties);

        return dataSource;
    }

    @Inject
    private TransactionAwareDataSourceProxy mJooqTransactionAwareDataSource;

    @Inject
    private DataSourceConnectionProvider mJooqConnectionProvider;

    // TODO: tomcat breakage without @Lazy. see above (dataSourceCentral())
    @Bean
    public TransactionAwareDataSourceProxy jooqTransactionAwareDataSourceProxy() {
        return new TransactionAwareDataSourceProxy(dataSourceCentral());
    }

    @Bean
    @Qualifier(DB_CENTRAL)
    public DataSourceTransactionManager jooqTransactionManager() {
        return new DataSourceTransactionManager(dataSourceCentral());
    }

    @Bean
    public DataSourceConnectionProvider jooqConnectionProvider() {
        return new DataSourceConnectionProvider(mJooqTransactionAwareDataSource);
    }

    @Bean
    public DefaultDSLContext dslContext() {
        return new DefaultDSLContext(mJooqConnectionProvider, SQLDialect.MYSQL);
    }

    @PostConstruct
    public void onInitialize() {
        // Disable JOOQ logo
        System.setProperty("org.jooq.no-logo", "true");
    }

    @PreDestroy
    public void onDestroy() throws Exception {
        mLog.info("Cleaning up persistence context");

        // Close all JDBC drivers
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        Driver d = null;
        while (drivers.hasMoreElements()) {
            try {
                d = drivers.nextElement();
                DriverManager.deregisterDriver(d);
                mLog.info(String.format("Driver %s deregistered", d));
            } catch (SQLException ex) {
                mLog.error(String.format("Error deregistering driver %s", d), ex);
            }
        }

        // Close mysql connection cleanup thread
        try {
            AbandonedConnectionCleanupThread.shutdown();
        } catch (InterruptedException e) {
            mLog.error(e.getMessage(), e);
        }
    }

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

