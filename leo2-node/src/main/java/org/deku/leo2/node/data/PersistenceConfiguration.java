package org.deku.leo2.node.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.node.LocalStorage;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.File;
import java.util.Properties;

/**
 * Embedded database persistence context
 */
@Configuration(PersistenceConfiguration.DB_EMBEDDED)
@ComponentScan(lazyInit = true)
@EnableTransactionManagement(mode = AdviceMode.PROXY, proxyTargetClass = true)
@EnableJpaRepositories(considerNestedRepositories = false)
public class PersistenceConfiguration implements DisposableBean /*, TransactionManagementConfigurer*/ {
    public static final String DB_EMBEDDED = "db_embedded";
    private Log mLog = LogFactory.getLog(PersistenceConfiguration.class.getName());

    private boolean mShowSql = false;

    @Bean
    @FlywayDataSource
    @Qualifier(DB_EMBEDDED)
    public DataSource dataSource() {
        final boolean IN_MEMORY = false;

        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        File dbPath = new File(LocalStorage.instance().getDataDirectory(), "h2/leo2");

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

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        // TODO. more robust behaviour when database is down.
        // eg. webservice fails when database is unreachable (on startup eg.)
        // more tests required referring to db outages during runtime
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(PersistenceConfiguration.class.getPackage().getName());

        // Setup specific jpa vendor adaptor
        JpaVendorAdapter vendorAdapter = new EclipseLinkJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Properties eclipseLinkProperties = new Properties();

        eclipseLinkProperties.setProperty("eclipselink.target-database", "org.eclipse.persistence.platform.database.H2Platform");
        // Automatic schema generation from jpa entites
        eclipseLinkProperties.setProperty("javax.persistence.schema-generation.database.action", "create");
        eclipseLinkProperties.setProperty("javax.persistence.schema-generation.create-database-schemas", "true");

        //region DDL script generation configuration
        // TODO: preliminary (demo) code to let eclipselink/jpa generate sql from entites
        // should be integrated as a build task
        if (false) {
            File sqlFile = new File("sql/leo2-ddl.sql");
            new File(sqlFile.getParent()).mkdirs();
            eclipseLinkProperties.setProperty("javax.persistence.schema-generation.scripts.action", "create");
            eclipseLinkProperties.setProperty("javax.persistence.schema-generation.scripts.create-target", sqlFile.toString());
            eclipseLinkProperties.setProperty("eclipselink.ddlgen-terminate-statements", "true");
        }
        //endregion

        // Some master tables may have zero id values
        eclipseLinkProperties.setProperty("eclipselink.allow-zero-id", "true");

        eclipseLinkProperties.setProperty("eclipselink.jdbc.batch-writing", "jdbc");
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

