package org.deku.leoz.node.config

import org.h2.jdbcx.JdbcConnectionPool
import org.h2.jdbcx.JdbcDataSource
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource
import org.springframework.context.annotation.*
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.jooq.SQLDialect
import org.jooq.conf.Settings
import org.jooq.conf.StatementType
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultDSLContext
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import java.io.File
import java.util.*
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject
import javax.persistence.Entity
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
open class PersistenceConfiguration {
    companion object {
        const val QUALIFIER = "db_embedded"
        const val QUALIFIER_JOOQ = "db_embedded_jooq"
    }

    private val log = LoggerFactory.getLogger(PersistenceConfiguration::class.java.name)

    private val showSql = false

    @Bean
    @FlywayDataSource
    @Qualifier(QUALIFIER)
    open fun dataSource(): DataSource {
        // Dev/debug flag for enabling h2 in memory database
        val H2_IN_MEMORY = false

        // Base URI
        val baseUri: String = if (!H2_IN_MEMORY) {
            "jdbc:h2:file:${StorageConfiguration.instance.h2DatabaseFile}"
        } else {
            "jdbc:h2:mem:db1"
        }

        //region H2 setup
        val params = HashMap<String, String>()

        // Even though this is declared as "experimental" for h2 (http://www.h2database.com/javadoc/org/h2/engine/DbSettings.html#DATABASE_TO_UPPER)
        // Lowercase table/column names or much better readable in queries and schema migrations
        params.put("DATABASE_TO_UPPER", "false")

        // In-memory specific settings
        if (H2_IN_MEMORY) {
            params.put("INIT", "CREATE SCHEMA IF NOT EXISTS leoz")
            params.put("DB_CLOSE_DELAY", "-1")
        }
        //endregion

        // Build url and setup data source
        val dataSource = JdbcDataSource()

        dataSource.setUrl(baseUri +
                params
                        .map { x -> "${x.key}=${x.value}" }
                        .joinToString(separator = ";", prefix = ";"))

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

        //region Setup eclipselink
        val eclipseLinkProperties = Properties()
        eclipseLinkProperties.setProperty("eclipselink.target-database", "org.eclipse.persistence.platform.database.H2Platform")

        //region Dev/debug code for automatically generating database from jpa entites
//        eclipseLinkProperties.setProperty("javax.persistence.schema-generation.database.action", "create")
//        eclipseLinkProperties.setProperty("javax.persistence.schema-generation.create-database-schemas", "true")
        //endregion

        //region Dev/debug code for letting eclipselink/jpa generate DDL/SQL from entites
        if (false) {
            val sqlFile = File("sql/leoz-ddl.sql")
            File(sqlFile.getParent()).mkdirs()
            eclipseLinkProperties.setProperty("javax.persistence.schema-generation.scripts.action", "create")
            eclipseLinkProperties.setProperty("javax.persistence.schema-generation.scripts.create-target", sqlFile.toString())
            eclipseLinkProperties.setProperty("eclipselink.ddlgen-terminate-statements", "true")
        }
        //endregion

        //region Caching
        eclipseLinkProperties.setProperty("eclipselink.cache.shared.default", "true")
//        eclipseLinkProperties.setProperty("javax.persistence.sharedCache.mode", "ENABLE_SELECTIVE");
        //endregion

        // Some master tables may have zero id values
        eclipseLinkProperties.setProperty("eclipselink.allow-zero-id", "true")

        // Enable jdbc batch writing
        eclipseLinkProperties.setProperty("eclipselink.jdbc.batch-writing", "jdbc")
        eclipseLinkProperties.setProperty("eclipselink.jdbc.bind-parameters", "true")
        eclipseLinkProperties.setProperty("eclipselink.jdbc.cache-statements", "true")

        // Weaving is required for lazy loading (amongst other features). Requires a LoadTimeWeaver to be setup (may require -javaagent as JVMARGS depending on setup)
        eclipseLinkProperties.setProperty("eclipselink.weaving", "static")

        if (showSql) {
            // Show SQL
            eclipseLinkProperties.setProperty("eclipselink.logging.level.sql", "FINE")
            eclipseLinkProperties.setProperty("eclipselink.logging.parameters", "true")
        }

        //region Entity setup
        // Scan and iterate entity classes
        val scanner = ClassPathScanningCandidateComponentProvider(false)
        scanner.addIncludeFilter(AnnotationTypeFilter(Entity::class.java))

        for (bd in scanner.findCandidateComponents(org.deku.leoz.node.data.Package.name)) {
            // Setup event listeners for all entity classes
            eclipseLinkProperties.setProperty("eclipselink.descriptor.customizer.${bd.beanClassName}", "org.deku.leoz.node.data.Customizer")
        }
        //endregion

        em.setJpaProperties(eclipseLinkProperties)

        return em
    }
    //endregion


    //region JOOQ
    @Inject
    private lateinit var jooqTransactionAwareDataSource: TransactionAwareDataSourceProxy

    @Inject
    private lateinit var jooqConnectionProvider: DataSourceConnectionProvider

    @Bean
    open fun jooqTransactionAwareDataSourceProxy(): TransactionAwareDataSourceProxy {
        return TransactionAwareDataSourceProxy(dataSource())
    }

    @Bean
    @Qualifier(QUALIFIER_JOOQ)
    open fun jooqTransactionManager(): DataSourceTransactionManager {
        return DataSourceTransactionManager(dataSource())
    }

    @Bean
    open fun jooqConnectionProvider(): DataSourceConnectionProvider {
        return DataSourceConnectionProvider(jooqTransactionAwareDataSource)
    }

    @Bean
    open fun dslContext(): DefaultDSLContext {
        val settings = Settings().withStatementType(StatementType.PREPARED_STATEMENT)
        return DefaultDSLContext(jooqConnectionProvider, SQLDialect.H2, settings)
    }
    //endregion

    @PostConstruct
    open fun onInitialize() {
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

