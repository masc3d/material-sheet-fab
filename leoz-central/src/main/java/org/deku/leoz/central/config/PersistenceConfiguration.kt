package org.deku.leoz.central.config

import com.mysql.jdbc.AbandonedConnectionCleanupThread
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.jooq.SQLDialect
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultDSLContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.*
import org.springframework.jdbc.datasource.AbstractDataSource
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import org.springframework.transaction.annotation.EnableTransactionManagement

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject
import java.sql.Driver
import java.sql.DriverManager
import java.sql.SQLException
import java.util.Enumeration
import java.util.Properties

/**
 * Leoz-central database persistence configuration
 * Created by masc on 28.08.14.
 */
@Configuration(PersistenceConfiguration.DB_CENTRAL)
@ComponentScan(
        lazyInit = true,
        basePackageClasses = arrayOf(org.deku.leoz.central.data.Package::class))
@Import(org.deku.leoz.node.config.PersistenceConfiguration::class)
@EnableConfigurationProperties
@EnableTransactionManagement(mode = AdviceMode.PROXY, proxyTargetClass = true)
open class PersistenceConfiguration {
    companion object {
        const val DB_CENTRAL = "db_central"
    }

    private val log = LogFactory.getLog(PersistenceConfiguration::class.java)

    // TODO: tomcat deployment breaks with circular dependency when wither dataSourceCentral()
    // or jooqTransactionAwareDataSourceProxy() are not @Lazy.
    // This works perfectly fine when running standalone.
    @Bean
    @Qualifier(DB_CENTRAL)
    @ConfigurationProperties(prefix = "datasource.central")
    open fun dataSourceCentral(): AbstractDataSource {
        // When running within tomcat, spring can't figure out the driver type (even though mysql is
        // part of jdbc url)

        val dataSource = DataSourceBuilder.create()
                .driverClassName("com.mysql.jdbc.Driver")
                .type(DriverManagerDataSource::class.java)
                .build() as DriverManagerDataSource

        // TODO: figure out how to get those into application.properties
        val dataSourceProperties = Properties()
        dataSourceProperties.setProperty("zeroDateTimeBehavior", "convertToNull")
        dataSourceProperties.setProperty("connectTimeout", "1000")
        dataSource.setConnectionProperties(dataSourceProperties)

        return dataSource
    }

    @Inject
    private val jooqTransactionAwareDataSource: TransactionAwareDataSourceProxy? = null

    @Inject
    private val jooqConnectionProvider: DataSourceConnectionProvider? = null

    // TODO: tomcat breakage without @Lazy. see above (dataSourceCentral())
    @Bean
    open fun jooqTransactionAwareDataSourceProxy(): TransactionAwareDataSourceProxy {
        return TransactionAwareDataSourceProxy(dataSourceCentral())
    }

    @Bean
    @Qualifier(DB_CENTRAL)
    open fun jooqTransactionManager(): DataSourceTransactionManager {
        return DataSourceTransactionManager(dataSourceCentral())
    }

    @Bean
    open fun jooqConnectionProvider(): DataSourceConnectionProvider {
        return DataSourceConnectionProvider(jooqTransactionAwareDataSource)
    }

    @Bean
    open fun dslContext(): DefaultDSLContext {
        return DefaultDSLContext(jooqConnectionProvider, SQLDialect.MYSQL)
    }

    @PostConstruct
    fun onInitialize() {
        // Disable JOOQ logo
        System.setProperty("org.jooq.no-logo", "true")
    }

    @PreDestroy
    @Throws(Exception::class)
    fun onDestroy() {
        log.info("Cleaning up persistence context")

        // Close all JDBC drivers
        val drivers = DriverManager.getDrivers()
        var d: Driver? = null
        while (drivers.hasMoreElements()) {
            try {
                d = drivers.nextElement()
                DriverManager.deregisterDriver(d)
                log.info("Driver [${d}] deregistered")
            } catch (ex: SQLException) {
                log.error("Error deregistering driver [${d}]", ex)
            }

        }

        // Close mysql connection cleanup thread
        try {
            AbandonedConnectionCleanupThread.shutdown()
        } catch (e: InterruptedException) {
            log.error(e.message, e)
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

