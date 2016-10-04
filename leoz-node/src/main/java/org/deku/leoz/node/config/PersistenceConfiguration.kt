package org.deku.leoz.node.config

import org.deku.leoz.node.data.entities.*
import org.h2.jdbcx.JdbcConnectionPool
import org.h2.jdbcx.JdbcDataSource
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource
import org.springframework.context.annotation.AdviceMode
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import sx.io.serialization.Serializer
import java.io.File
import java.util.*
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

/**
 * Leoz-node database persistence context
 * Created by masc on 24-Jul-15.
 */
@Configuration(PersistenceConfiguration.QUALIFIER)
@ComponentScan(lazyInit = true, basePackageClasses = arrayOf(org.deku.leoz.node.data.Package::class))
@EnableTransactionManagement(mode = AdviceMode.PROXY, proxyTargetClass = true)
@EnableJpaRepositories(considerNestedRepositories = false, basePackageClasses = arrayOf(org.deku.leoz.node.data.Package::class))
open class PersistenceConfiguration /*, TransactionManagementConfigurer*/ {
    companion object {
        const val QUALIFIER = "db_embedded"
    }

    private val log = LoggerFactory.getLogger(PersistenceConfiguration::class.java.name)

    private val showSql = false

    @Bean
    @FlywayDataSource
    @Qualifier(QUALIFIER)
    open fun dataSource(): DataSource {
        val IN_MEMORY = false

        val dataSource = JdbcDataSource()

        // Base URI
        val baseUri: String
        if (!IN_MEMORY) {
            baseUri = "jdbc:h2:file:" + StorageConfiguration.instance.h2DatabaseFile
        } else {
            baseUri = "jdbc:h2:mem:db1"
        }

        // H2 parameters
        val params = HashMap<String, String>()
        // Even though this is declared as "experimental" for h2 (http://www.h2database.com/javadoc/org/h2/engine/DbSettings.html#DATABASE_TO_UPPER)
        // Uppercase table/column names are just ugly.
        params.put("DATABASE_TO_UPPER", "false")
        if (IN_MEMORY) {
            // For in memory db
            params.put("INIT", "CREATE SCHEMA IF NOT EXISTS leoz")
            params.put("DB_CLOSE_DELAY", "-1")
        }

        dataSource.setUrl(baseUri + params.map { x -> "${x.key}=${x.value}" }.joinToString(separator = ";", prefix = ";"))

        return JdbcConnectionPool.create(dataSource)
    }

    //region JPA
    @Bean
    @Qualifier(QUALIFIER)
    open fun transactionManager(emf: EntityManagerFactory): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = emf
        transactionManager.dataSource = this.dataSource()

        return transactionManager
    }

    @Bean
    open fun entityManagerFactory(): LocalContainerEntityManagerFactoryBean {
        // TODO. more robust behaviour when database is down.
        // eg. webservice fails when database is unreachable (on startup eg.)
        // more tests required referring to db outages during runtime
        val em = LocalContainerEntityManagerFactoryBean()
        em.dataSource = this.dataSource()
        em.setPackagesToScan(org.deku.leoz.node.data.Package.name)

        // Setup specific jpa vendor adaptor
        val vendorAdapter = EclipseLinkJpaVendorAdapter()
        em.jpaVendorAdapter = vendorAdapter

        val eclipseLinkProperties = Properties()

        eclipseLinkProperties.setProperty("eclipselink.target-database", "org.eclipse.persistence.platform.database.H2Platform")
        // Automatic schema generation from jpa entites
//        eclipseLinkProperties.setProperty("javax.persistence.schema-generation.database.action", "create")
//        eclipseLinkProperties.setProperty("javax.persistence.schema-generation.create-database-schemas", "true")
        // Caching
        //        eclipseLinkProperties.setProperty("javax.persistence.sharedCache.mode", "ENABLE_SELECTIVE");

        //region DDL script generation configuration
        // TODO: preliminary (demo) code to let eclipselink/jpa generate sql from entites
        // should be integrated as a build task
        if (false) {
            val sqlFile = File("sql/leoz-ddl.sql")
            File(sqlFile.getParent()).mkdirs()
            eclipseLinkProperties.setProperty("javax.persistence.schema-generation.scripts.action", "create")
            eclipseLinkProperties.setProperty("javax.persistence.schema-generation.scripts.create-target", sqlFile.toString())
            eclipseLinkProperties.setProperty("eclipselink.ddlgen-terminate-statements", "true")
        }
        //endregion

        // Some master tables may have zero id values
        eclipseLinkProperties.setProperty("eclipselink.allow-zero-id", "true")

        eclipseLinkProperties.setProperty("eclipselink.jdbc.batch-writing", "jdbc")
        eclipseLinkProperties.setProperty("eclipselink.weaving", "false")
        eclipseLinkProperties.setProperty("eclipselink.cache.shared.default", "true")

        if (showSql) {
            // Show SQL
            eclipseLinkProperties.setProperty("eclipselink.logging.level.sql", "FINE")
            eclipseLinkProperties.setProperty("eclipselink.logging.parameters", "true")
        }

        em.setJpaProperties(eclipseLinkProperties)

        return em
    }
    //endregion


    @PostConstruct
    open fun onInitialize() {
        // Register entities
        Serializer.types.register(MstCountry::class.java, 0x61175aa6b510b3L)
        Serializer.types.register(MstHolidayCtrl::class.java, 0x56c4d6ff7b69dcL)
        Serializer.types.register(MstHolidayCtrlId::class.java, 0x0bef5f538e4ed9L)
        Serializer.types.register(MstRoute::class.java, 0xef6d8232d3263dL)
        Serializer.types.register(MstRoutingLayer::class.java, 0x92ed6a2fc3f79fL)
        Serializer.types.register(MstSector::class.java, 0xf1dec2bb66db87L)
        Serializer.types.register(MstSectorId::class.java, 0xd432b301532a4aL)
        Serializer.types.register(MstStation::class.java, 0x1f6229f711c472L)
        Serializer.types.register(MstStationSector::class.java, 0x0d1eaebfd81899L)
        Serializer.types.register(MstStationSectorId::class.java, 0x4a81cc447bdc43L)
        Serializer.types.register(SysProperty::class.java, 0xaa946790064006L)
        Serializer.types.register(SysPropertyId::class.java, 0x72b4356618387dL)
    }

    @PreDestroy
    open fun onDestroy() {

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

