package org.deku.leo2.central;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AdviceMode;
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
import java.io.File;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by masc on 28.08.14.
 */
public class PersistenceContext {
    private Logger mLog = Logger.getLogger(PersistenceContext.class.getName());

    public static final String DB_EMBEDDED = "db_embedded";
    public static final String DB_CENTRAL = "db_central";

    /**
     * Central database persistence context
     */
    @Configuration
    //@EnableTransactionManagement(proxyTargetClass = true)
    public static class Central implements DisposableBean {
        private Logger mLog = Logger.getLogger(PersistenceContext.Central.class.getName());

        @Inject
        @Qualifier(DB_CENTRAL)
        private AbstractDataSource mDataSource;

        @Bean
        @Lazy
        @Qualifier(DB_CENTRAL)
        public AbstractDataSource dataSourceCentral() {
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

        @Inject
        private TransactionAwareDataSourceProxy mJooqTransactionAwareDataSource;

        @Inject
        private DataSourceConnectionProvider mJooqConnectionProvider;

        @Bean
        public TransactionAwareDataSourceProxy jooqTransactionAwareDataSourceProxy() {
            return new TransactionAwareDataSourceProxy(mDataSource);
        }

        @Bean
        @Qualifier(DB_CENTRAL)
        public DataSourceTransactionManager jooqTransactionManager() {
            return new DataSourceTransactionManager(mDataSource);
        }

        @Bean
        public DataSourceConnectionProvider jooqConnectionProvider() {
            return new DataSourceConnectionProvider(mJooqTransactionAwareDataSource);
        }

        @Bean
        public DefaultDSLContext dslContext() {
            return new DefaultDSLContext(mJooqConnectionProvider, SQLDialect.MYSQL);
        }

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
    }

    /**
     * Embedded database persistence context
     */
    @Configuration
    @EnableTransactionManagement(mode = AdviceMode.PROXY, proxyTargetClass = false)
    @EnableJpaRepositories(considerNestedRepositories = true)
    public static class Embedded implements DisposableBean /*, TransactionManagementConfigurer*/ {
        private Logger mLog = Logger.getLogger(PersistenceContext.Embedded.class.getName());

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

            File dbPath = new File(Main.instance().getLocalHomeDirectory(), "db/leo2");

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
            em.setPackagesToScan(new String[]{
                    "org.deku.leo2.central.data.entities"
            });

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
