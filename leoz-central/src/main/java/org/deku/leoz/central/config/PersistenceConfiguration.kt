package org.deku.leoz.central.config

import com.mysql.jdbc.AbandonedConnectionCleanupThread
import org.apache.commons.dbcp2.BasicDataSource
import org.flywaydb.core.Flyway
import org.jooq.SQLDialect
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultDSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.*
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.sql.Driver
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.sql.DataSource

/**
 * Persistence configuration for leoz-central
 * Created by masc on 28.08.14.
 */
@Configuration(PersistenceConfiguration.QUALIFIER)
@ComponentScan(
        lazyInit = true,
        basePackageClasses = arrayOf(org.deku.leoz.central.data.Package::class))
@Import(org.deku.leoz.node.config.PersistenceConfiguration::class)
@EnableConfigurationProperties
@EnableTransactionManagement(mode = AdviceMode.PROXY, proxyTargetClass = true)
open class PersistenceConfiguration {
    companion object {
        const val QUALIFIER = "db_central"
    }

    private val log = LoggerFactory.getLogger(PersistenceConfiguration::class.java)

    @get:Bean
    @get:Qualifier(QUALIFIER)
    @get:ConfigurationProperties(prefix = "persistence.central.datasource")
    open val dataSourceCentral: DataSource
        get() {
            val basicDataSource = BasicDataSource()
            basicDataSource.driverClassName = com.mysql.jdbc.Driver::class.java.canonicalName

            val dataSourceProperties = Properties()
            dataSourceProperties.setProperty("zeroDateTimeBehavior", "convertToNull")
            dataSourceProperties.setProperty("connectTimeout", "1000")

            basicDataSource.setConnectionProperties(
                    dataSourceProperties.map { e -> "${e.key}=${e.value}" }.joinToString(";")
            )

            return basicDataSource
        }

    @get:Bean
    @get:Qualifier(QUALIFIER)
    open val jooqCentralTransactionAwareDataSourceProxy: TransactionAwareDataSourceProxy
        get() {
            return TransactionAwareDataSourceProxy(this.dataSourceCentral)
        }

    @get:Bean
    @get:Qualifier(QUALIFIER)
    open val jooqCentralTransactionManager: DataSourceTransactionManager
        get() {
            return DataSourceTransactionManager(this.dataSourceCentral)
        }

    @get:Bean
    @get:Qualifier(QUALIFIER)
    open val jooqCentralConnectionProvider: DataSourceConnectionProvider
        get() {
            return DataSourceConnectionProvider(this.jooqCentralTransactionAwareDataSourceProxy)
        }

    @get:Bean
    @get:Qualifier(QUALIFIER)
    open val centralDslContext: DefaultDSLContext
        get() {
            return DefaultDSLContext(this.jooqCentralConnectionProvider, SQLDialect.MYSQL)
        }

    @PostConstruct
    fun onInitialize() {
        // Disable JOOQ logo
        System.setProperty("org.jooq.no-logo", "true")

        log.info("Validating central schema")
        val flyway = Flyway()
        flyway.dataSource = this.dataSourceCentral
        flyway.setLocations("classpath:/db/central/migration")
        flyway.isIgnoreFutureMigrations = false
        flyway.validate()
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

    // TODO: example: aspect/DAOInterceptor
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

