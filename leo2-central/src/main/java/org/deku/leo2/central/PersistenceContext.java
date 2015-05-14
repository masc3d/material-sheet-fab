package org.deku.leo2.central;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by masc on 28.08.14.
 */
@Configuration
@EnableJpaRepositories
@EnableTransactionManagement
public class PersistenceContext implements DisposableBean {
    private Logger mLog = Logger.getLogger(PersistenceContext.class.getName());

    private boolean mShowSql = false;

    //region Spring inline persistence/unit configuration

    @Inject
    @Qualifier("dekuclient")
    private AbstractDataSource mDataSourceDekuclient;

    @Inject
    @Qualifier("leo2factory")
    private AbstractDataSource mDataSourceLeo2factory;

    @Bean
    @Lazy
    @Qualifier("dekuclient")
    public AbstractDataSource dataSourceDekuclient() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://10.0.10.10:3306/dekuclient");
        dataSource.setUsername("leo2");
        dataSource.setPassword("leo2");

        Properties dataSourceProperties = new Properties();
        dataSourceProperties.setProperty("zeroDateTimeBehavior", "convertToNull");
        dataSourceProperties.setProperty("connectTimeout", "1000");
        dataSource.setConnectionProperties(dataSourceProperties);

        return dataSource;
    }

    @Bean
    @Lazy
    @Qualifier("leo2factory")
    public AbstractDataSource dataSourceLeo2factory() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://10.0.10.10:3306/dekuclient");
        dataSource.setUsername("leo2");
        dataSource.setPassword("leo2");

        Properties dataSourceProperties = new Properties();
        dataSourceProperties.setProperty("zeroDateTimeBehavior", "convertToNull");
        dataSourceProperties.setProperty("connectTimeout", "1000");
        dataSource.setConnectionProperties(dataSourceProperties);

        return dataSource;
    }

    //region JPA
    @Bean
    @Qualifier("jpa")
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        transactionManager.setDataSource(mDataSourceLeo2factory);

        return transactionManager;
    }

    @Lazy
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        // TODO. more robust behaviour when database is down.
        // eg. webservice fails when database is unreachable (on startup eg.)
        // more tests required referring to db outages during runtime
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(mDataSourceLeo2factory);
        em.setPackagesToScan(new String[]{
                "org.deku.leo2.central.data.entities"
        });

        JpaVendorAdapter vendorAdapter = new EclipseLinkJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Properties eclipseLinkProperties = new Properties();
        eclipseLinkProperties.setProperty("eclipselink.allow-zero-id", "true");
        eclipseLinkProperties.setProperty("eclipselink.weaving", "false");
        if (mShowSql) {
          // Show SQL
          eclipseLinkProperties.setProperty("eclipselink.logging.level.sql", "FINE");
          eclipseLinkProperties.setProperty("eclipselink.logging.parameters", "true");
        }

        em.setJpaProperties(eclipseLinkProperties);

        return em;
    }
    //endregion

    //region JOOQ
    @Inject
    private TransactionAwareDataSourceProxy mJooqTransactionAwareDataSource;

    @Inject
    private DataSourceConnectionProvider mJooqConnectionProvider;

    @Bean
    public TransactionAwareDataSourceProxy jooqTransactionAwareDataSourceProxy() {
        return new TransactionAwareDataSourceProxy(mDataSourceDekuclient);
    }

    @Bean
    @Qualifier("jooq")
    public DataSourceTransactionManager jooqTransactionManager() {
        return new DataSourceTransactionManager(mDataSourceDekuclient);
    }

    @Bean
    public DataSourceConnectionProvider jooqConnectionProvider() {
        return new DataSourceConnectionProvider(mJooqTransactionAwareDataSource);
    }

    @Bean
    public DefaultDSLContext dslContext() {
        return new DefaultDSLContext(mJooqConnectionProvider, SQLDialect.MYSQL);
    }
    //endregion

    @Override
    public void destroy() throws Exception {
        mLog.info("Cleaning up persistence context");

        // Close all JDBC drivers
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        Driver d = null;
        while (drivers.hasMoreElements()) {
            try {
                d = drivers.nextElement();
                DriverManager.deregisterDriver(d);
                mLog.info(String.format("Driver %s deregistered", d));
            }
            catch (SQLException ex) {
                mLog.log(Level.SEVERE, String.format("Error deregistering driver %s", d), ex);
            }
        }

        // Close mysql connection cleanup thread
        try {
            AbandonedConnectionCleanupThread.shutdown();
        }
        catch (InterruptedException e) {
            mLog.log(Level.SEVERE, e.getMessage(), e);
        }
    }

//    @Aspect
//    public class DAOInterceptor {
//        private Logger log = Logger.getLogger(DAOInterceptor.class.getName());
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
