package org.deku.leo2.central;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.inject.Inject;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Central database persistence context
 * Created by masc on 28.08.14.
 */
@Configuration(PersistenceContext.DB_CENTRAL)
@Import(org.deku.leo2.node.PersistenceContext.class)
@EnableTransactionManagement(mode = AdviceMode.PROXY, proxyTargetClass = true)
public class PersistenceContext implements DisposableBean {
    public static final String DB_CENTRAL = "db_central";

    private Logger mLog = Logger.getLogger(PersistenceContext.class.getName());


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
            } catch (SQLException ex) {
                mLog.log(Level.SEVERE, String.format("Error deregistering driver %s", d), ex);
            }
        }

        // Close mysql connection cleanup thread
        try {
            AbandonedConnectionCleanupThread.shutdown();
        } catch (InterruptedException e) {
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

